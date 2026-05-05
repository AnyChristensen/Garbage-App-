package dk.christensen.garbagefinalassignment.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import dk.christensen.garbagefinalassignment.MainActivity
import dk.christensen.garbagefinalassignment.R

object NotificationHelper {
    private const val CHANNEL_ID = "garbage_notifications_V3"

    fun showNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int
    ) {
        val notificationManager =
            context.getSystemService(/* p0 = */ Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the channel (required for Android 8.0+)
        val channel = NotificationChannel(
            /* id = */ CHANNEL_ID,
            /* name = */ "Garbage Reminders",
            /* importance = */ NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications for deadlines and welcome messages"
        }
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(/* packageContext = */ context, /* cls = */ MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            /* context = */ context,
            /* requestCode = */ 0,
            /* intent = */ intent,
            /* flags = */ PendingIntent.FLAG_IMMUTABLE // This flag is strictly required for Android 12+
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Dismisses the notification when clicked
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}