package io.numbersprotocol.starlingcapture.data.information

import io.numbersprotocol.starlingcapture.data.proof.Proof

class InformationRepository(private val informationDao: InformationDao) {

    suspend fun getByProof(proof: Proof) = informationDao.queryByProofHash(proof.hash)

    fun getProvidersByProofWithLiveData(proof: Proof) =
        informationDao.queryProvidersByProofHashWithLiveData(proof.hash)

    fun getByProofAndProviderWithLiveData(proof: Proof, provider: String) =
        informationDao.queryByProofHashAndProviderWithLiveData(proof.hash, provider)

    suspend fun add(vararg information: Information) = informationDao.insert(*information)
}