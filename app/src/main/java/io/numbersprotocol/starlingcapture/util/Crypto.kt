package io.numbersprotocol.starlingcapture.util

import android.security.keystore.KeyProperties
import java.io.File
import java.security.*
import java.security.spec.ECGenParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

object Crypto {

    const val defaultSignatureProvider = "AndroidOpenSSL"

    fun sha256(file: File): String = sha256(file.readBytes())

    fun sha256(string: String): String = sha256(string.toByteArray(Charsets.UTF_8))

    private fun sha256(byteArray: ByteArray): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val digested = messageDigest.digest(byteArray)
        return digested.asHex()
    }

    fun createEcKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_EC,
            defaultSignatureProvider
        )
        keyPairGenerator.initialize(ECGenParameterSpec("secp256r1"))
        return keyPairGenerator.generateKeyPair()
    }

    fun signWithSha256AndEcdsa(message: String, privateKey: String): String {
        val keyFactory =
            KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_EC, defaultSignatureProvider)
        val key = keyFactory.generatePrivate(PKCS8EncodedKeySpec(privateKey.hexAsByteArray()))
        val signer = Signature.getInstance("SHA256withECDSA")
            .apply {
                initSign(key)
                update(message.toByteArray(Charsets.UTF_8))
            }
        return signer.sign().asHex()
    }

    @Suppress("unused")
    private fun verifyWithSha256AndEcdsa(
        message: String,
        signature: String,
        publicKey: String
    ): Boolean {
        val keyFactory =
            KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_EC, defaultSignatureProvider)
        val key = keyFactory.generatePublic(X509EncodedKeySpec(publicKey.hexAsByteArray()))
        val signer = Signature.getInstance("SHA256withECDSA")
            .apply {
                initVerify(key)
                update(message.toByteArray(Charsets.UTF_8))
            }
        return signer.verify(signature.toByteArray(Charsets.UTF_8))
    }
}

fun ByteArray.asHex() =
    joinToString(separator = "") { String.format("%02x", (it.toInt() and 0xFF)) }

fun String.hexAsByteArray() = chunked(2)
    .map { it.toUpperCase(Locale.getDefault()).toInt(16).toByte() }
    .toByteArray()