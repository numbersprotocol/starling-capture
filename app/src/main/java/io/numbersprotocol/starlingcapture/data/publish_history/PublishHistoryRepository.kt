package io.numbersprotocol.starlingcapture.data.publish_history

import io.numbersprotocol.starlingcapture.data.proof.Proof

class PublishHistoryRepository(private val publishHistoryDao: PublishHistoryDao) {

    fun getByProofWithFlow(proof: Proof) = publishHistoryDao.queryByProofHashWithFlow(proof.hash)

    suspend fun addOrUpdate(history: PublishHistory) = publishHistoryDao.upsert(history)
}