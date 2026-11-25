package do_.eki.yo.kisscrfinder.notification

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import do_.eki.yo.kisscrfinder.MainActivity
import do_.eki.yo.kisscrfinder.R
import do_.eki.yo.kisscrfinder.detection.Compound
import do_.eki.yo.kisscrfinder.detection.RadicalTable

class DetectionNotifier(
    private val context: Context,
) {
    private val manager = NotificationManagerCompat.from(context)

    fun ensureChannel() {
        val channel =
            NotificationChannelCompat
                .Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_HIGH)
                .setName(context.getString(R.string.notification_channel_name))
                .build()
        manager.createNotificationChannel(channel)
    }

    fun notifyDetected(compounds: List<Compound>) {
        val first = compounds.first().text
        if (!canPostNotifications(context)) return

        val contentIntent =
            PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE,
            )
        val notification =
            NotificationCompat
                .Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(
                    if (compounds.size == 1) {
                        context.getString(R.string.notification_detected, first)
                    } else {
                        context.getString(R.string.notification_detected_multiple, first, compounds.size)
                    },
                ).setContentText(compounds.joinToString("、") { it.describeShort() })
                .setStyle(
                    NotificationCompat.InboxStyle().also { style ->
                        compounds.take(MAX_LINES).forEach { style.addLine(it.describe()) }
                    },
                ).setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun Compound.describe(): String =
        context.getString(R.string.notification_line, text, RadicalTable.radicalChar(radical))

    private fun Compound.describeShort(): String =
        context.getString(R.string.notification_short, text, RadicalTable.radicalChar(radical))

    companion object {
        private const val CHANNEL_ID = "detections"
        private const val NOTIFICATION_ID = 1
        private const val MAX_LINES = 10

        fun canPostNotifications(context: Context): Boolean =
            NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
}
