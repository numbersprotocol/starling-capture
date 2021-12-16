package org.starlinglab.starlingcapture.publisher.starling_integrity

import io.numbersprotocol.starlingcapture.BuildConfig
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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
    }
}