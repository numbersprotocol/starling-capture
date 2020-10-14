package io.numbersprotocol.starlingcapture.data.attached_image

import android.content.Context
import io.numbersprotocol.starlingcapture.data.proof.Proof
import io.numbersprotocol.starlingcapture.util.sha256
import timber.log.Timber
import java.io.File

class AttachedImageRepository(
    context: Context,
    private val attachedImageDao: AttachedImageDao
) {

    suspend fun getByProof(proof: Proof) = attachedImageDao.queryByProofHash(proof.hash)

    fun getByProofWithFlow(proof: Proof) = attachedImageDao.queryByProofHashWithFlow(proof.hash)

    suspend fun add(vararg attachedImage: AttachedImage) = attachedImageDao.insert(*attachedImage)

    private val rawFilesDir = context.filesDir.resolve("raw")

    suspend fun removeRawFileByProof(proof: Proof) {
        val attachedImage = getByProof(proof)
        if (attachedImage != null) {
            val rawFile = getRawFile(attachedImage)
            if (rawFile.exists()) rawFile.delete()
            else Timber.e("Cannot delete the raw file as the file does not exist: $rawFile")
        }
    }

    fun getRawFile(attachedImage: AttachedImage) =
        rawFilesDir.resolve("${attachedImage.fileHash}.${attachedImage.mimeType.extension}")

    /**
     * Copy [file] to add raw file to internal storage.
     * @param file: The original source of raw file which will be copied.
     * @return: The file added in the internal storage. The name of the returned file will be its hash with original extension.
     */
    fun addRawFile(file: File): File {
        val fileHash = file.sha256()
        return file.copyTo(rawFilesDir.resolve("$fileHash.${file.extension}"), overwrite = true)
    }
}