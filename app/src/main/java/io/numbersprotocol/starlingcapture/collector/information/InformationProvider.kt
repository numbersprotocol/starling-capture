package io.numbersprotocol.starlingcapture.collector.information

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.collector.ProofCollector
import io.numbersprotocol.starlingcapture.data.information.Information
import io.numbersprotocol.starlingcapture.data.information.InformationRepository
import io.numbersprotocol.starlingcapture.util.MimeType
import io.numbersprotocol.starlingcapture.util.NotificationUtil
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import kotlin.properties.Delegates

abstract class InformationProvider(
    val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    abstract val name: String

    lateinit var hash: String
    lateinit var mimeType: MimeType
    private var notificationId by Delegates.notNull<Int>()
    private val notificationUtil: NotificationUtil by inject()
    private val informationRepository: InformationRepository by inject()

    private fun initialize() {
        hash = inputData.getString(ProofCollector.KEY_HASH)!!
        mimeType = MimeType.fromString(inputData.getString(ProofCollector.KEY_MIME_TYPE)!!)

        val illegalNotificationId = NotificationUtil.NOTIFICATION_PROOF_COLLECT_MIN - 1
        notificationId = inputData.getInt(ProofCollector.KEY_NOTIFICATION_ID, illegalNotificationId)
        if (notificationId == illegalNotificationId) error("Cannot get notification ID from input data.")
    }

    override suspend fun doWork(): Result {
        initialize()
        Timber.i("Start to collect information with $name.")
        return try {
            notifyStartCollectInformation()
            informationRepository.add(*provide().toTypedArray())
            Result.success()
        } catch (e: Exception) {
            Timber.e(e)
            notificationUtil.notifyException(e, notificationId)
            Result.failure()
        }
    }

    private fun notifyStartCollectInformation() {
        val builder = ProofCollector.getNotificationBuilder(context).apply {
            setContentText(context.resources.getString(R.string.message_collecting_proof_information))
            setProgress(0, 0, true)
            setOngoing(true)
        }
        notificationUtil.notify(notificationId, builder)
    }

    abstract suspend fun provide(): Collection<Information>
}