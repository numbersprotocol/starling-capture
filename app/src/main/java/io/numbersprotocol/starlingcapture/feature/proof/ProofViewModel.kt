package io.numbersprotocol.starlingcapture.feature.proof

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import io.numbersprotocol.starlingcapture.data.caption.Caption
import io.numbersprotocol.starlingcapture.data.caption.CaptionRepository
import io.numbersprotocol.starlingcapture.data.information.InformationRepository
import io.numbersprotocol.starlingcapture.data.proof.Proof
import io.numbersprotocol.starlingcapture.data.signature.SignatureRepository
import io.numbersprotocol.starlingcapture.util.Event
import io.numbersprotocol.starlingcapture.util.MimeType
import kotlinx.coroutines.launch

class ProofViewModel(
    private val informationRepository: InformationRepository,
    private val signatureRepository: SignatureRepository,
    private val captionRepository: CaptionRepository
) : ViewModel() {

    val proof = MutableLiveData<Proof>()
    val informationProviders = proof.switchMap {
        informationRepository.getProvidersByProofWithLiveData(it)
    }
    val signatures = proof.switchMap {
        signatureRepository.getByProofWithLiveData(it)
    }
    val isVideo = proof.switchMap {
        MutableLiveData(it.mimeType == MimeType.MP4)
    }
    val caption = proof.switchMap {
        captionRepository.getByProofWithLiveData(it)
    }
    val viewMediaEvent = MutableLiveData<Event<Unit>>()
    val editCaptionEvent = MutableLiveData<Event<String>>()

    fun viewMedia() {
        viewMediaEvent.value = Event(Unit)
    }

    fun editCaption() = viewModelScope.launch {
        val caption = captionRepository.getByProof(proof.value!!)?.text ?: ""
        editCaptionEvent.value = Event(caption)
    }

    fun saveCaption(text: String) = viewModelScope.launch {
        captionRepository.insertOrUpdate(Caption(proof.value!!.hash, text))
    }
}