package io.numbersprotocol.starlingcapture.publisher

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.data.proof.Proof
import io.numbersprotocol.starlingcapture.data.proof.ProofRepository
import io.numbersprotocol.starlingcapture.util.NotificationUtil
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.HttpException
import timber.log.Timber

abstract class ProofPublisher(
    val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    abstract val name: String
    lateinit var proofHash: String
    private val proofRepository: ProofRepository by inject()
    private val notificationUtil: NotificationUtil by inject()
    private val notificationId = notificationUtil.createNotificationId()
    private val notificationBuilder: NotificationCompat.Builder =
        NotificationCompat.Builder(context, NotificationUtil.CHANNEL_DEFAULT)
            .setSmallIcon(R.drawable.ic_publish)
            .setContentTitle(context.getString(R.string.publish_proof))

    private fun initialize() {
        proofHash = inputData.getString(KEY_PROOF_HASH)!!
    }

    override suspend fun doWork(): Result {
        initialize()
        notifyStartPublish()
        return try {
            Timber.i("Start to publish proof: $proofHash")
            val result = publish(proofRepository.getByHash(proofHash)!!)
            notifyFinishPublish()
            result
        } catch (e: HttpException) {
            @Suppress("BlockingMethodInNonBlockingContext")
            Timber.e(e.response()?.errorBody()?.string())
            notificationUtil.notifyException(e, notificationId)
            Result.failure()
        } catch (e: Exception) {
            notificationUtil.notifyException(e, notificationId)
            Result.failure()
        }
    }

    private fun notifyStartPublish() {
        notificationBuilder
            .setProgress(0, 0, true)
            .setOngoing(true)
            .setContentText(context.getString(R.string.message_publishing_proof))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$proofHash ${context.getString(R.string.message_is_being_published_to)} $name")
            )
        notificationUtil.notify(notificationId, notificationBuilder)
    }

    private fun notifyFinishPublish() {
        notificationBuilder
            .setProgress(0, 0, false)
            .setOngoing(false)
            .setContentText(context.getString(R.string.message_published_proof))
            .setSmallIcon(R.drawable.ic_done)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$proofHash ${context.getString(R.string.message_has_published_to)} $name")
            )
        notificationUtil.notify(notificationId, notificationBuilder)
    }

    abstract suspend fun publish(proof: Proof): Result

    companion object {
        const val KEY_PROOF_HASH = "KEY_PROOF_HASH"
    }
}