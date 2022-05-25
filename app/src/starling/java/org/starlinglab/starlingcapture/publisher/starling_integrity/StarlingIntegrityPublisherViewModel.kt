package org.starlinglab.starlingcapture.publisher.starling_integrity

import androidx.lifecycle.*
import io.numbersprotocol.starlingcapture.util.Event
import kotlinx.coroutines.launch

class StarlingIntegrityPublisherViewModel : ViewModel() {

    val hasLoggedIn = starlingIntegrityPublisherConfig.isEnabledLiveData
    val loginToken = MutableLiveData("")
    val errorEvent = MutableLiveData<Event<String>>()

    fun login() = viewModelScope.launch {
        starlingIntegrityPublisherConfig.apply {
            authToken = "Bearer ${loginToken.value?.trim()}"
            isEnabled = true
        }
    }

    fun logout() = viewModelScope.launch {
        starlingIntegrityPublisherConfig.isEnabled = false
        starlingIntegrityPublisherConfig.authToken = ""
    }
}