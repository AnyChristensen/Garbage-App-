package dk.christensen.garbagefinalassignment.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dk.christensen.garbagefinalassignment.R
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun GlobalCountdownBanner(
    remainingMillis: Long,
    modifier: Modifier = Modifier
) {
    if (remainingMillis > 0) {
        val duration = remainingMillis.milliseconds
        val formattedTime = duration.toComponents { _, minutes, seconds, _ ->
            String.format(locale = Locale.getDefault(), format = "%02d:%02d", minutes, seconds)
        }

        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.tertiaryContainer)
                .windowInsetsPadding(insets = WindowInsets.statusBars)
                .padding(all = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.count_down_description, formattedTime),
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}