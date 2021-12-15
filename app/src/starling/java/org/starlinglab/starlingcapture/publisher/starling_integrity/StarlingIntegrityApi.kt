package org.starlinglab.starlingcapture.publisher.starling_integrity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.numbersprotocol.starlingcapture.BuildConfig
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

interface StarlingIntegrityApi {

    @Multipart
    @POST("v1/assets/create")
    suspend fun createMedia(
        @Header("Authorization") authToken: String,
        @Part rawFile: MultipartBody.Part,
        @Part("meta") information: String,
        @Part("target_provider") targetProvider: String,
        @Part("caption") caption: String,
        @Part("signature") signatures: String,
        @Part("tag") tag: String
    ): String

    companion object {
        fun create(): StarlingIntegrityApi = Retrofit.Builder()
            .baseUrl(BuildConfig.STARLING_INTEGRITY_BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(240, TimeUnit.SECONDS)
                    .writeTimeout(240, TimeUnit.SECONDS)
                    .readTimeout(240, TimeUnit.SECONDS)
                    .build()
            )
            .build()
            .create(StarlingIntegrityApi::class.java)

//        fun createUserNameErrorLiveData(userName: MutableLiveData<String>) = userName.asFlow()
//            .drop(1)
//            .map {
//                when {
//                    it.isNullOrBlank() -> R.string.required
//                    else -> null
//                }
//            }.asLiveData(timeoutInMs = 0)

//        fun createEmailErrorLiveData(email: MutableLiveData<String>) = email.asFlow()
//            .drop(1)
//            .map {
//                when {
//                    it.isNullOrBlank() -> R.string.required
//                    !it.isEmail() -> R.string.message_invalid_email
//                    else -> null
//                }
//            }.asLiveData(timeoutInMs = 0)

//        fun createPasswordErrorLiveData(password: MutableLiveData<String>) = password.asFlow()
//            .drop(1)
//            .map {
//                when {
//                    it.isNullOrBlank() -> R.string.required
//                    it.length < 8 -> R.string.message_too_short
//                    it.isInt() -> R.string.message_entirely_numeric
//                    else -> null
//                }
//            }.asLiveData(timeoutInMs = 0)

        fun formatErrorResponse(e: HttpException): String {
            val string = e.response()?.errorBody()?.string() ?: return e.message()
            val errorMessages = mutableSetOf<String>()
            return try {
                JSONObject(string).apply {
                    keys().forEach { key ->
                        val array = getJSONArray(key as String)
                        for (i in 0 until array.length()) {
                            errorMessages.add("$key: ${array.getString(i)}")
                        }
                    }
                }
                errorMessages.joinToString(separator = "\n")
            } catch (jsonException: JSONException) {
                Timber.e(jsonException)
                e.message()
            }
        }
    }
}

//@JsonClass(generateAdapter = true)
//data class User(
//    @Json(name = "username")
//    val userName: String,
//    val email: String,
//    val id: Int
//)
//
//@JsonClass(generateAdapter = true)
//data class TokenCreate(
//    @Json(name = "auth_token")
//    val authToken: String
//)