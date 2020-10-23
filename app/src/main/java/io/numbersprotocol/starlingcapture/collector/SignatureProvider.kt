package io.numbersprotocol.starlingcapture.collector

import android.content.Context
import androidx.work.WorkerParameters
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.data.information.InformationRepository
import io.numbersprotocol.starlingcapture.data.proof.ProofRepository
import io.numbersprotocol.starlingcapture.data.serialization.SortedProofInformation
import io.numbersprotocol.starlingcapture.data.signature.Signature
import io.numbersprotocol.starlingcapture.data.signature.SignatureRepository
import org.koin.core.inject

abstract class SignatureProvider(
    context: Context, params: WorkerParameters
) : CollectWorker(context, params) {

    private val proofRepository: ProofRepository by inject()
    private val informationRepository: InformationRepository by inject()
    private val signatureRepository: SignatureRepository by inject()

    override suspend fun collect() {
        notifyStartCollecting(R.string.message_signing_proof_information)
        val proof = proofRepository.getByHash(hash)!!
        val sortedProofInformation = SortedProofInformation.create(proof, informationRepository)
        val json = sortedProofInformation.toJson()
        signatureRepository.add(*provide(json).toTypedArray())
    }

    abstract suspend fun provide(serialized: String): Collection<Signature>
}