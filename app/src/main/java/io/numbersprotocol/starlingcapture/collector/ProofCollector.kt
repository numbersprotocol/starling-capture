package io.numbersprotocol.starlingcapture.collector

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.*
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.data.proof.Proof
import io.numbersprotocol.starlingcapture.data.proof.ProofRepository
import io.numbersprotocol.starlingcapture.util.MimeType
import io.numbersprotocol.starlingcapture.util.NotificationUtil
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.io.File

class ProofCollector(
    private val context: Context,
    private val proofRepository: ProofRepository,
    private val notificationUtil: NotificationUtil
) {

    private val provideInformationRequestBuilders = mutableSetOf<OneTimeWorkRequest.Builder>()
    private val provideSignatureRequestBuilders = mutableSetOf<OneTimeWorkRequest.Builder>()

    suspend fun storeAndCollect(cachedMediaFile: File, mimeType: MimeType): String {
        val notificationId = notificationUtil.createNotificationId()

        val storedMediaFile = proofRepository.addRawFile(cachedMediaFile)
        val proofHash = storedMediaFile.nameWithoutExtension

        proofRepository.add(Proof(proofHash, mimeType, System.currentTimeMillis()))

        val workData = workDataOf(
            KEY_HASH to proofHash,
            KEY_MIME_TYPE to mimeType.toString(),
            KEY_NOTIFICATION_ID to notificationId
        )

        val finishCollectionRequest = OneTimeWorkRequestBuilder<FinishCollectionWorker>()
            .setInputData(workData)
            .build()

        val provideInformationRequests =
            provideInformationRequestBuilders.map { it.setInputData(workData).build() }

        val provideSignatureRequests =
            provideSignatureRequestBuilders.map { it.setInputData(workData).build() }

        notifyStartCollecting(notificationId)
        WorkManager.getInstance(context)
            .beginWith(provideInformationRequests)
            .then(provideSignatureRequests)
            .then(finishCollectionRequest)
            .enqueue()

        return proofHash
    }

    fun addProvideInformationRequestBuilder(builder: OneTimeWorkRequest.Builder) =
        provideInformationRequestBuilders.add(builder)

    fun removeProvideInformationRequestBuilder(builder: OneTimeWorkRequest.Builder) =
        provideInformationRequestBuilders.remove(builder)

    fun addProvideSignatureRequestBuilder(builder: OneTimeWorkRequest.Builder) =
        provideSignatureRequestBuilders.add(builder)

    fun removeProvideSignatureRequestBuilder(builder: OneTimeWorkRequest.Builder) =
        provideSignatureRequestBuilders.remove(builder)

    private fun notifyStartCollecting(notificationId: Int) {
        val builder = getNotificationBuilder(context).apply {
            setContentIntent(notificationUtil.baseActivityIntent)
            setProgress(0, 0, true)
            setOngoing(true)
        }
        notificationUtil.notify(notificationId, builder)
    }

    class FinishCollectionWorker(
        context: Context,
        params: WorkerParameters
    ) : CoroutineWorker(context, params), KoinComponent {

        private val notificationUtil: NotificationUtil by inject()

        override suspend fun doWork(): Result {
            val illegalNotificationId = NotificationUtil.NOTIFICATION_ID_MIN - 1
            val notificationId =
                inputData.getInt(KEY_NOTIFICATION_ID, illegalNotificationId)
            if (notificationId == illegalNotificationId) {
                Timber.e("Cannot get notification ID from input data.")
                return Result.failure()
            }
            notificationUtil.cancel(notificationId)
            return Result.success()
        }
    }

    companion object {
        const val KEY_HASH = "KEY_HASH"
        const val KEY_MIME_TYPE = "KEY_MIME_TYPE"
        const val KEY_NOTIFICATION_ID = "NOTIFICATION_ID"

        fun getNotificationBuilder(context: Context): NotificationCompat.Builder =
            NotificationCompat.Builder(context, NotificationUtil.CHANNEL_DEFAULT)
                .setSmallIcon(R.drawable.ic_capture)
                .setContentTitle(context.getString(R.string.collect_proof_information))
    }
}