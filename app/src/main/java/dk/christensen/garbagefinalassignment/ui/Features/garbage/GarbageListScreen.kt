    package dk.christensen.garbagefinalassignment.ui.Features.garbage

    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.size
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.items
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.Add
    import androidx.compose.material.icons.filled.MoreVert
    import androidx.compose.material3.Card
    import androidx.compose.material3.ExperimentalMaterial3Api
    import androidx.compose.material3.FloatingActionButton
    import androidx.compose.material3.Icon
    import androidx.compose.material3.IconButton
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Scaffold
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.collectAsState
    import androidx.compose.runtime.getValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.res.stringResource
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.tooling.preview.PreviewParameter
    import androidx.compose.ui.unit.dp
    import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
    import coil.compose.AsyncImage
    import dk.christensen.garbagefinalassignment.R
    import dk.christensen.garbagefinalassignment.domain.Item
    import dk.christensen.garbagefinalassignment.domain.fullDescription
    import dk.christensen.garbagefinalassignment.ui.components.GarbageTopAppBar
    import dk.christensen.garbagefinalassignment.ui.components.ItemOrNullProvider
    import dk.christensen.garbagefinalassignment.ui.components.NavigationType
    import dk.christensen.garbagefinalassignment.ui.components.ThemedPreviews
    import dk.christensen.garbagefinalassignment.ui.components.previewBins
    import dk.christensen.garbagefinalassignment.ui.components.previewGarbageList
    import dk.christensen.garbagefinalassignment.ui.navigation.AppRoute
    import dk.christensen.garbagefinalassignment.ui.theme.GarbageFinalTheme
    import kotlinx.serialization.Serializable

    @Serializable
    data class GarbageList(val itemId: String? = null) : AppRoute


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun GarbageListScreen(
        onNavigate: (GarbageListViewModel.NavigationEvent) -> Unit,
        modifier: Modifier = Modifier,
        viewModel: GarbageListViewModel = hiltViewModel(),
    ) {
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(key1 = Unit) {
            viewModel.navigationEvents.collect {
                onNavigate(it)
            }
        }

        GarbageListScreen(
            modifier = modifier,
            uiState = uiState,
            uiEvents = viewModel.uiEvents
        )
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun GarbageListScreen(
        uiState: GarbageListViewModel.UiState,
        uiEvents: GarbageListViewModel.UiEvents,
        modifier: Modifier = Modifier,
    ) {
        Scaffold(
            topBar = {
                GarbageTopAppBar(
                    titleRes = R.string.app_name,
                    navigationType = NavigationType.BACK,
                    onUpClick = uiEvents::onUpClick
                )
            },
            // The add item button which is a floating button "+"
            floatingActionButton = {
                FloatingActionButton(onClick = uiEvents::onAddItemClick){
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.add_button_label)
                    )
                }
            }
        ) { innerPadding ->
            // Implementing Scroll using LazyColumn scroll
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues = innerPadding)
                ) {
                    items(items = uiState.garbageList) { item ->
                        val bin = uiState.bins.find { it.name == item.where }
                        // Each item in the list displayed with item, bin image, in brandcolor and with the possibility to change the item
                        ListItem(
                            item = item,
                            imageUrl = bin?.imageUrl,
                            brandColor = bin?.brandColor,
                            onItemClick = { uiEvents.onEditItemClick(item) }
                        )
                    }
                }
             }

             uiState.selectedItem?.let {
                 // Detail sheet that shows the selected item
              DetailsSheet(
                  item = it,
                  isWhatError = uiState.isWhatError,
                  isWhereError = uiState.isWhereError,
                  showDeleteConfirmation = uiState.showDeleteConfirmation,
                  uiEvents = uiEvents
              )
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ListItem(
        item: Item,
        imageUrl: String?,
        brandColor: Color?,
        onItemClick: () -> Unit)
        {
        Card(
            onClick = onItemClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ){
            Row(
                modifier = Modifier.padding(all = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Column(modifier = Modifier.weight(weight = 1f))
                {
                    // Item "name"
                    Text(
                        text = item.what,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = brandColor ?: Color.Unspecified // Matching garbageIcon (brand) color
                    )

                    // Description text
                    Text(
                        text = item.fullDescription(context = LocalContext.current),
                        style = MaterialTheme.typography.bodyMedium,
                        color = brandColor ?: Color.Unspecified // Matching garbageIcon (brand) color
                    )


                }
                // Image of Garbage bin (if any)
                imageUrl?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = item.where,
                        modifier = Modifier.size(size = 40.dp)
                    )
                }

                IconButton(onClick = onItemClick) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(id = R.string.edit_item_title)
                    )
                }
            }
        }
    }

    @ThemedPreviews
    @Composable
    private fun GarbageListScreenPreview(@PreviewParameter(provider =
    ItemOrNullProvider:: class) itemOrNull: Item?){
        GarbageFinalTheme {
            GarbageListScreen(
                uiState = GarbageListViewModel.UiState(
                    garbageList = previewGarbageList(),
                    bins = previewBins(),
                    selectedItem = itemOrNull,
                    isWhatError = false,
                    isWhereError = false,
                    showDeleteConfirmation = false,
                ),
                uiEvents = object : GarbageListViewModel.UiEvents {
                    override fun onWhatChange(what: String){}
                    override fun onWhereChange(where: String) {}
                    override fun onDescriptionChange(description: String){}
                    override fun onSaveClick(): Boolean{return true}
                    override fun onDeleteClick(){}
                    override fun onConfirmDelete(){}
                    override fun onDismissDeleteConfirmation(){}
                    override fun onDismissDetails(){}
                    override fun onAddItemClick(){}
                    override fun onEditItemClick(item: Item) {}
                    override fun onUpClick(){}
                    //override fun onShowListClick() {}
                    //override fun onUserInputChanged(text: String) {}
                    //override fun onSearchItemClick(input: String){}

                },
            )
        }
    }


