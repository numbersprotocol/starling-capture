package io.numbersprotocol.starlingcapture.data.serialization

import com.squareup.moshi.Moshi
import io.numbersprotocol.starlingcapture.data.information.InformationRepository
import io.numbersprotocol.starlingcapture.data.proof.Proof
import io.numbersprotocol.starlingcapture.data.signature.SignatureRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

object Serialization : KoinComponent {

    private val moshi: Moshi by inject()
    private val informationRepository: InformationRepository by inject()
    private val signatureRepository: SignatureRepository by inject()

    suspend fun generateInformationJson(proof: Proof): String {
        val sortedProofInformation = SortedProofInformation.create(proof, informationRepository)
        return sortedProofInformation.toJson()
    }

    suspend fun generateSignaturesJson(proof: Proof): String {
        val signatures = signatureRepository.getByProof(proof)
        val jsonAdapter = moshi.adapter(List::class.java)
        return jsonAdapter.toJson(signatures)
    }
}