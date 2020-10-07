package io.numbersprotocol.starlingcapture.collector

import android.content.Context
import androidx.annotation.StringRes
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.data.information.Information
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

abstract class InformationAndSignatureProvider(
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
        return try {
            onStarted()
            collectInformation()
            collectSignature()
            onCleared()
            Result.success()
        } catch (e: Exception) {
            notificationUtil.notifyException(e, notificationId)
            Result.failure()
        }
    }

    private suspend fun collectInformation() {
        provideInformation()?.also {
            Timber.i("Collecting information with $name.")
            notifyStartCollection(R.string.message_collecting_proof_information)
            informationRepository.add(*it.toTypedArray())
        }
    }

    private suspend fun collectSignature() {
        val proof = proofRepository.getByHash(hash)!!
        val sortedProofInformation = SortedProofInformation.create(proof, informationRepository)
        val json = sortedProofInformation.toJson()
        provideSignature(json)?.also {
            Timber.i("Signing the proof and its information with $name.")
            notifyStartCollection(R.string.message_signing_proof_information)
            signatureRepository.add(*it.toTypedArray())
        }
    }

    private fun notifyStartCollection(@StringRes stringRes: Int) {
        val builder = ProofCollector.getNotificationBuilder(context).apply {
            setContentText(context.resources.getString(stringRes))
            setProgress(0, 0, true)
            setOngoing(true)
        }
        notificationUtil.notify(notificationId, builder)
    }

    open suspend fun onStarted() {}

    open suspend fun provideInformation(): Collection<Information>? = null

    open suspend fun provideSignature(serialized: String): Collection<Signature>? = null

    open suspend fun onCleared() {}
}