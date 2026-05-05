package dk.christensen.garbagefinalassignment.ui.Features.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.christensen.garbagefinalassignment.domain.Theme
import dk.christensen.garbagefinalassignment.domain.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

        val uiState: StateFlow<UiState> = userPreferencesRepository.theme
        .map { UiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = UiState()
        )

    val uiEvents: UiEvents = object : UiEvents {
        override fun onSetTheme(theme: Theme) {
            // Save the theme to the DataStore
            Log.d("SettingsViewModel", "Saving theme: $theme")
            viewModelScope.launch {
                userPreferencesRepository.setTheme(theme)
            }
        }
    }

    // using launch because it is a suspence function. Needs to be wrapped in coroutine
    // When the viewmodel is ended the coroutine is suspended, so it is not blocking

    data class UiState(
        val theme: Theme = Theme.SYSTEM
    )

    @Immutable
    interface UiEvents {
        fun onSetTheme(theme: Theme)
    }
}
