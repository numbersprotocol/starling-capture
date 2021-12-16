package org.starlinglab.starlingcapture.publisher.starling_integrity

import androidx.lifecycle.*
import io.numbersprotocol.starlingcapture.util.Event
import kotlinx.coroutines.launch
import retrofit2.HttpException

class StarlingIntegrityPublisherViewModel(
    private val starlingIntegrityApi: StarlingIntegrityApi
) : ViewModel() {

    val hasLoggedIn = starlingIntegrityPublisherConfig.isEnabledLiveData

//    val userNameDisplay = numbersStoragePublisherConfig.userNameLiveData

//    val email = MutableLiveData(numbersStoragePublisherConfig.email)
//    val emailError = NumbersStorageApi.createEmailErrorLiveData(email)
//    val emailDisplay = starlingIntegrityPublisherConfig.emailDisplay

    val authToken = MutableLiveData("")
//    val passwordError = NumbersStorageApi.createPasswordErrorLiveData(password)

//    val isValid = emailError.asFlow().combine(passwordError.asFlow()) { emailError, passwordError ->
//        emailError == null && passwordError == null
//    }.asLiveData(timeoutInMs = 0)

//    val isWaitingForServerResponse = MutableLiveData(false)
    val errorEvent = MutableLiveData<Event<String>>()
//    val signUpEvent = MutableLiveData<Event<Unit>>()
//
//    fun signUp() {
//        signUpEvent.value = Event(Unit)
//    }

//    fun login() = viewModelScope.launch {
//        isWaitingForServerResponse.value = true
//        try {
//            val createdToken =
//                "token ${starlingIntegrityApi.login(email.value!!, password.value!!).authToken}"
//            val userInformation = starlingIntegrityApi.getUserInformation(createdToken)
//            numbersStoragePublisherConfig.apply {
//                authToken = createdToken
//                userName = userInformation.userName
//                email = userInformation.email
//                isEnabled = true
//            }
//        } catch (e: HttpException) {
//            errorEvent.value = Event(StarlingIntegrityApi.formatErrorResponse(e))
//        } catch (e: Exception) {
//            errorEvent.value = Event(e.message ?: e.toString())
//        } finally {
//            isWaitingForServerResponse.value = false
//        }
//    }

    fun login() = viewModelScope.launch {
        starlingIntegrityPublisherConfig.apply {
            authToken = "Bearer {$authToken.value}"
            isEnabled = true
        }
    }

//    fun logout() = viewModelScope.launch {
//        isWaitingForServerResponse.value = true
//        try {
//            numbersStoragePublisherConfig.isEnabled = false
//            starlingIntegrityApi.logout(numbersStoragePublisherConfig.authToken)
//            numbersStoragePublisherConfig.authToken = ""
//        } catch (e: Exception) {
//            errorEvent.value = Event(e.message ?: e.toString())
//        } finally {
//            isWaitingForServerResponse.value = false
//        }
//    }

    fun logout() = viewModelScope.launch {
        starlingIntegrityPublisherConfig.isEnabled = false
        starlingIntegrityPublisherConfig.authToken = ""
    }
}