package io.numbersprotocol.starlingcapture

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import coil.Coil
import coil.ImageLoader
import coil.fetch.VideoFrameFileFetcher
import io.numbersprotocol.starlingcapture.collector.signature.zion.ZionApi
import io.numbersprotocol.starlingcapture.data.preference.PreferenceRepository
import io.numbersprotocol.starlingcapture.di.mainModule
import io.numbersprotocol.starlingcapture.di.variantModule
import io.numbersprotocol.starlingcapture.source.canon.CanonCameraControlProvider
import io.numbersprotocol.starlingcapture.util.NotificationUtil
import io.numbersprotocol.starlingcapture.util.asHex
import io.numbersprotocol.starlingcapture.util.createEcKeyPair
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin
import timber.log.Timber

@Suppress("unused")
class BaseApplication : Application() {

    private val preferenceRepository: PreferenceRepository by inject()
    private val notificationUtil: NotificationUtil by inject()
    private val zionApi: ZionApi by inject()
    private val canonCameraControlProvider: CanonCameraControlProvider by inject()

    @ExperimentalCoroutinesApi
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        initializeKoin()
        notificationUtil.initialize()
        initializeCoil()
        initializeDarkMode()
        initializeDefaultKeyPair()
        initializeZion()
        initializeCcapi()
    }

    private fun initializeKoin() {
        startKoin {
            androidLogger()
            androidContext(this@BaseApplication)
            fragmentFactory()

            // TODO Await fix for Koin and replace the explicit invocations
            //  of loadModules() and createRootScope() with a single call to modules()
            //  (https://github.com/InsertKoinIO/koin/issues/847)
            koin.loadModules(listOf(mainModule, variantModule))
            koin.createRootScope()
        }
    }

    private fun initializeCoil() {
        val defaultImageLoader = ImageLoader.Builder(applicationContext)
            .componentRegistry { add(VideoFrameFileFetcher(applicationContext)) }
            .error(R.drawable.ic_broken_image)
            .crossfade(true)
            .build()
        Coil.setImageLoader(defaultImageLoader)
    }

    private fun initializeDarkMode() {
        preferenceRepository.darkModeLiveData.observeForever {
            AppCompatDelegate.setDefaultNightMode(it.toInt())
        }
    }

    private fun initializeDefaultKeyPair() = GlobalScope.launch(Dispatchers.Default) {
        preferenceRepository.apply {
            if (defaultPublicKey.isBlank() || defaultPrivateKey.isBlank()) {
                val keyPair = createEcKeyPair()
                defaultPublicKey = keyPair.public.encoded.asHex()
                defaultPrivateKey = keyPair.private.encoded.asHex()
            }
        }
    }

    private fun initializeZion() = GlobalScope.launch(Dispatchers.Default) {
        zionApi.initialize()
    }

    @ExperimentalCoroutinesApi
    private fun initializeCcapi() = canonCameraControlProvider.initialize()
}