package io.numbersprotocol.starlingcapture.data.caption

import io.numbersprotocol.starlingcapture.data.proof.Proof

class CaptionRepository(private val captionDao: CaptionDao) {

    suspend fun getByProof(proof: Proof) = captionDao.queryByProofHash(proof.hash)

    fun getByProofWithLiveData(proof: Proof) = captionDao.queryByProofHashWithLiveData(proof.hash)

    suspend fun addOrUpdate(caption: Caption) = captionDao.upsert(caption)
}