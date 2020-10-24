package io.numbersprotocol.starlingcapture.di

import io.numbersprotocol.starlingcapture.data.certified_qr_code.CertifiedQrCodeRepository
import io.numbersprotocol.starlingcapture.publisher.numbers_storage.NumbersStorageApi
import io.numbersprotocol.starlingcapture.publisher.numbers_storage.NumbersStoragePublisherFragment
import io.numbersprotocol.starlingcapture.publisher.numbers_storage.NumbersStoragePublisherViewModel
import io.numbersprotocol.starlingcapture.publisher.numbers_storage.sign_up.NumbersStoragePublisherSignUpFragment
import io.numbersprotocol.starlingcapture.publisher.numbers_storage.sign_up.NumbersStoragePublisherSignUpViewModel
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.experimental.builder.create
import org.koin.experimental.builder.single

val variantModule = module {

    single { NumbersStorageApi.create() }

    single<CertifiedQrCodeRepository>()

    viewModel { create<NumbersStoragePublisherViewModel>() }
    fragment { create<NumbersStoragePublisherFragment>() }

    viewModel { create<NumbersStoragePublisherSignUpViewModel>() }
    fragment { create<NumbersStoragePublisherSignUpFragment>() }
}