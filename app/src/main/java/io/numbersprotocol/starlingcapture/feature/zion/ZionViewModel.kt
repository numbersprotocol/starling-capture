package io.numbersprotocol.starlingcapture.feature.zion

import android.view.View
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.*
import io.numbersprotocol.starlingcapture.collector.signature.zion.SessionSignature
import io.numbersprotocol.starlingcapture.collector.signature.zion.ZionApi
import io.numbersprotocol.starlingcapture.util.Event
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch
import java.text.DateFormat

class ZionViewModel(
    private val zionApi: ZionApi,
    private val sessionSignature: SessionSignature
) : ViewModel() {

    val signWithZion = zionApi.isEnabledLiveData
    val isSessionSignatureEnabled = sessionSignature.isEnabledLiveData
    val enableSessionSignatureEvent = MutableLiveData<Event<Unit>>()

    @ObsoleteCoroutinesApi
    val sessionDurationProgress = liveData {
        val tickerChannel = ticker(delayMillis = 1000, initialDelayMillis = 0)
        for (event in tickerChannel) emit(provideSessionDurationProgress())
    }

    @ObsoleteCoroutinesApi
    val sessionDescription = liveData {
        val tickerChannel = ticker(delayMillis = 1000, initialDelayMillis = 0)
        for (event in tickerChannel) emit(provideSessionDescription())
    }
    val sessionGeneratedTimestamp = sessionSignature.timestampLiveData.map {
        if (it == 0L) ""
        else DateFormat.getInstance().format(it)
    }
    val sessionPublicKey = sessionSignature.publicKeyLiveData
    val sessionPrivateKey = sessionSignature.privateKeyLiveData
    val sessionPublicKeySignature = sessionSignature.publicKeySignatureLiveData

    val errorEvent = MutableLiveData<Event<Throwable>>()

    fun showRecoveryPhrase() = viewModelScope.launch {
        try {
            zionApi.showSeed()
        } catch (e: Exception) {
            errorEvent.value = Event(e)
        }
    }

    fun onSessionSignatureSwitchClick(view: View) {
        if ((view as SwitchCompat).isChecked) {
            enableSessionSignatureEvent.value = Event(Unit)
        } else {
            sessionSignature.disable()
        }
    }

    fun startSession() = viewModelScope.launch {
        try {
            sessionSignature.createNewSession()
        } catch (e: IllegalStateException) {
            errorEvent.value = Event(e)
        }
    }

    private fun provideSessionDurationProgress(): Int {
        return if (sessionSignature.timestamp == 0L) 0
        else {
            val elapsed = System.currentTimeMillis() - sessionSignature.timestamp
            if (elapsed > sessionSignature.duration) 100
            else (elapsed.toDouble() / sessionSignature.duration.toDouble() * 100).toInt()
        }
    }

    private fun provideSessionDescription(): String {
        return if (sessionSignature.timestamp == 0L) ""
        else {
            val elapsed = System.currentTimeMillis() - sessionSignature.timestamp
            if (elapsed > sessionSignature.duration) ""
            else "${elapsed / 1000}/${sessionSignature.duration / 1000}"
        }
    }
}