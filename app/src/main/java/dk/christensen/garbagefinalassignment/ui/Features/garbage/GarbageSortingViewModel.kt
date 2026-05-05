package dk.christensen.garbagefinalassignment.ui.Features.garbage

// The View Model which act as the "brain" of the UI (GarbageSortingScreen)

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.christensen.garbagefinalassignment.domain.Item
import dk.christensen.garbagefinalassignment.domain.ItemRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.annotation.concurrent.Immutable
import javax.inject.Inject
import kotlinx.coroutines.flow.asStateFlow



@HiltViewModel
class GarbageSortingViewModel @Inject constructor(private val itemRepository : ItemRepository) :
    ViewModel() {
        private val _uiState = MutableStateFlow(value = UiState())
        val uiState: StateFlow<UiState> = _uiState.asStateFlow()

        private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
        val navigationEvents = _navigationEvents.asSharedFlow()

        init {
            viewModelScope.launch {
                itemRepository.getGarbageList().collect{ newList ->
                    _uiState.update {it.copy(garbageList = newList)}
                }
            }
        }


        val uiEvents: UiEvents = object : UiEvents {

            // Function that navigates to Garbagelist screen
           override fun onShowListClick() {
                viewModelScope.launch {
                    _navigationEvents.emit(value = NavigationEvent.NavigateToGarbageList)
                }
            }

            override fun onSearchItemClick(input: String) {
                if (input.isNotBlank()) {

                    viewModelScope.launch {

                        // Searching for the item that matches userInput
                        val item = itemRepository.searchItem(input)

                        val result = "$input should be placed in: ${item?.where ?: "not found"}"

                            _uiState.update {
                                it.copy(foundItem = result)
                            }
                    }

                        // Using collect to get the value from the flow item (emit)
                        // Response string using the safe call operator (?.) to check for null value
                        // and using the elvis operator as 'else' (if the result is null use "not found")

                }
            }



            // Creating function that handles when the user change the input (uiEvent)
            override fun onUserInputChanged(text: String) {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(
                            userInput = text,
                            foundItem = ""
                        )
                    }
                }
            }

            // Onclick function that navigates to AffaldKbhScreen
            override fun onShowAffaldKbhClick(){
                viewModelScope.launch {
                    _navigationEvents.emit(value = NavigationEvent.NavigateToAffaldKBH)
                }
            }

        }


    data class UiState(
        val garbageList : List<Item> = emptyList(),
        val userInput: String = "",
        val foundItem: String = ""
    )


    @Immutable
    interface UiEvents {

        // fun onShowSearchClick()
        fun onShowListClick()
        fun onSearchItemClick(input: String)
        fun onUserInputChanged(text: String)
        fun onShowAffaldKbhClick()
    }

    sealed class NavigationEvent {
        data object NavigateToGarbageList : NavigationEvent()
        data object NavigateToAffaldKBH : NavigationEvent()
    }

    }


