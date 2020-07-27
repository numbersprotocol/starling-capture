package io.numbersprotocol.starlingcapture.collector.signature

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.collector.ProofCollector
import io.numbersprotocol.starlingcapture.data.information.InformationRepository
import io.numbersprotocol.starlingcapture.data.proof.ProofRepository
import io.numbersprotocol.starlingcapture.data.serialization.SortedProofInformation
import io.numbersprotocol.starlingcapture.data.signature.Signature
import io.numbersprotocol.starlingcapture.data.signature.SignatureRepository
import io.numbersprotocol.starlingcapture.util.MimeType
import io.numbersprotocol.starlingcapture.util.NotificationUtil
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import kotlin.properties.Delegates

abstract class SignatureProvider(
    val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    abstract val name: String

    lateinit var hash: String
    lateinit var mimeType: MimeType
    private var notificationId by Delegates.notNull<Int>()
    private val notificationUtil: NotificationUtil by inject()
    private val proofRepository: ProofRepository by inject()
    private val informationRepository: InformationRepository by inject()
    private val signatureRepository: SignatureRepository by inject()

    private fun initialize() {
        hash = inputData.getString(ProofCollector.KEY_HASH)!!
        mimeType = MimeType.fromString(inputData.getString(ProofCollector.KEY_MIME_TYPE)!!)

        val illegalNotificationId = NotificationUtil.NOTIFICATION_ID_MIN - 1
        notificationId = inputData.getInt(ProofCollector.KEY_NOTIFICATION_ID, illegalNotificationId)
        if (notificationId == illegalNotificationId) error("Cannot get notification ID from input data.")
    }

    override suspend fun doWork(): Result {
        initialize()
        Timber.i("Start to sign the generated SortedProofInformation with $name.")
        return try {
            notifyStartSigning()
            val proof = proofRepository.getByHash(hash)!!
            val sortedProofInformation = SortedProofInformation.create(proof, informationRepository)
            val json = sortedProofInformation.toJson()
            signatureRepository.add(provide(json))
            Result.success()
        } catch (e: Exception) {
            Timber.e(e)
            notificationUtil.notifyException(e, notificationId)
            Result.failure()
        }
    }

    private fun notifyStartSigning() {
        val builder = ProofCollector.getNotificationBuilder(context).apply {
            setContentText(context.resources.getString(R.string.message_signing_proof_information))
            setProgress(0, 0, true)
            setOngoing(true)
        }
        notificationUtil.notify(notificationId, builder)
    }

    abstract suspend fun provide(serialized: String): Signature
}