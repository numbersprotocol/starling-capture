package io.numbersprotocol.starlingcapture.feature.information

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import io.numbersprotocol.starlingcapture.data.information.InformationRepository
import io.numbersprotocol.starlingcapture.data.proof.Proof
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class InformationViewModel(informationRepository: InformationRepository) : ViewModel() {

    val proof = MutableLiveData<Proof>()
    val provider = MutableLiveData<String>()

    @ExperimentalCoroutinesApi
    val informationGroup = combine(
        proof.asFlow().filterNotNull(),
        provider.asFlow().filterNotNull()
    ) { proof: Proof, provider: String -> proof to provider }
        .flatMapLatest { (proof, provider) ->
            informationRepository.getByProofAndProviderWithFlow(
                proof,
                provider
            )
        }.map { it.groupBy { information -> information.type } }
        .asLiveData(timeoutInMs = 0)
}