package io.numbersprotocol.starlingcapture.feature.ccapi

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import io.numbersprotocol.starlingcapture.source.canon.CanonCameraControlProvider
import io.numbersprotocol.starlingcapture.util.Event
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class CcapiViewModel(
    private val canonCameraControlProvider: CanonCameraControlProvider
) : ViewModel() {

    val isEnabled = canonCameraControlProvider.isEnabledLiveData
    val address = canonCameraControlProvider.addressLiveData
    val editIpAddressAndPortEvent = MutableLiveData<Event<Unit>>()

    @ObsoleteCoroutinesApi
    private val tickerChannel = ticker(delayMillis = 500, initialDelayMillis = 0)

    @ObsoleteCoroutinesApi
    val slateTime = liveData {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        for (event in tickerChannel) emit(dateFormat.format(Date()))
    }

    @ObsoleteCoroutinesApi
    val slateDate = liveData {
        val dateFormat = SimpleDateFormat.getDateInstance()
        for (event in tickerChannel) emit(dateFormat.format(Date()))
    }
    val slatePhotographer = MutableLiveData("")
    val editSlateEvent = MutableLiveData<Event<String>>()

    private var liveViewJob: Job? = null
    val liveView = MutableLiveData<Bitmap?>()

    @ExperimentalCoroutinesApi
    fun enable() {
        canonCameraControlProvider.enable()
        startLiveView()
    }

    @ExperimentalCoroutinesApi
    private fun startLiveView() {
        liveViewJob = viewModelScope.launch(Dispatchers.Main) {
            canonCameraControlProvider.liveView
                .catch { e -> Timber.e(e) }
                .collect { liveView.value = it }
        }
    }

    @ExperimentalCoroutinesApi
    fun disable() {
        stopLiveView()
        canonCameraControlProvider.disable()
    }

    private fun stopLiveView() = liveViewJob?.cancel()

    fun editAddress() {
        editIpAddressAndPortEvent.value = Event(Unit)
    }

    fun setAddress(address: String) {
        canonCameraControlProvider.address = address
    }

    fun editSlate() {
        editSlateEvent.value = Event(slatePhotographer.value ?: "")
    }
}