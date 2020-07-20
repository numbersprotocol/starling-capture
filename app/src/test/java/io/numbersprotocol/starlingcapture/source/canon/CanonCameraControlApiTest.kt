package io.numbersprotocol.starlingcapture.source.canon

import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class CanonCameraControlApiTest {

    private lateinit var canonCameraControlApi: CanonCameraControlApi

    @Before
    fun setUp() {
        canonCameraControlApi = CanonCameraControlApi.create("192.168.1.60:8080")
    }

    @Ignore("require Canon camera with CCAPI")
    @Test
    fun testListSupportedApi() = runBlocking {
        println(canonCameraControlApi.listSupportedApis())
    }

    @Ignore("require Canon camera with CCAPI")
    @Test
    fun testPolling() = runBlocking {
        println(canonCameraControlApi.poll())
    }

    @Ignore("require Canon camera with CCAPI")
    @Test
    fun testGetContent() = runBlocking {
        val url = "http://192.168.1.60:8080/ccapi/ver100/contents/sd/100CANON/IMG_0362.JPG"
        println(canonCameraControlApi.getContent(url).byteStream())
    }

    @Ignore("require Canon camera with CCAPI")
    @Test
    fun testStartLiveView() = runBlocking {
        canonCameraControlApi.startLiveView()
    }

    @Ignore("require Canon camera with CCAPI")
    @Test
    fun testGetLiveView() = runBlocking {
        println(canonCameraControlApi.getLiveView().byteStream())
    }
}