package dk.christensen.garbagefinalassignment.ui.Features.garbage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.christensen.garbagefinalassignment.ui.theme.GarbageFinalTheme
import kotlinx.serialization.Serializable

@Serializable
data class AddWhere(val what: String)

@Composable
fun AddWhereScreen(
    onNavigate: (AddWhereViewModel.NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddWhereViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.navigationEvents.collect {
            onNavigate(it)
        }
    }

    AddWhereScreen(
        modifier = modifier,
        uiState = uiState,
        uiEvents = viewModel.uiEvents
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddWhereScreen(
    uiState: AddWhereViewModel.UiState,
    uiEvents: AddWhereViewModel.UiEvents,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Add item: Where") },
                navigationIcon = {
                    IconButton(onClick = uiEvents::onUpClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val focusManager = LocalFocusManager.current

            TextField(
                value = uiState.where,
                onValueChange = uiEvents::onWhereChange,
                label = { Text(text = "Where") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                isError = uiState.isError,
                supportingText = {
                    if (uiState.isError) {
                        Text(text = "Where cannot be empty")
                    }
                }
            )

            Spacer(Modifier.height(height = 16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // TODO add Cancel button

                Button(onClick = uiEvents::onDoneClick) {
                    Text(text = "Done")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddWhereScreenPreview() {
    GarbageFinalTheme {
        AddWhereScreen(
            modifier = Modifier,
            uiState = AddWhereViewModel.UiState(where = "Dairy"),
            uiEvents = object : AddWhereViewModel.UiEvents {
                override fun onWhereChange(where: String) {}
                override fun onDoneClick() {}
                override fun onUpClick() {}
                override fun onCancelClick() {}
            }
        )
    }
}