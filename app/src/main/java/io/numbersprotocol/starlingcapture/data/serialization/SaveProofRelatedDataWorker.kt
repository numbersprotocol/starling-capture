package io.numbersprotocol.starlingcapture.data.serialization

import android.content.Context
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.documentfile.provider.DocumentFile
import androidx.work.*
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.data.caption.CaptionRepository
import io.numbersprotocol.starlingcapture.data.proof.Proof
import io.numbersprotocol.starlingcapture.data.proof.ProofRepository
import io.numbersprotocol.starlingcapture.util.NotificationUtil
import io.numbersprotocol.starlingcapture.util.NotificationUtil.Companion.NOTIFICATION_SAVE_PROOF_RELATED_DATA
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class SaveProofRelatedDataWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val proofRepository: ProofRepository by inject()
    private val captionRepository: CaptionRepository by inject()
    private lateinit var proofHash: String
    private lateinit var saveDirectory: DocumentFile
    private lateinit var saveFolder: DocumentFile
    private val notificationUtil: NotificationUtil by inject()
    private val notificationBuilder =
        NotificationCompat.Builder(context, NotificationUtil.CHANNEL_DEFAULT)
            .setSmallIcon(R.drawable.ic_save_alt)
            .setContentTitle(context.getString(R.string.save_proof))

    private fun initialize() {
        proofHash = inputData.getString(KEY_PROOF_HASH)!!
        saveDirectory = DocumentFile.fromTreeUri(
            context,
            Uri.parse(inputData.getString(KEY_SAVE_DIRECTORY_URI))
        )!!
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun doWork(): Result {
        return try {
            notifyWorkerStart()
            initialize()

            saveFolder = saveDirectory.createDirectory(proofHash)!!

            val proof = proofRepository.getByHash(proofHash)!!
            val rawFile = proofRepository.getRawFile(proof)
            val proofInformationJson = Serialization.generateInformationJson(proof)
            val proofSignaturesJson = Serialization.generateSignaturesJson(proof)
            val captionText = captionRepository.getByProof(proof)?.text ?: ""

            withContext(Dispatchers.IO) {
                val targetRawFileUri =
                    saveFolder.createFile(proof.mimeType.toString(), rawFile.name)!!.uri
                rawFile.inputStream()
                    .copyTo(context.contentResolver.openOutputStream(targetRawFileUri)!!)

                val targetInformationUri =
                    saveFolder.createFile("application/json", "information.json")!!.uri
                context.contentResolver.openOutputStream(targetInformationUri)!!
                    .write(proofInformationJson.toByteArray(Charsets.UTF_8))

                val targetSignatureUri =
                    saveFolder.createFile("application/json", "signature.json")!!.uri
                context.contentResolver.openOutputStream(targetSignatureUri)!!
                    .write(proofSignaturesJson.toByteArray(Charsets.UTF_8))

                val targetCaptionUri =
                    saveFolder.createFile("text/plain", "caption.txt")!!.uri
                context.contentResolver.openOutputStream(targetCaptionUri)!!
                    .write(captionText.toByteArray(Charsets.UTF_8))
            }
            notifyWorkerSuccess()
            Result.success()
        } catch (e: Exception) {
            Timber.e(e)
            notificationUtil.notifyException(e, NOTIFICATION_SAVE_PROOF_RELATED_DATA)
            Result.failure()
        }
    }

    private fun notifyWorkerStart() {
        notificationBuilder.setContentText(context.getString(R.string.message_saving_proof_to_external))
        notificationBuilder.setProgress(0, 0, true)
        notificationUtil.notify(NOTIFICATION_SAVE_PROOF_RELATED_DATA, notificationBuilder)
    }

    private fun notifyWorkerSuccess() {
        notificationBuilder.setContentText(context.getString(R.string.message_saved_proof_to_external))
        notificationBuilder.setProgress(0, 0, false)
        notificationUtil.notify(NOTIFICATION_SAVE_PROOF_RELATED_DATA, notificationBuilder)

    }

    companion object {
        const val KEY_PROOF_HASH = "KEY_PROOF_HASH"
        const val KEY_SAVE_DIRECTORY_URI = "KEY_SAVE_DIRECTORY_URI"

        fun saveProofAs(context: Context, proof: Proof, saveDirectory: Uri) {
            val inputData = workDataOf(
                KEY_PROOF_HASH to proof.hash,
                KEY_SAVE_DIRECTORY_URI to saveDirectory.toString()
            )
            val saveProofRelatedData = OneTimeWorkRequestBuilder<SaveProofRelatedDataWorker>()
                .setInputData(inputData)
                .build()
            WorkManager.getInstance(context).enqueue(saveProofRelatedData)
        }
    }
}