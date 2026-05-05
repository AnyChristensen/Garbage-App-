package dk.christensen.garbagefinalassignment.core.receivers

// Okay I see your user er here. This is the listener. The os (?) says user is here.
// The reciever will be in the background. So you dont have to have the app open in
// the background.
// As long as it is installed it should send messages from the background (tjek op)

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import dk.christensen.garbagefinalassignment.core.workers.SmartGeofenceWorker

class GeofenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(/* intent = */ intent)

        if (geofencingEvent == null || geofencingEvent.hasError()) {
            Log.e(/* tag = */ "GeofenceReceiver", /* msg = */ "Error receiving geofence event")
            return
        }

        if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            val triggeringGeofences = geofencingEvent.triggeringGeofences ?: return

            triggeringGeofences.forEach { geofence ->
                val recyclingStationId = geofence.requestId

                val workRequest = OneTimeWorkRequestBuilder<SmartGeofenceWorker>()
                    .setInputData(workDataOf("recyclingStation_id" to recyclingStationId))
                    .build()

                WorkManager.getInstance(context).enqueue(workRequest)
            }
        }
    }
}