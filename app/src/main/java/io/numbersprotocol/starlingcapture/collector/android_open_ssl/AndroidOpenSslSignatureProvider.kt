package io.numbersprotocol.starlingcapture.collector.android_open_ssl

import android.content.Context
import androidx.work.WorkerParameters
import io.numbersprotocol.starlingcapture.collector.SignatureProvider
import io.numbersprotocol.starlingcapture.data.preference.PreferenceRepository
import io.numbersprotocol.starlingcapture.data.signature.Signature
import io.numbersprotocol.starlingcapture.util.androidOpenSslSignatureProvider
import io.numbersprotocol.starlingcapture.util.signWithSha256AndEcdsa
import org.koin.core.KoinComponent
import org.koin.core.inject

class AndroidOpenSslSignatureProvider(
    context: Context,
    params: WorkerParameters
) : SignatureProvider(context, params), KoinComponent {

    override val name = androidOpenSslSignatureProvider

    private val preferenceRepository: PreferenceRepository by inject()

    override suspend fun provide(serialized: String): Collection<Signature> {
        val privateKey = preferenceRepository.defaultPrivateKey
        val publicKey = preferenceRepository.defaultPublicKey
        val signature = serialized.signWithSha256AndEcdsa(privateKey)

        return setOf(Signature(hash, name, signature, publicKey))
    }
}