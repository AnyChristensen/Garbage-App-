package dk.christensen.garbagefinalassignment.ui.components

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dk.christensen.garbagefinalassignment.R

enum class NavigationType {
    BACK,
    CLOSE,
    NONE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GarbageTopAppBar(
    @StringRes titleRes: Int,
    navigationType: NavigationType,
    onUpClick: () -> Unit = {},
) {
    TopAppBar(
        title = { Text(text = stringResource(id = titleRes)) },
        navigationIcon = {
            when (navigationType) {
                NavigationType.BACK -> {
                    IconButton(onClick = onUpClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.up_button_label)
                        )
                    }
                }
                NavigationType.CLOSE -> {
                    IconButton(onClick = onUpClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.close_button_label)
                        )
                    }
                }
                NavigationType.NONE -> { /* No icon */ }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}