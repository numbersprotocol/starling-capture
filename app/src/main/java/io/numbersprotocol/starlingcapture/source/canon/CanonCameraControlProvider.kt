package io.numbersprotocol.starlingcapture.source.canon

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.os.IBinder
import io.numbersprotocol.starlingcapture.util.booleanLiveData
import io.numbersprotocol.starlingcapture.util.booleanPref
import io.numbersprotocol.starlingcapture.util.stringLiveData
import io.numbersprotocol.starlingcapture.util.stringPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import timber.log.Timber

class CanonCameraControlProvider(private val context: Context) : ServiceConnection {

    private val sharedPreferences =
        context.getSharedPreferences(CANON_CAMERA_CONTROL, Context.MODE_PRIVATE)
    private val serviceIntent = Intent(context, CanonCameraControlService::class.java)

    private var isEnabled by sharedPreferences.booleanPref(KEY_IS_ENABLED)
    val isEnabledLiveData = sharedPreferences.booleanLiveData(KEY_IS_ENABLED)

    var address by sharedPreferences.stringPref(KEY_ADDRESS, DEFAULT_ADDRESS)
    val addressLiveData = sharedPreferences.stringLiveData(KEY_ADDRESS, DEFAULT_ADDRESS)

    @ExperimentalCoroutinesApi
    private val canonCameraControlApi = MutableStateFlow<CanonCameraControlApi?>(null)

    @ExperimentalCoroutinesApi
    val liveView = canonCameraControlApi
        .filterNotNull()
        .onEach {
            it.waitUntilConnected { e -> Timber.e(e) }
            it.startLiveView()
        }
        .flatMapLatest { generateLiveViewFlow(it) }
        .map { BitmapFactory.decodeStream(it) }
        .flowOn(Dispatchers.IO)

    @ExperimentalCoroutinesApi
    fun initialize() {
        if (isEnabled) enable()
    }

    @ExperimentalCoroutinesApi
    fun enable() = synchronized(this) {
        if (!isEnabled) {
            isEnabled = true
            startService()
            bindService()
            canonCameraControlApi.value = CanonCameraControlApi.create(address)
        }
    }

    private fun startService() {
        serviceIntent.putExtra(CanonCameraControlService.CAMERA_ADDRESS, address)
        context.startForegroundService(serviceIntent)
    }

    private fun bindService() = context.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE)

    private fun generateLiveViewFlow(canonCameraControlApi: CanonCameraControlApi) = flow {
        while (true) {
            Timber.v("getting live view...")
            try {
                emit(canonCameraControlApi.getLiveView().byteStream())
            } catch (e: HttpException) {
                if (e.code() == 503) Timber.w("Service is currently unavailable. Is the shooting processing in progress?")
                else Timber.e(e)
            }
        }
    }

    override fun onServiceConnected(name: ComponentName, binder: IBinder) {
        val service = (binder as CanonCameraControlService.Binder).service
        service.addBeforeDestroy { isEnabled = false }
    }

    override fun onServiceDisconnected(name: ComponentName?) {}

    @ExperimentalCoroutinesApi
    fun disable() = synchronized(this) {
        canonCameraControlApi.value = null
        isEnabled = false
        unbindService()
        stopService()
    }

    private fun unbindService() = context.unbindService(this)

    private fun stopService() = context.stopService(serviceIntent)

    companion object {
        const val DEFAULT_ADDRESS = "192.168.1.2:8080"
        private const val CANON_CAMERA_CONTROL = "CANON_CAMERA_CONTROL"
        private const val KEY_IS_ENABLED = "KEY_IS_ENABLED"
        private const val KEY_ADDRESS = "KEY_ADDRESS"
    }
}