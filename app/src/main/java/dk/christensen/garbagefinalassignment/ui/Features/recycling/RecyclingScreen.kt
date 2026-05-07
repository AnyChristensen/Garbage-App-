package dk.christensen.garbagefinalassignment.ui.Features.recycling

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import dk.christensen.garbagefinalassignment.R
import dk.christensen.garbagefinalassignment.ui.components.NavigationType
import dk.christensen.garbagefinalassignment.ui.components.GarbageTopAppBar
import dk.christensen.garbagefinalassignment.ui.navigation.AppRoute
import kotlinx.serialization.Serializable
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import dk.christensen.garbagefinalassignment.ui.components.RequestBackgroundLocationPermission
import dk.christensen.garbagefinalassignment.ui.components.RequestLocationPermission
import dk.christensen.garbagefinalassignment.ui.components.Map


@Serializable
object RecyclingStation : AppRoute

@Serializable
object Bins : AppRoute

@Composable
fun RecyclingScreen(
    modifier: Modifier = Modifier,
    viewModel: RecyclingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    RecyclingScreen(uiState = uiState, uiEvents = viewModel.uiEvents, modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecyclingScreen(
    uiState: RecyclingViewModel.UiState,
    uiEvents: RecyclingViewModel.UiEvents,
    modifier: Modifier = Modifier
) {

    var showPermissionRequest by remember { mutableStateOf(value = true) }

    if (showPermissionRequest) {
        RequestLocationPermission(
            onPermissionGranted = {
                showPermissionRequest = false
                uiEvents.onLocationPermissionGranted()
            },
            onPermissionDenied = {
                showPermissionRequest = false
                // Optional: You could show a Snackbar here saying "Shops are not sorted by distance."
            }
        )
    }

    if (uiState.showBackgroundPermission) {
        RequestBackgroundLocationPermission(
            onPermissionGranted = {
                uiEvents.onBackgroundPermissionHandled(isGranted = true)
            },
            onPermissionDenied = {
                uiEvents.onBackgroundPermissionHandled(isGranted = false)
            }
        )
    }

    Scaffold(
        topBar = {
            GarbageTopAppBar(
                titleRes = R.string.bins_screen_title,
                navigationType = NavigationType.NONE
            )
        }
    )
    { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
        {

            // CAROUSEL WITH BIN TYPES
            item {
                HorizontalUncontainedCarousel(
                    state = rememberCarouselState { uiState.bins.count() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 10.dp),
                    itemWidth = 150.dp,
                    itemSpacing = 8.dp,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) { i ->
                    val bin = uiState.bins[i]

                    Card(
                        modifier = Modifier
                            .height(150.dp)
                            .clip(MaterialTheme.shapes.large)
                            .clickable { uiEvents.onBinSelected(bin) }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = bin.imageUrl,
                                contentDescription = bin.name,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = bin.name,
                                modifier = Modifier.padding(bottom = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // GEOFENCING BUTTON
            item {
                OutlinedButton(
                    onClick = uiEvents::onEnableGeofencingClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp, bottom = 12.dp)
                ) {
                    Text("Enable recycling notifications")
                }
            }

            // MAP WITH USER LOCATION AND RECYCLING STATIONS
            item {
                Text(
                    text = stringResource(id = R.string.map_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 24.dp, bottom = 12.dp)
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    Map(
                        // setting the latitude and length using the uiState from Viewmodel and with a fallback value (as Copenhagen)
                        lat = uiState.userLat ?: 55.6761,
                        lng = uiState.userLng ?: 12.5683,
                        stations = uiState.filteredRecyclingStations

                    )
                }
            }

            // FILTERING OF RECYCLING STATIONS
            item {

                Text(
                    text = "Filter by waste type",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .padding(top = 24.dp, bottom = 12.dp)
                )

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                )
                {
                    uiState.bins.forEach { bin ->
                        val isSelected = uiState.clickedBins.contains(bin.name)

                        // Counting the number of recycling stations with this type of bin
                        // To make it visible that only few stations do not have all bins
                        val count = uiState.recyclingStations.count { station ->
                            station.bins.any {
                                it.contains(bin.nameDanish, ignoreCase = true)
                            }
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = { uiEvents.onFilterClick(bin.name) },
                            label = {Text("${bin.name} ($count)",
                                style = MaterialTheme.typography.labelMedium)},
                            modifier = Modifier.height(32.dp),
                        )
                    }
                }
            }

            item{
                Spacer(modifier = Modifier.size(24.dp))
            }

            item {
                Text(
                    text = stringResource(R.string.Recycling_station_list),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 12.dp)
                )
            }


            items(uiState.recyclingStations) { recyclingStation ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {

                        Text(
                            text = recyclingStation.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = recyclingStation.address,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = recyclingStation.category,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        // Bottom Sheet for selected bin
        uiState.selectedBin?.let { bin ->
            BinDetailsSheet(
                bin = bin,
                onDismiss = uiEvents::onDismissBinDetails,
                onTrackRecycling = {uiEvents.onTrackRecycling(bin)}
            )
        }

    }
}


