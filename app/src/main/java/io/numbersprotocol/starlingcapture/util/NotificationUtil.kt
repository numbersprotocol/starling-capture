package io.numbersprotocol.starlingcapture.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.numbersprotocol.starlingcapture.BaseActivity
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.collector.ProofCollector
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger

class NotificationUtil(private val context: Context) {

    private val notificationIdMin = AtomicInteger(NOTIFICATION_ID_MIN)

    val baseActivityIntent: PendingIntent = Intent(context, BaseActivity::class.java).let {
        PendingIntent.getActivity(context, 0, it, 0)
    }

    fun initialize() {
        createChannel(CHANNEL_DEFAULT, R.string.starling_capture)
    }

    fun createChannel(
        id: String,
        @StringRes name: Int,
        importance: Int = NotificationManager.IMPORTANCE_HIGH
    ) {
        val channel = NotificationChannel(id, context.getString(name), importance)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun createNotificationId() = notificationIdMin.getAndIncrement()

    fun notifyException(e: Throwable, notificationId: Int) {
        Timber.e(e)
        val builder = ProofCollector.getNotificationBuilder(context).apply {
            setSmallIcon(R.drawable.ic_error)
            setContentText(e.message)
            setProgress(0, 0, false)
            setOngoing(false)
        }
        notify(notificationId, builder)
    }

    fun notify(notificationId: Int, builder: NotificationCompat.Builder) {
        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }

    fun cancel(notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    companion object {
        const val CHANNEL_DEFAULT = "CHANNEL_DEFAULT"
        const val NOTIFICATION_SAVE_PROOF_RELATED_DATA = 1
        const val NOTIFICATION_ID_MIN = 1000
    }
}