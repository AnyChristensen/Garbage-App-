package dk.christensen.garbagefinalassignment.core.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dk.christensen.garbagefinalassignment.R
import dk.christensen.garbagefinalassignment.core.NotificationHelper
import dk.christensen.garbagefinalassignment.domain.BinRepository
import dk.christensen.garbagefinalassignment.domain.ItemRepository
import dk.christensen.garbagefinalassignment.domain.RecyclingStationRepository
import dk.christensen.garbagefinalassignment.domain.getOverdueBins
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

// Obs core folder is for components all around in the application

// This file: what is triggered when the user enters a geospot?
@HiltWorker
class SmartGeofenceWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val recyclingStationRepository: RecyclingStationRepository,
    private val binRepository: BinRepository
) : CoroutineWorker(appContext = context, params = workerParams) {

    override suspend fun doWork(): Result {
        // Getting the Recycling station name passed from the GeofenceReceiver (key-value)
        val recyclingStationId = inputData.getString(key = "recyclingStation_id") ?: return Result.failure()

        // Getting the list of Recycling Stations
        val recyclingStations = recyclingStationRepository.getRecyclingStations().first()

        // Finding the nearest Recycling station
        val nearestRecyclingStation = recyclingStations.find { it.id == recyclingStationId } ?: return Result.success()


        // Fetching the list of bins
        val bins = binRepository.getBins().first()

        // Checking if any bin is over 1 week due
        val overdueBins = getOverdueBins(bins)

        // Checking if any of the overdue bins is of the same type of bins that exists on the nearest recycling station
        val matchingBins = overdueBins.filter { bin ->
            nearestRecyclingStation.bins.any { binInStation ->
                binInStation.equals(bin.name, ignoreCase = true) }  }


        // Only showing the notification if there is matching bins
        if (matchingBins.isNotEmpty()) {
            val binWord = if (matchingBins.size == 1) context.getString(/* resId = */ R.string.bin_singular) else context.getString(R.string.bin_plural)
            val sampleBin = matchingBins.first().name

            NotificationHelper.showNotification(
                context =  applicationContext,
                title = context.getString(R.string.geofence_notification_title, nearestRecyclingStation.name),
                message = context.getString(R.string.geofence_notification_message, matchingBins.size, binWord, sampleBin),
                notificationId = nearestRecyclingStation.id.hashCode())


        } else {
            Log.d(/* tag = */ "SmartGeofenceWorker", /* msg = */ "Passed ${nearestRecyclingStation.name}, but no bins need to be emptied. Staying quiet!")
        }

        return Result.success()
    }
}