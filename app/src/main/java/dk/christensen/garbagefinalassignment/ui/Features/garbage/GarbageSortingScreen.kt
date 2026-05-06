package dk.christensen.garbagefinalassignment.ui.Features.garbage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.christensen.garbagefinalassignment.domain.Item
import dk.christensen.garbagefinalassignment.R
import dk.christensen.garbagefinalassignment.ui.theme.GarbageFinalTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.ui.unit.dp
import dk.christensen.garbagefinalassignment.ui.navigation.AppRoute
import dk.christensen.garbagefinalassignment.ui.navigation.NestedGraph
import kotlinx.serialization.Serializable

@Serializable
object GarbageGraph: NestedGraph {
    override val startDestination: AppRoute
        get() = GarbageSorting
}

@Serializable
data object GarbageSorting : AppRoute


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GarbageSortingScreen(
    onNavigate: (GarbageSortingViewModel.NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GarbageSortingViewModel = hiltViewModel(),
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.navigationEvents.collect{
            onNavigate(it)
        }
    }

    GarbageSortingScreen(
        modifier = modifier,
        uiState = uiState,
        uiEvents = viewModel.uiEvents
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GarbageSortingScreen(
    uiState: GarbageSortingViewModel.UiState,
    uiEvents: GarbageSortingViewModel.UiEvents,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Handles what is in focus in the UI (e.g. can be used to activate or close the keyboard)
                val focusManager = LocalFocusManager.current

                // Text Field for Search
                TextField(
                    value = if(uiState.foundItem.isEmpty()){uiState.userInput}else{uiState.foundItem},
                    onValueChange = {uiEvents.onUserInputChanged(it)},
                    label = { Text(text = stringResource(id = R.string.garbageItem_label)) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) })
                )
                Spacer(modifier = Modifier.height(height = 16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    // Where Button
                    Button(onClick = {
                        uiEvents.onSearchItemClick(uiState.userInput)
                        focusManager.clearFocus()
                    }) {
                        Text(text = stringResource(id = R.string.where_button_label))
                    }

                    // ShowGarbageList Button
                    Button(onClick = uiEvents::onShowListClick) {
                        Text(text = stringResource(id = R.string.list_button_label))
                    }

                    // Show Affald KBH screen Button
                    Button(onClick = uiEvents::onShowAffaldKbhClick) {
                        Text(text = stringResource(id = R.string.affald_Kbh_button))
                    }

                    // Detect garbage items using AI Button
                    Button(
                        onClick = uiEvents::onScanClick,
                    ) {
                        Icon(Icons.Default.CameraAlt, null)
                        Text(text = stringResource(id = R.string.scan_garbage_button))
                    }



                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GarbageSortingScreenPreview(){
    GarbageFinalTheme() {
        GarbageSortingScreen(
            modifier = Modifier,
            uiState = GarbageSortingViewModel.UiState(
                garbageList = listOf(
                    Item(what = "Newspaper", where = "Paper"),
                    Item(what = "Apple", where = "Food")
                )
            ),
            uiEvents = object : GarbageSortingViewModel.UiEvents {
                //override fun onAddItemClick(){}
                //override fun onEditItemClick(item: Item) {}
                override fun onShowListClick() {}
                override fun onUserInputChanged(text: String) {}
                override fun onSearchItemClick(input: String){}
                override fun onShowAffaldKbhClick() {}
                override fun onScanClick() {}
                },
            )
        }
    }

