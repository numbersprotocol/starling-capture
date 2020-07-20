@file:Suppress("unused")

package io.numbersprotocol.starlingcapture.source.canon

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.delay
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

interface CanonCameraControlApi {

    @GET(".")
    suspend fun listSupportedApis(): Map<String, List<Api>>

    @GET("{version}/event/polling")
    suspend fun poll(@Path("version") version: String = DEFAULT_VERSION): Status

    @GET
    @Streaming
    suspend fun getContent(@Url url: String): ResponseBody

    @POST("{version}/shooting/liveview")
    suspend fun startLiveView(
        @Body body: LiveViewSettings = LiveViewSettings(
            LiveViewSettings.Size.Medium,
            LiveViewSettings.CameraDisplay.On
        ),
        @Path("version") version: String = DEFAULT_VERSION
    )

    @GET("{version}/shooting/liveview/flip")
    @Streaming
    suspend fun getLiveView(@Path("version") version: String = DEFAULT_VERSION): ResponseBody

    companion object {
        private const val DEFAULT_VERSION = "ver100"
        fun create(address: String): CanonCameraControlApi = Retrofit.Builder()
            .baseUrl("http://${address}/ccapi/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(CanonCameraControlApi::class.java)
    }
}

suspend fun CanonCameraControlApi.waitUntilConnected(
    retryInterval: Long = 1000L,
    onError: (Exception) -> Unit
): Map<String, List<Api>> {
    while (true) {
        try {
            return listSupportedApis()
        } catch (e: Exception) {
            onError(e)
        }
        delay(retryInterval)
    }
}

@JsonClass(generateAdapter = true)
data class Api(
    val url: String,
    val get: Boolean,
    val post: Boolean,
    val put: Boolean,
    val delete: Boolean
)

@JsonClass(generateAdapter = true)
data class Status(
    @Json(name = "addedcontents")
    val addedContents: List<String>?,
    @Json(name = "updatedcontents")
    val updatedContents: List<String>?
)

@JsonClass(generateAdapter = true)
data class LiveViewSettings(
    @Json(name = "liveviewsize")
    val size: Size,
    @Json(name = "cameradisplay")
    val cameraDisplay: CameraDisplay
) {
    enum class Size {
        @Json(name = "off")
        Off,

        @Json(name = "small")
        Small,

        @Json(name = "medium")
        Medium
    }

    enum class CameraDisplay {
        @Json(name = "on")
        On,

        @Json(name = "off")
        Off
    }
}