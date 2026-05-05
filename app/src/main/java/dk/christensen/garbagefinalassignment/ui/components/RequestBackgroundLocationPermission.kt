package dk.christensen.garbagefinalassignment.ui.components

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import dk.christensen.garbagefinalassignment.R

@Composable
fun RequestBackgroundLocationPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    // Before Android 10 (Q), background location was granted automatically with foreground
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        LaunchedEffect(key1 = Unit) { onPermissionGranted() }
        return
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) onPermissionGranted() else onPermissionDenied()
        }
    )

    // The Prominent Disclosure Dialog (Required by Google Play Policy)
    AlertDialog(
        onDismissRequest = { onPermissionDenied() },
        title = { Text(text = stringResource(id = R.string.background_location_permission_title)) },
        text = {
            Text(text = stringResource(id = R.string.background_location_permission_rationale))
        },
        confirmButton = {
            TextButton(onClick = {
                permissionLauncher.launch(input = Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }) {
                Text(text = stringResource(id = R.string.go_to_settings_button_label))
            }
        },
        dismissButton = {
            TextButton(onClick = { onPermissionDenied() }) {
                Text(text = stringResource(id = R.string.no_thanks_button_label))
            }
        }
    )
}