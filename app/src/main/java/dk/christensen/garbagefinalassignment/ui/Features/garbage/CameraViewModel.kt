package dk.christensen.garbagefinalassignment.ui.Features.garbage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.christensen.garbagefinalassignment.domain.Item
import dk.christensen.garbagefinalassignment.domain.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(private val repository: ItemRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val uiEvents: UiEvents = object : UiEvents {

        // function that is called when a item/label is detected
        override fun onLabelDetected(label: String) {

            viewModelScope.launch {

                // Search Firestore for matching garbage item
                val item = repository.searchItem(label)

                // updating the ui state
                _uiState.update { it.copy(detectedItem = item) }
            }
        }
    }


    // to store the item matched from the model's scanning/detection
    data class UiState (val detectedItem: Item? = null)

    interface UiEvents {fun onLabelDetected(label: String)}

}


