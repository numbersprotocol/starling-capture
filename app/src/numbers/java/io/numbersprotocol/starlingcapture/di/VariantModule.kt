package io.numbersprotocol.starlingcapture.di

import io.numbersprotocol.starlingcapture.publisher.numbers_storage.NumbersStorageApi
import io.numbersprotocol.starlingcapture.publisher.numbers_storage.NumbersStoragePublisherFragment
import io.numbersprotocol.starlingcapture.publisher.numbers_storage.NumbersStoragePublisherViewModel
import io.numbersprotocol.starlingcapture.publisher.numbers_storage.sign_up.NumbersStoragePublisherSignUpFragment
import io.numbersprotocol.starlingcapture.publisher.numbers_storage.sign_up.NumbersStoragePublisherSignUpViewModel
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val variantModule = module {

    single { NumbersStorageApi.create() }

    viewModel { NumbersStoragePublisherViewModel(get()) }
    fragment { NumbersStoragePublisherFragment() }

    viewModel { NumbersStoragePublisherSignUpViewModel(get()) }
    fragment { NumbersStoragePublisherSignUpFragment() }
}