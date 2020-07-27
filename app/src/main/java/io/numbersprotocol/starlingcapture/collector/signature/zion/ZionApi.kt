package io.numbersprotocol.starlingcapture.collector.signature.zion

import android.content.Context
import android.os.Build
import androidx.work.OneTimeWorkRequestBuilder
import com.htc.htcwalletsdk.Export.HtcWalletSdkManager
import com.htc.htcwalletsdk.Export.RESULT
import com.htc.htcwalletsdk.Native.Type.ByteArrayHolder
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.collector.ProofCollector
import io.numbersprotocol.starlingcapture.util.asHex
import io.numbersprotocol.starlingcapture.util.booleanLiveData
import io.numbersprotocol.starlingcapture.util.booleanPref
import io.numbersprotocol.starlingcapture.util.sha256
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*
import kotlin.properties.Delegates

class ZionApi(private val context: Context) : KoinComponent {

    private val sharedPreferences = context.getSharedPreferences(ZION, Context.MODE_PRIVATE)
    private val zkma by lazy { HtcWalletSdkManager.getInstance() }
    private var uniqueId by Delegates.notNull<Long>()
    private val proofCollector: ProofCollector by inject()
    private val provideZionSignatureRequestBuilder =
        OneTimeWorkRequestBuilder<ZionSessionSignatureProvider>()
    private val sessionSignature: SessionSignature by inject()

    private var isEnabled by sharedPreferences.booleanPref(KEY_IS_ENABLED)
    val isEnabledLiveData = sharedPreferences.booleanLiveData(KEY_IS_ENABLED)

    suspend fun initialize() {
        if (isEnabled) enable()
    }

    suspend fun enable() = withContext(Dispatchers.Default) {
        synchronized(this) {
            isEnabled = true
            val result = zkma.init(context)
            if (result != RESULT.SUCCESS) error("Bad result when initializing Zion: $result")
            uniqueId = registerWallet(context.getString(R.string.starling_capture))
            proofCollector.addProvideSignatureRequestBuilder(provideZionSignatureRequestBuilder)
            true
        }
    }

    private fun registerWallet(name: String) = zkma.register(name, name.sha256())

    suspend fun hasCreatedSeed() = withContext(Dispatchers.Default) {
        when (val check = zkma.isSeedExists(uniqueId)) {
            RESULT.SUCCESS -> return@withContext true
            RESULT.E_TEEKM_SEED_NOT_FOUND -> return@withContext false
            else -> error("Bad result when checking Zion seed exist: $check")
        }
    }

    suspend fun createSeed() = withContext(Dispatchers.Default) {
        val result = zkma.createSeed(uniqueId)
        if (result != RESULT.SUCCESS) error("Bad result when creating Zion seed: $result")
    }

    suspend fun restoreSeed() = withContext(Dispatchers.Default) {
        val result = zkma.restoreSeed(uniqueId)
        if (result != RESULT.SUCCESS) error("Bad result when restoring Zion seed: $result")
    }

    suspend fun showSeed() = withContext(Dispatchers.Default) {
        val result = zkma.showSeed(uniqueId)
        if (result != RESULT.SUCCESS) error("Bad result when showing Zion seed: $result")
    }

    suspend fun signWithSha256AndEthereum(message: String) = withContext(Dispatchers.Default) {
        synchronized(this) {
            val json = JSONObject().apply {
                put("path", "m/44'/60'/0'/0/0")
                put("message", JSONObject().apply {
                    put("version", "45")
                    put("data", message.sha256())
                })
            }
            val signature = ByteArrayHolder()
            val result = zkma.signMessage(uniqueId, ETHEREUM_TYPE, json.toString(), signature)
            if (result != RESULT.SUCCESS) error("Bad result when signing message with Zion: $result")
            signature.byteArray.asHex()
        }
    }

    suspend fun getEthereumReceivePublicKey(): String = withContext(Dispatchers.Default) {
        val publicKeyHolder = zkma.getReceivePublicKey(uniqueId, ETHEREUM_TYPE)
        return@withContext publicKeyHolder.key
    }

    suspend fun getEthereumSendPublicKey(): String = withContext(Dispatchers.Default) {
        val publicKeyHolder = zkma.getSendPublicKey(uniqueId, ETHEREUM_TYPE)
        return@withContext publicKeyHolder.key
    }

    suspend fun disable() = withContext(Dispatchers.Default) {
        synchronized(this) {
            sessionSignature.disable()
            proofCollector.removeProvideSignatureRequestBuilder(provideZionSignatureRequestBuilder)
            val result = zkma.deinit()
            if (result != RESULT.SUCCESS) error("Bad result when destroying Zion: $result")
            isEnabled = false
        }
    }

    companion object {
        val deviceName: String = Build.MODEL
        val isSupported = setOf("EXODUS 1", "EXODUS 1S")
            .contains(deviceName.toUpperCase(Locale.ROOT))
        private const val ETHEREUM_TYPE = 60
        private const val ZION = "ZION"
        private const val KEY_IS_ENABLED = "KEY_IS_ENABLED"
    }
}