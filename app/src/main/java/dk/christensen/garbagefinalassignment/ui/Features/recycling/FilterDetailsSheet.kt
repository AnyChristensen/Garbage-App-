package dk.christensen.garbagefinalassignment.ui.Features.recycling

import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import dk.christensen.garbagefinalassignment.domain.Bin
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dk.christensen.garbagefinalassignment.ui.components.ThemedPreviews
import dk.christensen.garbagefinalassignment.ui.theme.GarbageFinalTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDetailsSheet(
    bins: List<Bin>,
    clickedBins: List<String>,
    onFilterClick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest =  onDismiss
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
        ) {
            bins.forEach { bin ->
                FilterChip(
                    selected = clickedBins.contains(bin.name),
                    onClick = { onFilterClick(bin.name) },
                    label = { Text(bin.name) }
                )
            }
        }
    }
}


@ThemedPreviews
@Composable
private fun FilterDetailsSheetPreview() {
    GarbageFinalTheme() {
        FilterDetailsSheet(
            bins = listOf(
                Bin( name = "Paper",
                    imageUrl = "",
                    brandColor = Color.Red,
                    lastPickupTime = null),
                Bin(name = "Glass",
                    imageUrl = "",
                    brandColor = Color.Green,
                    lastPickupTime = null)
            ),
            clickedBins = listOf("Paper"),
            onFilterClick = {},
            onDismiss = {}
        )
    }
}

