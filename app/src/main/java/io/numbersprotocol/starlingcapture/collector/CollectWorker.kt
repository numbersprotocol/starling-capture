package io.numbersprotocol.starlingcapture.collector

import android.content.Context
import androidx.annotation.StringRes
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.numbersprotocol.starlingcapture.util.MimeType
import io.numbersprotocol.starlingcapture.util.NotificationUtil
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.properties.Delegates

abstract class CollectWorker(
    val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    abstract val name: String

    lateinit var hash: String
    lateinit var mimeType: MimeType
    private var notificationId by Delegates.notNull<Int>()
    private val notificationUtil: NotificationUtil by inject()

    private fun initialize() {
        hash = inputData.getString(ProofCollector.KEY_HASH)!!
        mimeType = MimeType.fromString(inputData.getString(ProofCollector.KEY_MIME_TYPE)!!)

        val illegalNotificationId = NotificationUtil.NOTIFICATION_ID_MIN - 1
        notificationId = inputData.getInt(ProofCollector.KEY_NOTIFICATION_ID, illegalNotificationId)
        if (notificationId == illegalNotificationId) error("Cannot get notification ID from input data.")
    }

    override suspend fun doWork(): Result {
        initialize()
        return try {
            onStarted()
            collect()
            onCleared()
            Result.success()
        } catch (e: Exception) {
            notificationUtil.notifyException(e, notificationId)
            Result.failure()
        } finally {
            onCleared()
        }
    }

    open suspend fun onStarted() {}
    abstract suspend fun collect()
    open suspend fun onCleared() {}

    protected fun notifyStartCollecting(@StringRes stringRes: Int) {
        val builder = ProofCollector.getNotificationBuilder(context).apply {
            setContentText(context.resources.getString(stringRes))
            setProgress(0, 0, true)
            setOngoing(true)
        }
        notificationUtil.notify(notificationId, builder)
    }
}