package io.numbersprotocol.starlingcapture.di

import org.starlinglab.starlingcapture.publisher.starling_integrity.StarlingIntegrityApi
import org.starlinglab.starlingcapture.publisher.starling_integrity.StarlingIntegrityPublisherFragment
import org.starlinglab.starlingcapture.publisher.starling_integrity.StarlingIntegrityPublisherViewModel
//import io.numbersprotocol.starlingcapture.publisher.numbers_storage.sign_up.NumbersStoragePublisherSignUpFragment
//import io.numbersprotocol.starlingcapture.publisher.numbers_storage.sign_up.NumbersStoragePublisherSignUpViewModel
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val variantModule = module {

    single { StarlingIntegrityApi.create() }

    viewModel { StarlingIntegrityPublisherViewModel(get()) }
    fragment { StarlingIntegrityPublisherFragment() }

//    viewModel { NumbersStoragePublisherSignUpViewModel(get()) }
//    fragment { NumbersStoragePublisherSignUpFragment() }
}