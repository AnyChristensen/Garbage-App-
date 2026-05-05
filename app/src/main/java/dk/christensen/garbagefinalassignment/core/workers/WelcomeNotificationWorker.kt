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

@HiltWorker
class WelcomeNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext = context, params = workerParams) {

    override suspend fun doWork(): Result {
        Log.d("WelcomeWorker", "RUNNING")

        NotificationHelper.showNotification(
            context = applicationContext,
            title = applicationContext.getString(/* resId = */ R.string.welcome_notification_title),
            message = applicationContext.getString(/* resId = */ R.string.welcome_notification_message),
            notificationId = 1 // Static ID
        )
        return Result.success()
    }
}