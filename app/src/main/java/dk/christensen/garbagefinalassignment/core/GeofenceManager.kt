package dk.christensen.garbagefinalassignment.core



// We are setting up some specific places or listing. We dont want to listen everywhere.

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import dk.christensen.garbagefinalassignment.core.receivers.GeofenceReceiver
import dk.christensen.garbagefinalassignment.domain.RecyclingStation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeofenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val geofencingClient = LocationServices.getGeofencingClient(context)

    // The Intent that points to our BroadcastReceiver
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(/* packageContext = */ context, /* cls = */ GeofenceReceiver::class.java)
        // FLAG_MUTABLE is strictly required by Android 12+ for Geofencing PendingIntents!
        PendingIntent.getBroadcast(
            /* context = */ context,
            /* requestCode = */ 0,
            /* intent = */ intent,
            /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }


    @SuppressLint("MissingPermission") // We only call this after background permissions are granted
    fun registerGeofences(recyclingStations: List<RecyclingStation>) {
        if (recyclingStations.isEmpty()) return

        // (Tip: Android limits you to 100 geofences per app, so we'll just take the 10 closest ones)
        val geofences = recyclingStations.take(10).map { recyclingStation ->
            Geofence.Builder()
                .setRequestId(recyclingStation.name)
                .setCircularRegion(
                    /* latitude = */ recyclingStation.latitude,
                    /* longitude = */ recyclingStation.longitude,
                    /* radius = */ 150f // Radius in meters (150m is a good size for a supermarket)
                )
                .setExpirationDuration(/* durationMillis = */ Geofence.NEVER_EXPIRE)
                .setTransitionTypes(/* transitionTypes = */ Geofence.GEOFENCE_TRANSITION_ENTER)
                .build()
        }

        val geofencingRequest = GeofencingRequest.Builder()
            // INITIAL_TRIGGER_ENTER means trigger immediately if they are ALREADY inside the circle
            .setInitialTrigger(/* initialTrigger = */ GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(/* geofences = */ geofences)
            .build()

        geofencingClient.addGeofences(/* p0 = */ geofencingRequest, /* p1 = */ geofencePendingIntent)
            .addOnSuccessListener { Log.d(/* tag = */ "GeofenceManager", /* msg = */ "✅ Geofences successfully registered!") }
            .addOnFailureListener { e -> Log.d(/* tag = */ "GeofenceManager", /* msg = */ "❌ Failed to register geofences: ${e.message}") }
    }
}


