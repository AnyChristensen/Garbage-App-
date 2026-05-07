package dk.christensen.garbagefinalassignment.ui.Features.garbage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.christensen.garbagefinalassignment.domain.Item
import dk.christensen.garbagefinalassignment.domain.ItemRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(private val repository: ItemRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    val uiEvents: UiEvents = object : UiEvents {

        private var lastDetectionTime = 0L
        private var lastDetectedLabel: String? = null

        // function that is called when a item/label is detected
        override fun onLabelDetected(label: String) {

            val currentTime = System.currentTimeMillis()

            // To avoid constant recomposing, only update UI after 2 sec
            if(currentTime - lastDetectionTime < 2000) return

            // Also to prevent  constant recomposing (rerendering), ignore labels that are the same as the previous
            if (label == lastDetectedLabel) return


            // Updating lastDetectedLabel with the newest label
            lastDetectedLabel = label
            lastDetectionTime = currentTime


            // Show scanning state immediately
            _uiState.update {
                it.copy(detectedLabel = label, isScanning = true)
            }



            viewModelScope.launch {

                // Search database for matching garbage item
                val item = repository.searchItem(label)

                // updating the ui state
                // (but only updating if a new item is detected to prevent constant recomposing (rerendering))
                if (_uiState.value.detectedItem != item) {
                    _uiState.update {
                        it.copy(
                            detectedItem = item,
                            isScanning = false)
                    }
                }
            }
        }

        override fun onUpClick() {
            viewModelScope.launch {
                _navigationEvents.emit(NavigationEvent.NavigateUp)
            }
        }
    }


    // to store item, label og isScanning
    data class UiState (

        // Item match from Database
        val detectedItem: Item? = null,

        // Label detected by the model (ML Kit)
        val detectedLabel: String? = null,

        // Loading/scanning state
        val isScanning: Boolean = false


    )

    interface UiEvents {
        fun onLabelDetected(label: String)
        fun onUpClick()
    }

    sealed class NavigationEvent {
        data object NavigateUp : NavigationEvent()
    }

}


