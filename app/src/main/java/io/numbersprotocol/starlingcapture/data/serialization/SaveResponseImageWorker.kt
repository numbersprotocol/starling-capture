package io.numbersprotocol.starlingcapture.data.serialization

import android.content.Context
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.work.*
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.util.MimeType
import io.numbersprotocol.starlingcapture.util.NotificationUtil
import io.numbersprotocol.starlingcapture.util.NotificationUtil.Companion.NOTIFICATION_SAVE_RESPONSE_IMAGE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File

class SaveResponseImageWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private lateinit var imageFile: File
    private lateinit var saveDirectory: DocumentFile
    private val notificationUtil: NotificationUtil by inject()
    private val notificationBuilder =
        NotificationCompat.Builder(context, NotificationUtil.CHANNEL_DEFAULT)
            .setSmallIcon(R.drawable.ic_save_alt)
            .setContentTitle(context.getString(R.string.save_image))

    private fun initialize() {
        imageFile = inputData.getString(KEY_IMAGE_URI)!!.toUri().toFile()
        saveDirectory = DocumentFile.fromTreeUri(
            context,
            inputData.getString(KEY_SAVE_DIRECTORY_URI)!!.toUri()
        )!!
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun doWork(): Result {
        return try {
            notifyWorkerStart()
            initialize()

            withContext(Dispatchers.IO) {
                val targetUri =
                    saveDirectory.createFile("${MimeType.from(imageFile)}", imageFile.name)!!.uri
                imageFile.inputStream()
                    .copyTo(context.contentResolver.openOutputStream(targetUri)!!)
            }

            notifyWorkerSuccess()
            Result.success()
        } catch (e: Exception) {
            notificationUtil.notifyException(e, NOTIFICATION_SAVE_RESPONSE_IMAGE)
            Result.failure()
        }
    }

    private fun notifyWorkerStart() {
        notificationBuilder.setContentText(context.getString(R.string.message_saving_data_to_external))
        notificationBuilder.setProgress(0, 0, true)
        notificationUtil.notify(
            NOTIFICATION_SAVE_RESPONSE_IMAGE,
            notificationBuilder
        )
    }

    private fun notifyWorkerSuccess() {
        notificationBuilder.setContentText(context.getString(R.string.message_saved_data_to_external))
        notificationBuilder.setProgress(0, 0, false)
        notificationUtil.notify(
            NOTIFICATION_SAVE_RESPONSE_IMAGE,
            notificationBuilder
        )
    }

    companion object {
        private const val KEY_IMAGE_URI = "KEY_IMAGE_URI"
        private const val KEY_SAVE_DIRECTORY_URI = "KEY_SAVE_DIRECTORY_URI"

        fun saveImageAs(context: Context, imageUri: Uri, saveDirectory: Uri) {
            val inputData = workDataOf(
                KEY_IMAGE_URI to imageUri.toString(),
                KEY_SAVE_DIRECTORY_URI to saveDirectory.toString()
            )
            val saveResponseImage = OneTimeWorkRequestBuilder<SaveResponseImageWorker>()
                .setInputData(inputData)
                .build()
            WorkManager.getInstance(context).enqueue(saveResponseImage)
        }
    }
}