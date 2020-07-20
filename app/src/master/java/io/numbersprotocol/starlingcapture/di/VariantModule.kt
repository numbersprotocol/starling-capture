package io.numbersprotocol.starlingcapture.di

import io.numbersprotocol.starlingcapture.publisher.sample.SamplePublisherFragment
import io.numbersprotocol.starlingcapture.publisher.sample.SamplePublisherViewModel
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val variantModule = module {

    viewModel { SamplePublisherViewModel() }
    fragment { SamplePublisherFragment() }
}