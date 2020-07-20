package io.numbersprotocol.starlingcapture.feature.ccapi

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.numbersprotocol.starlingcapture.source.canon.CanonCameraControlProvider
import io.numbersprotocol.starlingcapture.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class CcapiViewModel(
    private val canonCameraControlProvider: CanonCameraControlProvider
) : ViewModel() {

    val isEnabled = canonCameraControlProvider.isEnabledLiveData
    val address = canonCameraControlProvider.addressLiveData
    val editIpAddressAndPortEvent = MutableLiveData<Event<Unit>>()

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
}