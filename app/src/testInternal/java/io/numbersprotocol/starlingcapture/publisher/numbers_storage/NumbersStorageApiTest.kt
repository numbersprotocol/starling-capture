package io.numbersprotocol.starlingcapture.publisher.numbers_storage

import io.numbersprotocol.starlingcapture.BuildConfig
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test
import retrofit2.HttpException

class NumbersStorageApiTest {

    private val numbersStorageApi = NumbersStorageApi.create()
    private val userName = "tester"
    private val email = "tester@test.com"
    private val password = "testpassword"

    @Test
    fun testBaseUrlGenerated() {
        assertTrue(BuildConfig.NUMBERS_STORAGE_BASE_URL.isNotEmpty() && BuildConfig.NUMBERS_STORAGE_BASE_URL != "null")
    }

    @Ignore("Numbers Storage Backend")
    @Test
    fun testCreateUser() = runBlocking {
        try {
            val result = numbersStorageApi.createUser(userName, email, password)
            assertTrue(result.userName == userName && result.email == email)
            println(result)
        } catch (e: Exception) {
            handleHttpException(e as HttpException)
        }
    }

    @Ignore("Numbers Storage Backend")
    @Test
    fun testLogin() = runBlocking {
        try {
            val result = numbersStorageApi.login(email, password)
            assertTrue(result.authToken.isNotBlank())
            println(result)
        } catch (e: Exception) {
            handleHttpException(e as HttpException)
        }
    }

    @Ignore("Numbers Storage Backend")
    @Test
    fun testLogout() = runBlocking {
        val token = "token 6f532ad2a412db68c7fe3474714859572486f3b0"
        val result = numbersStorageApi.logout(token)
        println(result.message())
        assertTrue(result.message() == "No Content" || result.message() == "Unauthorized")
    }

    private fun handleHttpException(e: HttpException) {
        println(e)
        val errorString = e.response()?.errorBody()?.string()
        assertNotNull(errorString)
        println(errorString)
    }
}