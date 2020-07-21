package io.numbersprotocol.starlingcapture.collector.signature.zion

import android.content.Context
import androidx.work.WorkerParameters
import io.numbersprotocol.starlingcapture.collector.signature.SignatureProvider
import io.numbersprotocol.starlingcapture.data.signature.Signature
import org.koin.core.KoinComponent
import org.koin.core.inject

class ZionSessionSignatureProvider(
    context: Context,
    params: WorkerParameters
) : SignatureProvider(context, params), KoinComponent {

    override val name = "Zion"

    private val zionApi: ZionApi by inject()
    private val sessionSignature: SessionSignature by inject()

    override suspend fun provide(serialized: String): Signature {
        val signature =
            if (sessionSignature.isEnabled) sessionSignature.signWithSha256AndEcdsa(serialized)
            else zionApi.signWithSha256AndEthereum(serialized)
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

        return Signature(hash, name, signature, publicKeys)
    }
}