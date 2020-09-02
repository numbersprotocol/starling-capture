package io.numbersprotocol.starlingcapture.publisher.numbers_storage

import androidx.lifecycle.*
import io.numbersprotocol.starlingcapture.util.Event
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import retrofit2.HttpException

class NumbersStoragePublisherViewModel(
    private val numbersStorageApi: NumbersStorageApi
) : ViewModel() {

    val hasLoggedIn = numbersStoragePublisherConfig.isEnabledLiveData

    val userNameDisplay = numbersStoragePublisherConfig.userNameLiveData

    val email = MutableLiveData(numbersStoragePublisherConfig.email)
    val emailError = NumbersStorageApi.createEmailErrorLiveData(email)
    val emailDisplay = numbersStoragePublisherConfig.emailLiveData

    val password = MutableLiveData("")
    val passwordError = NumbersStorageApi.createPasswordErrorLiveData(password)

    val isValid = emailError.asFlow().combine(passwordError.asFlow()) { emailError, passwordError ->
        emailError == null && passwordError == null
    }.asLiveData(timeoutInMs = 0)

    val isWaitingForServerResponse = MutableLiveData(false)
    val errorEvent = MutableLiveData<Event<String>>()
    val signUpEvent = MutableLiveData<Event<Unit>>()

    fun signUp() {
        signUpEvent.value = Event(Unit)
    }

    fun login() = viewModelScope.launch {
        isWaitingForServerResponse.value = true
        try {
            val createdToken =
                "token ${numbersStorageApi.login(email.value!!, password.value!!).authToken}"
            val userInformation = numbersStorageApi.getUserInformation(createdToken)
            numbersStoragePublisherConfig.apply {
                authToken = createdToken
                userName = userInformation.userName
                email = userInformation.email
                isEnabled = true
            }
        } catch (e: HttpException) {
            errorEvent.value = Event(NumbersStorageApi.formatErrorResponse(e))
        } catch (e: Exception) {
            errorEvent.value = Event(e.message ?: e.toString())
        } finally {
            isWaitingForServerResponse.value = false
        }
    }

    fun logout() = viewModelScope.launch {
        isWaitingForServerResponse.value = true
        try {
            numbersStoragePublisherConfig.isEnabled = false
            numbersStorageApi.logout(numbersStoragePublisherConfig.authToken)
            numbersStoragePublisherConfig.authToken = ""
        } catch (e: Exception) {
            errorEvent.value = Event(e.message ?: e.toString())
        } finally {
            isWaitingForServerResponse.value = false
        }
    }
}