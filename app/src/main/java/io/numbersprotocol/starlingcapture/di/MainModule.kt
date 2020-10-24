package io.numbersprotocol.starlingcapture.di

import androidx.navigation.fragment.NavHostFragment
import androidx.room.Room
import androidx.work.OneTimeWorkRequestBuilder
import coil.ImageLoader
import coil.fetch.VideoFrameFileFetcher
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.collector.ProofCollector
import io.numbersprotocol.starlingcapture.collector.android_open_ssl.AndroidOpenSslSignatureProvider
import io.numbersprotocol.starlingcapture.collector.infosnapshot.InfoSnapshotProvider
import io.numbersprotocol.starlingcapture.collector.zion.SessionSignature
import io.numbersprotocol.starlingcapture.collector.zion.ZionApi
import io.numbersprotocol.starlingcapture.data.AppDataBase
import io.numbersprotocol.starlingcapture.data.caption.CaptionRepository
import io.numbersprotocol.starlingcapture.data.information.InformationRepository
import io.numbersprotocol.starlingcapture.data.preference.PreferenceRepository
import io.numbersprotocol.starlingcapture.data.proof.ProofRepository
import io.numbersprotocol.starlingcapture.data.publish_history.PublishHistoryRepository
import io.numbersprotocol.starlingcapture.data.signature.SignatureRepository
import io.numbersprotocol.starlingcapture.feature.camera.CameraFragment
import io.numbersprotocol.starlingcapture.feature.camera.CameraViewModel
import io.numbersprotocol.starlingcapture.feature.ccapi.CcapiFragment
import io.numbersprotocol.starlingcapture.feature.ccapi.CcapiViewModel
import io.numbersprotocol.starlingcapture.feature.information.InformationFragment
import io.numbersprotocol.starlingcapture.feature.information.InformationViewModel
import io.numbersprotocol.starlingcapture.feature.information_provider_config.InformationProviderConfigFragment
import io.numbersprotocol.starlingcapture.feature.information_provider_config.InformationProviderConfigViewModel
import io.numbersprotocol.starlingcapture.feature.proof.ProofFragment
import io.numbersprotocol.starlingcapture.feature.proof.ProofViewModel
import io.numbersprotocol.starlingcapture.feature.publisher_config.PublisherConfigFragment
import io.numbersprotocol.starlingcapture.feature.publisher_config.PublisherConfigViewModel
import io.numbersprotocol.starlingcapture.feature.setting.PreferenceFragment
import io.numbersprotocol.starlingcapture.feature.setting.SettingFragment
import io.numbersprotocol.starlingcapture.feature.storage.StorageFragment
import io.numbersprotocol.starlingcapture.feature.storage.StorageViewModel
import io.numbersprotocol.starlingcapture.feature.zion.ZionFragment
import io.numbersprotocol.starlingcapture.feature.zion.ZionViewModel
import io.numbersprotocol.starlingcapture.publisher.PublisherManager
import io.numbersprotocol.starlingcapture.source.canon.CanonCameraControlProvider
import io.numbersprotocol.starlingcapture.util.MimeType
import io.numbersprotocol.starlingcapture.util.NotificationUtil
import io.numbersprotocol.starlingcapture.util.SortedSetAdapterFactory
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.experimental.builder.create
import org.koin.experimental.builder.single

val mainModule = module {

    single { NotificationUtil(androidContext()) }

    single {
        Moshi.Builder()
            .add(MimeType.JsonAdapter())
            .add(SortedSetAdapterFactory())
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDataBase::class.java,
            AppDataBase::javaClass.name
        ).build()
    }

    single { get<AppDataBase>().proofDao() }
    single<ProofRepository>()

    single { get<AppDataBase>().informationDao() }
    single<InformationRepository>()

    single { get<AppDataBase>().signatureDao() }
    single<SignatureRepository>()

    single { get<AppDataBase>().captionDao() }
    single<CaptionRepository>()

    single { get<AppDataBase>().publishHistoryDao() }
    single<PublishHistoryRepository>()

    single<PreferenceRepository>()

    single {
        create<ProofCollector>().apply {
            addProvideInformationRequestBuilder(OneTimeWorkRequestBuilder<InfoSnapshotProvider>())
            addProvideSignatureRequestBuilder(OneTimeWorkRequestBuilder<AndroidOpenSslSignatureProvider>())
        }
    }

    single<ZionApi>()
    single<SessionSignature>()

    single<PublisherManager>()

    single<CanonCameraControlProvider>()

    fragment { NavHostFragment() }

    viewModel { create<StorageViewModel>() }
    fragment { create<StorageFragment>() }

    viewModel { create<ProofViewModel>() }
    fragment { create<ProofFragment>() }

    viewModel { create<InformationViewModel>() }
    fragment { create<InformationFragment>() }

    fragment { create<SettingFragment>() }
    fragment { create<PreferenceFragment>() }

    viewModel { create<InformationProviderConfigViewModel>() }
    fragment { create<InformationProviderConfigFragment>() }

    viewModel { create<PublisherConfigViewModel>() }
    fragment { create<PublisherConfigFragment>() }

    viewModel { create<ZionViewModel>() }
    fragment { create<ZionFragment>() }

    viewModel { create<CcapiViewModel>() }
    fragment { create<CcapiFragment>() }

    viewModel { create<CameraViewModel>() }
    fragment { create<CameraFragment>() }

    single(named(CoilImageLoader.SmallThumb)) {
        ImageLoader.Builder(androidContext())
            .componentRegistry { add(VideoFrameFileFetcher(androidContext())) }
            .error(R.drawable.ic_broken_image)
            .crossfade(true)
            .build()
    }
    single(named(CoilImageLoader.LargeTransitionThumb)) {
        ImageLoader.Builder(androidContext())
            .componentRegistry { add(VideoFrameFileFetcher(androidContext())) }
            .error(R.drawable.ic_broken_image)
            .build()
    }
}

enum class CoilImageLoader {
    SmallThumb,
    LargeTransitionThumb
}