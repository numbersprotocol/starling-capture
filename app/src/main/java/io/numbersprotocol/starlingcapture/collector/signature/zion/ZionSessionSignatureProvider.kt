package io.numbersprotocol.starlingcapture.collector.signature.zion

import android.content.Context
import androidx.work.WorkerParameters
import io.numbersprotocol.starlingcapture.collector.signature.SignatureProvider
import io.numbersprotocol.starlingcapture.data.information.InformationRepository
import io.numbersprotocol.starlingcapture.data.proof.ProofRepository
import io.numbersprotocol.starlingcapture.data.serialization.SortedProofInformation
import io.numbersprotocol.starlingcapture.data.signature.Signature
import io.numbersprotocol.starlingcapture.data.signature.SignatureRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class ZionSessionSignatureProvider(
    context: Context,
    params: WorkerParameters
) : SignatureProvider(context, params), KoinComponent {

    override val provider = "Zion"

    private val zionApi: ZionApi by inject()
    private val sessionSignature: SessionSignature by inject()
    private val proofRepository: ProofRepository by inject()
    private val informationRepository: InformationRepository by inject()
    private val signatureRepository: SignatureRepository by inject()

    override suspend fun provideSignature(): Result {

        val proof = proofRepository.getByHash(hash)!!
        val sortedProofInformation = SortedProofInformation.create(proof, informationRepository)
        val json = sortedProofInformation.toJson()
        val signature =
            if (sessionSignature.isEnabled) sessionSignature.signWithSha256AndEcdsa(json)
            else zionApi.signWithSha256AndEthereum(json)
        val publicKeys = if (sessionSignature.isEnabled) """
            Session:
            ${sessionSignature.publicKey}
            
            Receive:
            ${zionApi.getEthereumReceivePublicKey()}
            
            Send:
            ${zionApi.getEthereumSendPublicKey()}
        """.trimIndent()
        else """
            Receive:
            ${zionApi.getEthereumReceivePublicKey()}
            
            Send:
            ${zionApi.getEthereumSendPublicKey()}
        """.trimIndent()
        signatureRepository.add(Signature(hash, provider, signature, publicKeys))

        return Result.success()
    }
}