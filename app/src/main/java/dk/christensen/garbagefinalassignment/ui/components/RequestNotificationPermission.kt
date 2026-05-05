package dk.christensen.garbagefinalassignment.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dk.christensen.garbagefinalassignment.core.workers.WelcomeNotificationWorker
import java.util.concurrent.TimeUnit
import androidx.core.content.ContextCompat



@Composable
fun RequestNotificationPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit) {

    // We only need to ask on Android 13 (TIRAMISU) and above
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

        val context = LocalContext.current

        // 1. Create the launcher that handles the result
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (isGranted) {
                    onPermissionGranted()
                } else {
                    // The user denied it. You could optionally show a Snackbar here
                    // explaining why the app needs notifications.
                    onPermissionDenied()
                }
            }
        )

            // 2. Launch the request exactly once when this Composable enters the composition
        LaunchedEffect(key1 = Unit) {
            val isGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (isGranted) {
                onPermissionGranted()
            } else {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }

        }
    }
}