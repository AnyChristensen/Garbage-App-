package dk.christensen.garbagefinalassignment.ui.Features.recycling

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import dk.christensen.garbagefinalassignment.R
import dk.christensen.garbagefinalassignment.domain.Bin
import dk.christensen.garbagefinalassignment.ui.components.BinProvider
import dk.christensen.garbagefinalassignment.ui.components.ThemedPreviews
import dk.christensen.garbagefinalassignment.ui.theme.GarbageFinalTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BinDetailsSheet(bin: Bin, onDismiss: () -> Unit, onTrackRecycling: (Bin) -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onDismiss()
                }
            }
        },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Bin name
            Text(text = bin.name, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(height = 16.dp))
            // Track recycling button
            Button(
                onClick = {onTrackRecycling(bin)}
            ){
                Text(text = stringResource(R.string.track_recycle_button),
                    style = MaterialTheme.typography.labelLarge)
            }
            Spacer(modifier = Modifier.height(height = 16.dp))
            // "Counter" since last emptied
            Text(
                text = getTimeSinceLastPickup(bin.lastPickupTime),
                style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(height = 16.dp))
            // Description
            Text(
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


// Helper function to calculate the time since the bin has been emptied
@Composable
fun getTimeSinceLastPickup(lastPickupTime: Long?): String {
    if (lastPickupTime == null) return stringResource(R.string.bin_not_emptied)

    val diff = System.currentTimeMillis() - lastPickupTime
    val days = diff / (1000 * 60 * 60 * 24)

    return when {
        days == 0L -> stringResource(R.string.bin_emptied_today)
        days == 1L -> stringResource(R.string.bin_emptied_one_day_ago)
        else -> stringResource(R.string.bin_emptied_days_ago, days)
    }
}



@ThemedPreviews
@Composable
private fun BinDetailsSheetPreview(@PreviewParameter(provider = BinProvider::class) bin: Bin) {
    GarbageFinalTheme() {
        BinDetailsSheet(bin = bin, onDismiss = {}, onTrackRecycling= {})
    }
}

