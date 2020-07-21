package io.numbersprotocol.starlingcapture.collector.signature

import android.content.Context
import androidx.work.WorkerParameters
import io.numbersprotocol.starlingcapture.data.preference.PreferenceRepository
import io.numbersprotocol.starlingcapture.data.signature.Signature
import io.numbersprotocol.starlingcapture.util.Crypto
import org.koin.core.KoinComponent
import org.koin.core.inject

class DefaultSignatureProvider(
    context: Context,
    params: WorkerParameters
) : SignatureProvider(context, params), KoinComponent {

    override val name = Crypto.defaultSignatureProvider

    private val preferenceRepository: PreferenceRepository by inject()

    override suspend fun provide(serialized: String): Signature {
        val privateKey = preferenceRepository.defaultPrivateKey
        val publicKey = preferenceRepository.defaultPublicKey
        val signature = Crypto.signWithSha256AndEcdsa(serialized, privateKey)

        return Signature(hash, name, signature, publicKey)
    }
}