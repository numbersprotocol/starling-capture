package io.numbersprotocol.starlingcapture.di

import io.numbersprotocol.starlingcapture.publisher.numbers_storage.NumbersStorageApi
import io.numbersprotocol.starlingcapture.publisher.numbers_storage.NumbersStoragePublisherFragment
import io.numbersprotocol.starlingcapture.publisher.numbers_storage.NumbersStoragePublisherViewModel
import io.numbersprotocol.starlingcapture.publisher.numbers_storage.sign_up.NumbersStoragePublisherSignUpFragment
import io.numbersprotocol.starlingcapture.publisher.numbers_storage.sign_up.NumbersStoragePublisherSignUpViewModel
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.experimental.builder.create

val variantModule = module {

    single { NumbersStorageApi.create() }

    viewModel { create<NumbersStoragePublisherViewModel>() }
    fragment { create<NumbersStoragePublisherFragment>() }

    viewModel { create<NumbersStoragePublisherSignUpViewModel>() }
    fragment { create<NumbersStoragePublisherSignUpFragment>() }
}