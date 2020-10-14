package io.numbersprotocol.starlingcapture.feature.e_signature

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbersprotocol.starlingcapture.util.Event

class ESignatureViewModel : ViewModel() {

    val isOkEnabled = MutableLiveData(false)
    val onOkClickedEvent = MutableLiveData<Event<Unit>>()

    fun onOkClicked() {
        isOkEnabled.value = false
        onOkClickedEvent.value = Event(Unit)
    }
}