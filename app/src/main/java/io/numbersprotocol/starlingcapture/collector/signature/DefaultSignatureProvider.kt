package io.numbersprotocol.starlingcapture.collector.signature

import android.content.Context
import androidx.work.WorkerParameters
import io.numbersprotocol.starlingcapture.data.information.InformationRepository
import io.numbersprotocol.starlingcapture.data.preference.PreferenceRepository
import io.numbersprotocol.starlingcapture.data.proof.ProofRepository
import io.numbersprotocol.starlingcapture.data.serialization.SortedProofInformation
import io.numbersprotocol.starlingcapture.data.signature.Signature
import io.numbersprotocol.starlingcapture.data.signature.SignatureRepository
import io.numbersprotocol.starlingcapture.util.Crypto
import org.koin.core.KoinComponent
import org.koin.core.inject

class DefaultSignatureProvider(
    context: Context,
    params: WorkerParameters
) : SignatureProvider(context, params), KoinComponent {

    override val provider = Crypto.defaultSignatureProvider

    private val proofRepository: ProofRepository by inject()
    private val informationRepository: InformationRepository by inject()
    private val preferenceRepository: PreferenceRepository by inject()
    private val signatureRepository: SignatureRepository by inject()


    override suspend fun provideSignature(): Result {
        val proof = proofRepository.getByHash(hash)!!
        val sortedProofInformation = SortedProofInformation.create(proof, informationRepository)
        val json = sortedProofInformation.toJson()
        val privateKey = preferenceRepository.defaultPrivateKey
        val publicKey = preferenceRepository.defaultPublicKey
        val signature = Crypto.signWithSha256AndEcdsa(json, privateKey)
        signatureRepository.add(Signature(hash, provider, signature, publicKey))

        return Result.success()
    }
}