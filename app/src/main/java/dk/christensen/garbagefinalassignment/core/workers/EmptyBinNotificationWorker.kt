package dk.christensen.garbagefinalassignment.core.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dk.christensen.garbagefinalassignment.R
import dk.christensen.garbagefinalassignment.core.NotificationHelper
import dk.christensen.garbagefinalassignment.domain.BinRepository
import dk.christensen.garbagefinalassignment.domain.getOverdueBins
import kotlinx.coroutines.flow.first


// Sending a notification when a (previously emptied) bin has not been emptied for more than 7 days
@HiltWorker
class EmptyBinNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val binRepository: BinRepository
) : CoroutineWorker(appContext = context, params = workerParams) {

    override suspend fun doWork(): Result {
        return try {

            // The current number of milliseconds since jan. 1970
            val currentTime = System.currentTimeMillis()

            // Calculate when there has been more than a week (7 days)
            val sevenDaysInMilliSec = 7 * 24 * 60 * 60 * 1000L

            // Retrieving the bins
            val bins = binRepository.getBins().first()
            val overdueBins = getOverdueBins(bins)

            overdueBins.forEach { bin ->
                NotificationHelper.showNotification(
                    context = applicationContext,
                    title = applicationContext.getString(/* resId = */ R.string.deadline_notification_title),
                    notificationId = bin.name.hashCode(),
                    message = applicationContext.getString(
                        /* resId = */ R.string.deadline_notification_message,
                        /* ...formatArgs = */ bin.name
                    )
                )
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
