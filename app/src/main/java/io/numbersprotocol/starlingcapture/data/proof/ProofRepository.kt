package io.numbersprotocol.starlingcapture.data.proof

import android.content.Context
import io.numbersprotocol.starlingcapture.data.RawFileRepository
import io.numbersprotocol.starlingcapture.data.RawFileRepositoryImpl
import io.numbersprotocol.starlingcapture.data.attached_image.AttachedImageRepository

class ProofRepository(
    context: Context,
    private val proofDao: ProofDao,
    private val attachedImageRepository: AttachedImageRepository
) : RawFileRepository by RawFileRepositoryImpl(context) {

    fun getAllWithDataSource() = proofDao.queryAllWithDataSource()

    suspend fun getAll() = proofDao.queryAll()

    suspend fun getByHash(hash: String) = proofDao.queryByHash(hash)

    suspend fun add(proof: Proof) = proofDao.insert(proof)

    suspend fun remove(proofs: Collection<Proof>): Int {
        var deleteCount = 0
        proofs.forEach { proof -> deleteCount += remove(proof) }
        return deleteCount
    }

    suspend fun remove(proof: Proof): Int {
        val deleteCount = proofDao.delete(proof)
        attachedImageRepository.removeRawFileByProof(proof)
        removeRawFile(proof)
        return deleteCount
    }

    private fun removeRawFile(proof: Proof) = removeRawFile(getRawFile(proof))

    fun getRawFile(proof: Proof) = getRawFile("${proof.hash}.${proof.mimeType.extension}")
}