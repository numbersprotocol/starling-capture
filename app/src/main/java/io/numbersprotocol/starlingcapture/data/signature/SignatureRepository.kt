package io.numbersprotocol.starlingcapture.data.signature

import io.numbersprotocol.starlingcapture.data.proof.Proof

class SignatureRepository(private val signatureDao: SignatureDao) {

    suspend fun getByProof(proof: Proof) = signatureDao.queryByProofHash(proof.hash)

    fun getByProofWithLiveData(proof: Proof) = signatureDao.queryByProofHashWithLiveData(proof.hash)

    suspend fun add(vararg signature: Signature) = signatureDao.insert(*signature)
}