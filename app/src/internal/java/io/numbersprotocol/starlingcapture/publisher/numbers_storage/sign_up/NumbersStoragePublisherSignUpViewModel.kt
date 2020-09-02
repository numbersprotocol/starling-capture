package io.numbersprotocol.starlingcapture.publisher.numbers_storage.sign_up

import androidx.lifecycle.*
import io.numbersprotocol.starlingcapture.publisher.numbers_storage.NumbersStorageApi
import io.numbersprotocol.starlingcapture.util.Event
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import retrofit2.HttpException

class NumbersStoragePublisherSignUpViewModel(
    private val numbersStorageApi: NumbersStorageApi
) : ViewModel() {

    val userName = MutableLiveData("")
    val userNameError = NumbersStorageApi.createUserNameErrorLiveData(userName)

    val email = MutableLiveData("")
    val emailError = NumbersStorageApi.createEmailErrorLiveData(email)

    val password = MutableLiveData("")
    val passwordError = NumbersStorageApi.createPasswordErrorLiveData(password)

    val isValid = userNameError.asFlow()
        .combine(emailError.asFlow()) { userNameError, emailError ->
            userNameError == null && emailError == null
        }.combine(passwordError.asFlow()) { isValid, passwordError ->
            isValid && passwordError == null
        }.asLiveData(timeoutInMs = 0)

    val isWaitingForServerResponse = MutableLiveData(false)
    val errorEvent = MutableLiveData<Event<String>>()
    val successEvent = MutableLiveData<Event<Unit>>()

    fun createAccount() = viewModelScope.launch {
        isWaitingForServerResponse.value = true
        try {
            numbersStorageApi.createUser(userName.value!!, email.value!!, password.value!!)
            successEvent.value = Event(Unit)
        } catch (e: HttpException) {
            errorEvent.value = Event(NumbersStorageApi.formatErrorResponse(e))
            isWaitingForServerResponse.value = false
        } catch (e: Exception) {
            errorEvent.value = Event(e.message ?: e.toString())
            isWaitingForServerResponse.value = false
        }
    }
}