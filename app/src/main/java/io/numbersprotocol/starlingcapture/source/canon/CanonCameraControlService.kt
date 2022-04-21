package io.numbersprotocol.starlingcapture.source.canon

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.app.NotificationCompat
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.collector.ProofCollector
import io.numbersprotocol.starlingcapture.util.MimeType
import io.numbersprotocol.starlingcapture.util.NotificationUtil
import io.numbersprotocol.starlingcapture.util.copyFromInputStream
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.android.ext.android.inject
import retrofit2.HttpException
import timber.log.Timber
import java.io.File
import java.io.InputStream

class CanonCameraControlService : Service(), CoroutineScope by CoroutineScope(Dispatchers.IO) {

    private val notificationUtil: NotificationUtil by inject()
    private val foregroundNotificationId = notificationUtil.createNotificationId()
    private val errorNotificationId = notificationUtil.createNotificationId()
    private lateinit var address: String
    private lateinit var canonCameraControlApi: CanonCameraControlApi
    private val proofCollector: ProofCollector by inject()
    private lateinit var foregroundNotificationBuilder: NotificationCompat.Builder
    private val beforeDestroyCallbacks = mutableSetOf<() -> Unit>()

    @FlowPreview
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
//        bindProcessToWiFiNetwork()
        initializeCanonCameraControlApi(intent)
        startForeground()
        connectToCamera()
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * Default to WiFi network transport (even when Internet in unavailable on that interface) since
     * our camera has no Internet connectivity. This tells Android to still connect to the local IP
     * when mobile data is available on the phone.
     */
    private fun bindProcessToWiFiNetwork() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val request = NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build()
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                connectivityManager.bindProcessToNetwork(network)
            }
        }
        connectivityManager?.requestNetwork(request, networkCallback)
    }

    private fun initializeCanonCameraControlApi(intent: Intent) {
        address = intent.extras!!.getString(CAMERA_ADDRESS)!!
        canonCameraControlApi = CanonCameraControlApi.create(address)
    }

    private fun startForeground() {
        notificationUtil.createChannel(CHANNEL_CCAPI, R.string.canon_ccapi)
        foregroundNotificationBuilder = NotificationCompat
            .Builder(this, CHANNEL_CCAPI)
            .setSmallIcon(R.drawable.ic_linked_camera)
            .setContentTitle(getString(R.string.canon_ccapi))
            .setContentText(getString(R.string.message_monitoring_canon_camera))
            .setContentIntent(notificationUtil.baseActivityIntent)
        startForeground(foregroundNotificationId, foregroundNotificationBuilder.build())
    }

    @FlowPreview
    private fun connectToCamera() = launch {
        Timber.v("Connecting to camera...")
        canonCameraControlApi.waitUntilConnected { e ->
            if (e !is CancellationException) {
                notificationUtil.notifyException(e, foregroundNotificationId)
            }
        }
        Timber.i("Camera has connected.")
        notificationUtil.notify(foregroundNotificationId, foregroundNotificationBuilder)
        getPollingFlow()
            .flatMapConcat { getContentFlow(it) }
            .map { storeAndCollect(it) }
            .catch { e ->
                if (e !is CancellationException) {
                    notificationUtil.notifyException(e, errorNotificationId)
                }
            }.collect()
    }

    private fun getPollingFlow() = flow {
        while (true) {
            Timber.v("Polling...")
            canonCameraControlApi.poll().addedContents?.forEach {
                if (MimeType.isSupported(it)) emit(it)
                else Timber.w("Ignore the unsupported media file type: $it")
            }
            delay(POLLING_INTERVAL_MILLIS)
        }
    }

    private fun getContentFlow(path: String) = flow {
        val url = "http://$address$path"
        Timber.v("Getting content stream at $url")
        emit(MediaStream(canonCameraControlApi.getContent(url).byteStream(), MimeType.fromUrl(url)))
    }.retry { e ->
        (e is HttpException && e.code() == 503).also {
            Timber.e("Getting content stream failed with error: %s", e.message)
            Timber.w("Service is currently unavailable. Is the shooting processing in progress?")
            delay(POLLING_INTERVAL_MILLIS)
        }
    }

    private suspend fun storeAndCollect(mediaStream: MediaStream) {
        Timber.v("Storing new content...")
        val cachedFile = createCachedFile(mediaStream)
        cachedFile.copyFromInputStream(mediaStream.inputStream)
        proofCollector.storeAndCollect(cachedFile, mediaStream.mimeType)
        cachedFile.delete()
        Timber.i("New content stored.")
    }

    private fun createCachedFile(mediaStream: MediaStream) = File.createTempFile(
        "${System.currentTimeMillis()}",
        ".${mediaStream.mimeType.extension}",
        cacheDir
    )

    override fun onBind(intent: Intent?) = Binder(this)

    fun addBeforeDestroy(callback: () -> Unit) = beforeDestroyCallbacks.add(callback)

    override fun onDestroy() {
        beforeDestroyCallbacks.forEach { it() }
        super.onDestroy()
        cancel()
    }

    class Binder(val service: CanonCameraControlService) : android.os.Binder()

    data class MediaStream(
        val inputStream: InputStream,
        val mimeType: MimeType
    )

    companion object {
        const val CAMERA_ADDRESS = "CAMERA_ADDRESS"
        private const val CHANNEL_CCAPI = "CHANNEL_CCAPI"
        private const val POLLING_INTERVAL_MILLIS = 1000L
    }
}