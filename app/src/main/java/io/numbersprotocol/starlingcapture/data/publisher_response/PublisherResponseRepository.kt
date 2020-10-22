package io.numbersprotocol.starlingcapture.data.publisher_response

import io.numbersprotocol.starlingcapture.data.proof.Proof

class PublisherResponseRepository(private val publisherResponseDao: PublisherResponseDao) {

    fun getPublishersByProofWithLiveData(proof: Proof) =
        publisherResponseDao.queryPublishersByProofHashWithLiveData(proof.hash)

    fun getByProofAndPublisherWithLiveData(proof: Proof, publisher: String) =
        publisherResponseDao.queryByProofHashAndPublisherWithLiveData(proof.hash, publisher)

    suspend fun add(vararg publisherResponses: PublisherResponse) =
        publisherResponseDao.insert(*publisherResponses)
}