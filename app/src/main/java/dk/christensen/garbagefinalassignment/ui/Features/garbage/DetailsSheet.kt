package dk.christensen.garbagefinalassignment.ui.Features.garbage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import dk.christensen.garbagefinalassignment.R
import dk.christensen.garbagefinalassignment.domain.Item
import dk.christensen.garbagefinalassignment.ui.components.BooleanProvider
import dk.christensen.garbagefinalassignment.ui.components.GarbageTextField
import dk.christensen.garbagefinalassignment.ui.components.ThemedPreviews
import dk.christensen.garbagefinalassignment.ui.components.DismissButton
import dk.christensen.garbagefinalassignment.ui.theme.GarbageFinalTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsSheet(
    item: Item,
    isWhatError: Boolean,
    isWhereError: Boolean,
    showDeleteConfirmation: Boolean,
    uiEvents: GarbageListViewModel.UiEvents,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val paneTitle = stringResource(id = R.string.edit_item_title)


    if (showDeleteConfirmation) {
        AlertDialog(
            icon = {
                // Icon(icon, contentDescription = "Example Icon")
            },

            title = { Text(text = stringResource(id = R.string.delete_item_title)) },
            text = { Text(text = stringResource(id = R.string.delete_item_confirmation)) },
            onDismissRequest = uiEvents:: onDismissDeleteConfirmation,
            confirmButton = {
                TextButton(
                    onClick = {uiEvents.onConfirmDelete()
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion{
                        if(!sheetState.isVisible){
                            uiEvents.onDismissDetails()
                        }
                    }
            }) {
            Text(text = stringResource(id = R.string.delete_button_label))
        }
        },
            dismissButton = {
                DismissButton(onClick = uiEvents:: onDismissDeleteConfirmation
                )
            }
        )
    }

    ModalBottomSheet(
        onDismissRequest = uiEvents::onDismissDetails,
        sheetState = sheetState,
        dragHandle = null,
        modifier = modifier.semantics {
            this.paneTitle = paneTitle
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            uiEvents.onDismissDetails()
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.close_button_label)
                    )
                }

                Text(
                    text = stringResource(id = R.string.edit_item_title),
                    modifier = Modifier.semantics { heading() }
                )

                TextButton(onClick = {
                    val canDismissSheet = uiEvents.onSaveClick()
                    if (canDismissSheet)
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                uiEvents.onDismissDetails()
                            }
                        }
                }) {
                    Text(text = stringResource(id = R.string.save_button_label))
                }
            }

            Spacer(Modifier.height(height = 8.dp))

            val focusManager = LocalFocusManager.current

            GarbageTextField(
                value = item.what,
                onValueChange = uiEvents::onWhatChange,
                labelRes = R.string.what_label,
                focusManager = focusManager,
                isLastField = false,
                isError = isWhatError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp)
            )

            GarbageTextField(
                value = item.where,
                onValueChange = uiEvents::onWhereChange,
                labelRes = R.string.where_label,
                focusManager = focusManager,
                isLastField = false,
                isError = isWhereError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp)
            )

            GarbageTextField(
                value = item.description,
                onValueChange = uiEvents::onDescriptionChange,
                labelRes = R.string.description_label,
                focusManager = focusManager,
                isLastField = true,
                isError = false,
                minLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp)
            )

            Spacer(Modifier.height(height = 8.dp))

            Button(onClick = uiEvents::onDeleteClick) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.semantics(mergeDescendants = true) { }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )

                    Spacer(Modifier.height(height = 8.dp))

                    Text(text = stringResource(id = R.string.delete_button_label))
                }
            }
        }
    }
}

@ThemedPreviews
@Composable
fun DetailsSheetPreview(@PreviewParameter(provider = BooleanProvider::class) isTrue: Boolean) {
    GarbageFinalTheme {
        DetailsSheet(
            item = Item(what = "Milk", where = "Dairy", description = "Low fat"),
            isWhatError = isTrue,
            isWhereError = isTrue,
            showDeleteConfirmation = isTrue,
            uiEvents = object : GarbageListViewModel.UiEvents {
                override fun onWhatChange(what: String) {}
                override fun onWhereChange(where: String) {}
                override fun onDescriptionChange(description: String) {}
                override fun onSaveClick(): Boolean {return true}
                override fun onDeleteClick() {}
                override fun onConfirmDelete() {}
                override fun onDismissDeleteConfirmation() {}
                override fun onDismissDetails() {}
                override fun onUpClick() {}
                override fun onAddItemClick() {}
                override fun onEditItemClick(item: Item) {}
            },
        )
    }
}