package dk.christensen.garbagefinalassignment.ui.Features.recycling

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.christensen.garbagefinalassignment.core.GeofenceManager
import dk.christensen.garbagefinalassignment.domain.Bin
import dk.christensen.garbagefinalassignment.domain.BinRepository
import dk.christensen.garbagefinalassignment.domain.RecyclingStation
import dk.christensen.garbagefinalassignment.domain.RecyclingStationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class RecyclingViewModel @Inject constructor(
    private val binRepository: BinRepository,
    private val recyclingStationRepository: RecyclingStationRepository,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val geofenceManager: GeofenceManager
) : ViewModel() {


    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {

        // Bins
        viewModelScope.launch {
            binRepository.getBins().collect { bins ->

                // If empty insert all the bins
                if (bins.isEmpty()) {
                    binRepository.insertBins()
                } else {
                    _uiState.update { currentState ->
                        val updatedSelectedBin = currentState.selectedBin?.let { selected ->
                            bins.find { it.name == selected.name }
                        }
                        currentState.copy(
                            bins = bins,
                            selectedBin = updatedSelectedBin
                        )
                    }
                }
            }
        }

        // Recycling Stations
        viewModelScope.launch {
                // using collect in order to "collect" from the Flow list
            val recyclingStations =
                recyclingStationRepository.getRecyclingStations().collect { stations ->
                    _uiState.update { it.copy(
                        recyclingStations = stations,
                        filteredRecyclingStations = stations) }
                }

        }
    }


    val uiEvents: UiEvents = object : UiEvents {
        override fun onBinSelected(bin: Bin) {
            _uiState.update { it.copy(selectedBin = bin) }
        }

        override fun onDismissBinDetails() {
            _uiState.update { it.copy(selectedBin = null) }
        }


        override fun onTrackRecycling(bin: Bin) {
            viewModelScope.launch {
                val updatedBin = bin.copy(
                    // Setting the time of lastest pickup to current time (in milliseconds) when the user clicks track recycling
                    lastPickupTime = System.currentTimeMillis()
                )

                // And updating it in the repository (in firestore)
                binRepository.updateBin(updatedBin)
            }
        }

        override fun onFilterClick (binName: String){
            // not using viewmodelScope.launch as it is a not a operation that
            // needs to be done async.
            _uiState.update { current ->
                val updatedClickedBins =
                    if (current.clickedBins.contains(binName)){
                        current.clickedBins - binName
                    } else {
                        current.clickedBins + binName
                    }

                val filteredStations = filterRecyclingStations(
                    recyclingStations = current.recyclingStations,
                    clickedBins = updatedClickedBins,
                    bins = current.bins
                )

                current.copy(
                    clickedBins = updatedClickedBins,
                    filteredRecyclingStations = filteredStations
                )
            }
        }

        override fun onShowFilterSheet() {
            _uiState.update { it.copy(showFilterSheet = true)}
        }

        override fun onDismissFilterDetails() {
            _uiState.update { it.copy(showFilterSheet = false)}
        }


        @SuppressLint("MissingPermission")
        override fun onLocationPermissionGranted() {
            Log.d(/* tag = */ "RecyclingViewModel", /* msg = */ "Permission granted! Waking up the GPS...")

            fusedLocationClient.getCurrentLocation(
                /* p0 = */ Priority.PRIORITY_HIGH_ACCURACY,
                /* p1 = */ CancellationTokenSource().token
            ).addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Log.d(/* tag = */ "RecyclingViewModel", /* msg = */ "Location found: ${location.latitude}, ${location.longitude}")

                    _uiState.update { currentState ->
                        val sortedRecyclingStations = sortRecyclingStationsByDistance(
                            userLat = location.latitude,
                            userLng = location.longitude,
                            currentRecyclingStations = currentState.recyclingStations
                        )
                        currentState.copy(
                            userLat = location.latitude,
                            userLng = location.longitude,
                            recyclingStations = sortedRecyclingStations
                        )
                    }
                } else {
                    // This happens if the emulator has NEVER had its location set
                    Log.d(/* tag = */ "RecyclingViewModel", /* msg = */ "Location is still null! Make sure to set a location in the Emulator settings.")
                }
            }.addOnFailureListener { exception ->
                Log.d(/* tag = */ "RecyclingViewModel", /* msg = */ "Failed to get location: ${exception.message}")
            }
        }

        override fun onEnableGeofencingClick() {
            _uiState.update { current ->
                current.copy(showBackgroundPermission = !current.showBackgroundPermission)
            }
        }

        override fun onBackgroundPermissionHandled(isGranted: Boolean) {
            _uiState.update { it.copy(showBackgroundPermission = false) }

            if (isGranted) {
                val recyclingStations = _uiState.value.recyclingStations
                geofenceManager.registerGeofences(recyclingStations)
            } else {
                Log.d(/* tag = */ "RecyclingViewModel", /* msg = */ "Background location denied. Geofencing won't work in the background.")
            }
        }


    }


        // Function to filter the recycling stations by bin type (obs. removing recycling stations that not contains the bin types)
        // (Using allRecyclingStations to keep the same list with all stations for each filtering)
        private fun filterRecyclingStations(
            clickedBins: List<String>,
            recyclingStations: List<RecyclingStation>,
            bins: List<Bin>
        ): List<RecyclingStation> {

            if (clickedBins.isEmpty()) return recyclingStations

            // Log to see the clicked bins
            Log.d("FILTER", "Clicked bins: $clickedBins")

            return recyclingStations.filter { station ->

                // For each station, check if at least one (any) clicked bin matches the stations bins
                clickedBins.any { englishName ->

                    // Finding the danish name by looking through bins (matching on english name)
                    val danishName = bins.find { it.name == englishName } ?.nameDanish

                    // Checking that the name in danish is not null
                    if (danishName == null) return@any false

                    // Checking if any of the bins of the Recycling Stations (from API) contains
                    // the clicked bin name (match in danish)
                    station.bins.any { stationBin ->
                        stationBin.contains(danishName, ignoreCase = true)
                    }
                }
            }
        }



    // Function to sort the recycling stations by distance (obs. changing the order)
    private fun sortRecyclingStationsByDistance(userLat: Double, userLng: Double, currentRecyclingStations: List<RecyclingStation>): List<RecyclingStation> {
        val distanceResults = FloatArray(size = 1)

        return currentRecyclingStations.sortedBy { recyclingStation ->
            Location.distanceBetween(
                /* startLatitude = */ userLat,
                /* startLongitude = */ userLng,
                /* endLatitude = */ recyclingStation.latitude,
                /* endLongitude = */ recyclingStation.longitude,
                /* results = */ distanceResults
            )

            val distanceInMeters = distanceResults[0]

            Log.d(/* tag = */ "RecyclingViewModel", /* msg = */ "Distance to ${recyclingStation.name}: $distanceInMeters meters")

            distanceInMeters
        }

    }


    data class UiState(
        val bins: List<Bin> = emptyList(),
        val selectedBin: Bin? = null,
        val recyclingStations: List<RecyclingStation> = emptyList(),
        val filteredRecyclingStations: List<RecyclingStation> = emptyList(),
        val userLat: Double? = null,
        val userLng: Double? = null,
        val showBackgroundPermission: Boolean = false,
        val clickedBins: List<String> = emptyList(),
        val showFilterSheet: Boolean = false
    )


    @Immutable
    interface UiEvents {
        fun onBinSelected(bin: Bin)
        fun onDismissBinDetails()
        fun onEnableGeofencingClick()
        fun onBackgroundPermissionHandled(isGranted: Boolean)
        fun onTrackRecycling(bin: Bin)
        fun onLocationPermissionGranted()
        fun onFilterClick(binName: String)
        fun onShowFilterSheet()
        fun onDismissFilterDetails()

    }
}