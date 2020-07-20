package io.numbersprotocol.starlingcapture.collector.signature.zion

import android.content.Context
import io.numbersprotocol.starlingcapture.util.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SessionSignature(context: Context, private val zionApi: ZionApi) {

    private val sharedPreferences =
        context.getSharedPreferences(SESSION_SIGNATURE, Context.MODE_PRIVATE)

    var isEnabled by sharedPreferences.booleanPref(KEY_IS_ENABLED)
        private set
    val isEnabledLiveData = sharedPreferences.booleanLiveData(KEY_IS_ENABLED)

    var timestamp by sharedPreferences.longPref(KEY_TIMESTAMP)
    val timestampLiveData = sharedPreferences.longLiveData(KEY_TIMESTAMP)

    var duration by sharedPreferences.longPref(KEY_DURATION)
        private set

    private var publicKeySignature by sharedPreferences.stringPref(KEY_PUBLIC_KEY_SIGNATURE)
    val publicKeySignatureLiveData = sharedPreferences.stringLiveData(KEY_PUBLIC_KEY_SIGNATURE)

    var publicKey by sharedPreferences.stringPref(KEY_PUBLIC_KEY)
    val publicKeyLiveData = sharedPreferences.stringLiveData(KEY_PUBLIC_KEY)

    private var privateKey by sharedPreferences.stringPref(KEY_PRIVATE_KEY)
    val privateKeyLiveData = sharedPreferences.stringLiveData(KEY_PRIVATE_KEY)

    private val signingMutex = Mutex()

    fun enable(durationInMinutes: Long) {
        duration = durationInMinutes * 1000 * 60
        isEnabled = true
    }

    suspend fun signWithSha256AndEcdsa(message: String) = signingMutex.withLock {
        if (isTimeout()) createNewSession()
        return@withLock Crypto.signWithSha256AndEcdsa(message, privateKey)
    }

    private fun isTimeout() = System.currentTimeMillis() - timestamp > duration

    suspend fun createNewSession() {
        val keyPair = Crypto.createEcKeyPair()
        publicKeySignature = zionApi.signWithSha256AndEthereum(keyPair.public.encoded.asHex())

        publicKey = keyPair.public.encoded.asHex()
        privateKey = keyPair.private.encoded.asHex()
        timestamp = System.currentTimeMillis()
    }

    fun disable() {
        isEnabled = false

        publicKeySignature = ""
        publicKey = ""
        privateKey = ""
        timestamp = 0L
    }

    companion object {
        private const val SESSION_SIGNATURE = "SESSION_SIGNATURE"
        private const val KEY_IS_ENABLED = "KEY_IS_ENABLED"
        private const val KEY_TIMESTAMP = "KEY_TIMESTAMP"
        private const val KEY_DURATION = "KEY_DURATION"
        private const val KEY_PUBLIC_KEY_SIGNATURE = "KEY_PBULIC_KEY_SIGNATURE"
        private const val KEY_PUBLIC_KEY = "KEY_PUBLIC_KEY"
        private const val KEY_PRIVATE_KEY = "KEY_PRIVATE_KEY"
    }
}