package dk.christensen.garbagefinalassignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.christensen.garbagefinalassignment.domain.UserPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val targetTime: StateFlow<Long?> = preferencesRepository.targetTime
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val remainingMillis: StateFlow<Long> = targetTime
        .flatMapLatest { target ->
            flow {
                if (target != null && target > 0L) {
                    var remaining = target - System.currentTimeMillis()
                    while (remaining > 0) {
                        emit(value = remaining) // Push the new time to the UI
                        delay(timeMillis = 1000)     // Wait 1 second
                        remaining = target - System.currentTimeMillis()
                    }
                    emit(value = 0L) // Ensure we explicitly emit 0 when finished
                } else {
                    emit(value = 0L)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = 0L
        )

    init {
        viewModelScope.launch {
            val savedTargetTime = preferencesRepository.targetTime.firstOrNull() ?: 0L
            val now = System.currentTimeMillis()

            if (savedTargetTime < now) {
                val newTarget = now + (1 * 60 * 1000)
                preferencesRepository.setTargetTime(newTarget)
            }
        }
    }
}