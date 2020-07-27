package io.numbersprotocol.starlingcapture.util

import android.security.keystore.KeyProperties
import java.io.File
import java.security.*
import java.security.spec.ECGenParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

private const val SHA_256 = "SHA-256"

fun File.sha256(): String {
    val digest = MessageDigest.getInstance(SHA_256)
    DigestInputStream(this.inputStream(), digest).use { digestInputStream ->
        val buffer = ByteArray(8192)
        // Read all bytes:
        @Suppress("ControlFlowWithEmptyBody")
        while (digestInputStream.read(buffer, 0, buffer.size) != -1) {
        }
    }
    return digest.digest().asHex()
}

fun String.sha256() = toByteArray(Charsets.UTF_8).sha256()

fun ByteArray.sha256(): String {
    val messageDigest = MessageDigest.getInstance(SHA_256)
    val digested = messageDigest.digest(this)
    return digested.asHex()
}

const val defaultSignatureProvider = "AndroidOpenSSL"

fun createEcKeyPair(): KeyPair {
    val keyPairGenerator = KeyPairGenerator.getInstance(
        KeyProperties.KEY_ALGORITHM_EC,
        defaultSignatureProvider
    )
    keyPairGenerator.initialize(ECGenParameterSpec("secp256r1"))
    return keyPairGenerator.generateKeyPair()
}

fun String.signWithSha256AndEcdsa(privateKey: String): String {
    val keyFactory =
        KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_EC, defaultSignatureProvider)
    val key = keyFactory.generatePrivate(PKCS8EncodedKeySpec(privateKey.hexAsByteArray()))
    val signer = Signature.getInstance("SHA256withECDSA")
        .apply {
            initSign(key)
            update(toByteArray(Charsets.UTF_8))
        }
    return signer.sign().asHex()
}

@Suppress("unused")
fun String.verifyWithSha256AndEcdsa(signature: String, publicKey: String): Boolean {
    val keyFactory =
        KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_EC, defaultSignatureProvider)
    val key = keyFactory.generatePublic(X509EncodedKeySpec(publicKey.hexAsByteArray()))
    val signer = Signature.getInstance("SHA256withECDSA")
        .apply {
            initVerify(key)
            update(toByteArray(Charsets.UTF_8))
        }
    return signer.verify(signature.toByteArray(Charsets.UTF_8))
}