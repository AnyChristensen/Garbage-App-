    package dk.christensen.garbagefinalassignment.ui.Features.garbage

    // The View Model which act as the "brain" of the UI (GarbageListScreen)

    import android.util.Log
    import androidx.lifecycle.SavedStateHandle
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import androidx.navigation.toRoute
    import dagger.hilt.android.lifecycle.HiltViewModel
    import dk.christensen.garbagefinalassignment.domain.BinRepository
    import dk.christensen.garbagefinalassignment.domain.Bin
    import dk.christensen.garbagefinalassignment.domain.Item
    import dk.christensen.garbagefinalassignment.domain.ItemRepository
    import kotlinx.coroutines.flow.MutableSharedFlow
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.flow.asSharedFlow
    import kotlinx.coroutines.flow.combine
    import kotlinx.coroutines.flow.update
    import kotlinx.coroutines.launch
    import javax.annotation.concurrent.Immutable
    import javax.inject.Inject
    import kotlinx.coroutines.flow.asStateFlow
    import kotlinx.coroutines.flow.first


    @HiltViewModel
    class GarbageListViewModel @Inject constructor(
        private val itemRepository : ItemRepository,
        private val binRepository: BinRepository,
        savedStateHandle: SavedStateHandle) :
        ViewModel() {
            private val garbageListArgs: GarbageList = savedStateHandle.toRoute()
            private val itemId: String? = garbageListArgs.itemId

            private val _uiState = MutableStateFlow(value = UiState())
            val uiState: StateFlow<UiState> = _uiState.asStateFlow()

            private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
            val navigationEvents = _navigationEvents.asSharedFlow()

        init {

            // Garbage items (prepopulating with existing items if the list is empty)
             viewModelScope.launch {
                itemRepository.getGarbageList().collect { items ->

                    if (items.isEmpty()) {
                        Log.d("TEST", "Inserting items...")
                        itemRepository.insertExistingItems()
                    }

                    _uiState.update {
                        it.copy(garbageList = items)
                    }
                }
            }

            //Bins
            viewModelScope.launch {
                binRepository.getBins().collect { bins ->
                    _uiState.update {
                        it.copy(bins = bins)
                    }
                }
            }

            //Deeplink
            itemId?.let { id ->

                if (id == "deep-link-item") {
                    val deepLinkItem = Item(
                        what = "Deep Link Item",
                        where = "Paper",
                        description = "Opened from deeplink"
                    )

                    _uiState.update {
                        it.copy(selectedItem = deepLinkItem)
                    }

                } else {
                    viewModelScope.launch {
                        itemRepository.getItem(id).collect { item ->
                            _uiState.update {
                                it.copy(selectedItem = item)
                            }
                        }
                    }
                }
            }

}


            val uiEvents: UiEvents = object : UiEvents {

            override fun onAddItemClick() {
                viewModelScope.launch {
                    _navigationEvents.emit(value =
                        NavigationEvent.NavigateToAddWhat)
                }
            }

            override fun onEditItemClick(item: Item) {
                 _uiState.update { it.copy(selectedItem = item) }
                }


            override fun onWhatChange(what: String) {
                _uiState.update { it.copy(selectedItem = it.selectedItem?.copy(what = what)) }
            }


            override fun onWhereChange(where: String) {
                _uiState.update { it.copy(selectedItem = it.selectedItem?.copy(where = where)) }
            }

            override fun onDescriptionChange(description: String) {
                _uiState.update { it.copy(selectedItem = it.selectedItem?.copy(description = description)) }
            }

            override fun onSaveClick(): Boolean {
                val item = _uiState.value.selectedItem ?: return true

                when {
                    item.what.isNotBlank() && item.where.isNotBlank() -> {
                        _uiState.update { it.copy(isWhatError = false, isWhereError = false) }
                        viewModelScope.launch {
                            Log.d("SAVE_ITEM", "Saving item: $item")
                            itemRepository.updateItem(item = item)
                        }
                        onDismissDetails()
                        return true
                    }

                    item.what.isBlank() && item.where.isBlank() ->
                        _uiState.update { it.copy(isWhatError = true, isWhereError = true) }

                    item.what.isBlank() -> _uiState.update { it.copy(isWhatError = true, isWhereError = false) }
                    item.where.isBlank() -> _uiState.update { it.copy(isWhatError = false, isWhereError = true) }
                }

                return false
            }

            // When clicking delete. Should open the confirmation dialog
            override fun onDeleteClick() {
                _uiState.update { it.copy(showDeleteConfirmation = true) }
            }


            // When clicking delete in the dialog confirmation
            // It will also close the dialog overlay therefore call onDismissDetails()
            override fun onConfirmDelete() {
                _uiState.value.selectedItem?.let { item ->
                    viewModelScope.launch {
                        itemRepository.removeItem(item)
                    }
                    onDismissDetails()
                }
            }


            // Cancel bottom. Hiding the dialog by setting showDeleteConfirmation to false
            override fun onDismissDeleteConfirmation() {
                _uiState.update { it.copy(showDeleteConfirmation = false) }
            }


            override fun onDismissDetails() {
                _uiState.update { it.copy(selectedItem = null, showDeleteConfirmation = false) }
            }


            override fun onUpClick() {
                viewModelScope.launch {
                    _navigationEvents.emit(NavigationEvent.NavigateUp)
                }
            }
        }


        /* // Creating function that handles when the user change the input (uiEvent)
         override fun onUserInputChanged(text: String) {
             viewModelScope.launch {
                 _uiState.update {
                     it.copy(
                         userInput = text,
                         foundItem = ""
                     )
                 }
             }
         }*/


        data class UiState(
            val garbageList : List<Item> = emptyList(),
            val bins: List<Bin> = emptyList(),
            val selectedItem: Item? = null,
            val isWhatError: Boolean = false,
            val isWhereError: Boolean = false,
            val showDeleteConfirmation: Boolean = false,
            val userInput: String = "",
            val foundItem: String = ""
        )


        @Immutable
        interface UiEvents {
            fun onAddItemClick()
            fun onEditItemClick(item: Item)
            fun onWhatChange(what: String)
            fun onWhereChange(where: String)
            fun onDescriptionChange(description: String)
            fun onSaveClick(): Boolean
            fun onDeleteClick()
            fun onConfirmDelete()
            fun onDismissDeleteConfirmation()
            fun onDismissDetails()
            fun onUpClick()


            // fun onShowSearchClick()
            // fun onShowListClick()
            // fun onSearchItemClick(input: String)
            // fun onUserInputChanged(text: String)

        }

        sealed class NavigationEvent {
            data object NavigateToAddWhat : NavigationEvent()
            data object NavigateUp : NavigationEvent()
        }

    }