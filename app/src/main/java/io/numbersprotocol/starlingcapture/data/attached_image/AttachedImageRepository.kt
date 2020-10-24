package io.numbersprotocol.starlingcapture.data.attached_image

import android.content.Context
import io.numbersprotocol.starlingcapture.data.RawFileRepository
import io.numbersprotocol.starlingcapture.data.RawFileRepositoryImpl
import io.numbersprotocol.starlingcapture.data.proof.Proof

class AttachedImageRepository(
    context: Context,
    private val attachedImageDao: AttachedImageDao
) : RawFileRepository by RawFileRepositoryImpl(context) {

    suspend fun getByProof(proof: Proof) = attachedImageDao.queryByProofHash(proof.hash)

    fun getByProofWithFlow(proof: Proof) = attachedImageDao.queryByProofHashWithFlow(proof.hash)

    suspend fun add(vararg attachedImage: AttachedImage) = attachedImageDao.insert(*attachedImage)

    suspend fun removeRawFileByProof(proof: Proof) {
        val attachedImage = getByProof(proof)
        if (attachedImage != null) removeRawFile(getRawFile(attachedImage))
    }

    fun getRawFile(attachedImage: AttachedImage) =
        getRawFile("${attachedImage.fileHash}.${attachedImage.mimeType.extension}")
}