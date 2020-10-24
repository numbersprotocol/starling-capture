package io.numbersprotocol.starlingcapture.collector.proofmode

import android.content.Context
import android.os.Build
import androidx.work.OneTimeWorkRequestBuilder
import io.numbersprotocol.starlingcapture.collector.ProofCollector
import io.numbersprotocol.starlingcapture.util.booleanLiveData
import io.numbersprotocol.starlingcapture.util.booleanPref
import org.koin.core.KoinComponent
import org.koin.core.inject

object ProofModeConfig : KoinComponent {

    private val context: Context by inject()
    private val proofCollector: ProofCollector by inject()
    private val proofModeProviderBuilder = OneTimeWorkRequestBuilder<ProofModeProvider>()

    val isSupported = Build.VERSION.SDK_INT <= Build.VERSION_CODES.P

    const val name = "ProofMode"
    private val sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    private const val KEY_IS_ENABLED = "KEY_IS_ENABLED"
    var isEnabled by sharedPreferences.booleanPref(KEY_IS_ENABLED)
        private set
    val isEnabledLiveData = sharedPreferences.booleanLiveData(KEY_IS_ENABLED)

    fun enable() {
        isEnabled = true
        proofCollector.addProvideInformationRequestBuilder(proofModeProviderBuilder)
    }

    fun disable() {
        proofCollector.removeProvideInformationRequestBuilder(proofModeProviderBuilder)
        isEnabled = false
    }
}