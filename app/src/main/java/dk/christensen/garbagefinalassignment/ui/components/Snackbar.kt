package dk.christensen.garbagefinalassignment.ui.components

// Copied from Shopping App
// The Snackbar as a component

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SnackBarHandler @Inject constructor(private val scope: CoroutineScope) {
    val snackBarHostState = SnackbarHostState()

    fun postMessage(
        msg: String,
        actionLabel: String? = null,
        onDismiss: (() -> Unit)? = null,
        onActionClick: (() -> Unit)? = null,
    ) {
        scope.launch {
            val result = snackBarHostState.showSnackbar(
                message = msg,
                actionLabel = actionLabel,
                duration = if (actionLabel != null) SnackbarDuration.Long else SnackbarDuration.Short
            )
            when (result) {
                SnackbarResult.ActionPerformed -> onActionClick?.invoke()
                SnackbarResult.Dismissed -> onDismiss?.invoke()
            }
        }
    }
}