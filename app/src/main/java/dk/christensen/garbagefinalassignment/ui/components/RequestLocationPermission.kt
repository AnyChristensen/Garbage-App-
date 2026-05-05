package dk.christensen.garbagefinalassignment.ui.components

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dk.christensen.garbagefinalassignment.R

// Helper function to safely get the Activity from the Compose Context
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun RequestLocationPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    // State to control when to show our custom rationale dialog
    var showRationale by remember { mutableStateOf(value = false) }

    // 1. Create the launcher for multiple permissions (Fine + Coarse)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            // Geofencing usually requires fine location, so we check for that.
            val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

            if (isGranted) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }
    )

    // 2. Launch the check exactly once when this Composable enters the composition
    LaunchedEffect(key1 = Unit) {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            /* context = */ context, /* permission = */ Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted) {
            onPermissionGranted()
        } else {
            // Check if we should show the rationale
            val needsRationale = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    /* activity = */ it,
                    /* permission = */ Manifest.permission.ACCESS_FINE_LOCATION
                )
            } ?: false

            if (needsRationale) {
                showRationale = true
            } else {
                // First time asking or user previously selected "Don't ask again"
                permissionLauncher.launch(
                    input = arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    // 3. Show the rationale dialog if needed
    if (showRationale) {
        AlertDialog(
            onDismissRequest = {
                showRationale = false
                onPermissionDenied()
            },
            title = { Text(text = stringResource(id = R.string.location_permission_title)) },
            text = { Text(text = stringResource(id = R.string.location_permission_rationale)) },
            confirmButton = {
                TextButton(onClick = {
                    showRationale = false
                    // Ask for the permission after user reads the rationale
                    permissionLauncher.launch(
                        input = arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }) {
                    Text(text = stringResource(id = R.string.continue_button_label))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRationale = false
                    onPermissionDenied()
                }) {
                    Text(text = stringResource(id = R.string.no_thanks_button_label))
                }
            }
        )
    }
}