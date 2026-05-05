package dk.christensen.garbagefinalassignment.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import dk.christensen.garbagefinalassignment.domain.RecyclingStation

// Creating a Map component that I can use in my Recycling Screen (and other places)

@Composable
fun Map(
    lat: Double,
    lng: Double,
    stations: List<RecyclingStation>
) {
    val location = LatLng(lat, lng)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 12f)
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ){

        // The users location
        // Change the icon to blue to separate it from the recycling stations pins
        Marker(
            state = MarkerState(position = location),
            title = "You are here",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
        )

        // Recycling stations
        stations.forEach { station ->
            Marker(
                state = MarkerState(
                    position = LatLng(
                        station.latitude,
                        station.longitude
                    )
                ),
                title = station.name,
                snippet = station.address
            )
        }
    }
}

