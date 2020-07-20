package io.numbersprotocol.starlingcapture.source

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Parcelable
import android.provider.MediaStore
import androidx.annotation.StringRes
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import io.numbersprotocol.starlingcapture.BuildConfig.APPLICATION_ID
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.collector.ProofCollector
import io.numbersprotocol.starlingcapture.util.MimeType
import io.numbersprotocol.starlingcapture.util.decodeToBitmap
import io.numbersprotocol.starlingcapture.util.rotate
import io.numbersprotocol.starlingcapture.util.writeBitmap
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

class InternalCameraProvider(
    private val proofCollector: ProofCollector,
    private val savedStateHandle: SavedStateHandle
) {

    private var currentCachedMediaFile: MediaFile? = savedStateHandle.run {
        if (contains(CURRENT_CACHED_MEDIA_FILE)) get(CURRENT_CACHED_MEDIA_FILE)
        else null
    }

    fun createImageCaptureIntent(fragment: Fragment) = createCameraIntent(
        fragment,
        SupportedAction.IMAGE
    )

    fun createVideoCaptureIntent(fragment: Fragment) = createCameraIntent(
        fragment,
        SupportedAction.VIDEO
    )

    private fun createCameraIntent(fragment: Fragment, action: SupportedAction): Intent {
        val intent = Intent(action.intentAction)
        if (intent.resolveActivity(fragment.requireContext().packageManager) == null) {
            error("No activity component can handle ${MediaStore.ACTION_IMAGE_CAPTURE} intent.")
        }

        val directory = fragment.requireContext().cacheDir
        val cachedMediaFile = createCachedMediaFile(directory, action.mimeType)
        val mediaUri = FileProvider.getUriForFile(
            fragment.requireContext(),
            "$APPLICATION_ID.provider",
            cachedMediaFile
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri)
        return intent
    }

    private fun createCachedMediaFile(directory: File, mimeType: MimeType): File {
        val timestamp = System.currentTimeMillis()
        val mediaFile = File.createTempFile("$timestamp", ".${mimeType.extension}", directory)
        currentCachedMediaFile = MediaFile(mediaFile.absolutePath, mimeType).also {
            savedStateHandle.set(CURRENT_CACHED_MEDIA_FILE, it)
        }
        return mediaFile
    }

    suspend fun storeMedia() = withContext(Dispatchers.IO) {
        currentCachedMediaFile?.also {
            Timber.i("Storing media file from ${it.path} to intenral storage.")
            val cachedMediaFile = File(it.path)
            if (!cachedMediaFile.exists()) error("Cached media file does not exist: ${it.path}")
            if (it.mimeType == MimeType.JPEG) correctImageOrientation(cachedMediaFile)
            proofCollector.storeAndCollect(cachedMediaFile, it.mimeType)
            removeCachedMediaFile()
        } ?: error("Access cached media file before creating image or video capture intent.")
    }

    private fun correctImageOrientation(imageFile: File) {
        val exifInterface = ExifInterface(imageFile)
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        Timber.i("Original image orientation: $orientation")
        val originalBitmap = imageFile.decodeToBitmap()
        val rotatedBitmap = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> originalBitmap.rotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> originalBitmap.rotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> originalBitmap.rotate(270f)
            else -> originalBitmap
        }
        imageFile.writeBitmap(rotatedBitmap, Bitmap.CompressFormat.JPEG)
    }

    fun removeCachedMediaFile() {
        currentCachedMediaFile?.also {
            val cachedMediaFile = File(it.path)
            if (cachedMediaFile.exists()) cachedMediaFile.delete()
        }
    }

    enum class SupportedAction(
        @StringRes val title: Int,
        val intentAction: String,
        val mimeType: MimeType
    ) {
        IMAGE(R.string.take_picture, MediaStore.ACTION_IMAGE_CAPTURE, MimeType.JPEG),
        VIDEO(R.string.take_video, MediaStore.ACTION_VIDEO_CAPTURE, MimeType.MP4);

        companion object {
            fun from(title: String, context: Context): SupportedAction {
                values().forEach { if (context.getString(it.title) == title) return it }
                error("Cannot find the supported action with given title: $title")
            }
        }
    }

    @Parcelize
    data class MediaFile(
        val path: String,
        val mimeType: MimeType
    ) : Parcelable

    companion object {
        private const val CURRENT_CACHED_MEDIA_FILE = "CURRENT_CACHED_MEDIA_FILE"
    }
}