package io.numbersprotocol.starlingcapture.collector.signature

import android.content.Context
import androidx.work.WorkerParameters
import io.numbersprotocol.starlingcapture.data.preference.PreferenceRepository
import io.numbersprotocol.starlingcapture.data.signature.Signature
import io.numbersprotocol.starlingcapture.util.defaultSignatureProvider
import io.numbersprotocol.starlingcapture.util.signWithSha256AndEcdsa
import org.koin.core.KoinComponent
import org.koin.core.inject

class DefaultSignatureProvider(
    context: Context,
    params: WorkerParameters
) : SignatureProvider(context, params), KoinComponent {

    override val name = defaultSignatureProvider

    private val preferenceRepository: PreferenceRepository by inject()

    override suspend fun provide(serialized: String): Signature {
        val privateKey = preferenceRepository.defaultPrivateKey
        val publicKey = preferenceRepository.defaultPublicKey
        val signature = serialized.signWithSha256AndEcdsa(privateKey)

        return Signature(hash, name, signature, publicKey)
    }
}