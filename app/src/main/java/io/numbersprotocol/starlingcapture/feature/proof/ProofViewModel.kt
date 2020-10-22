package io.numbersprotocol.starlingcapture.feature.proof

import androidx.lifecycle.*
import io.numbersprotocol.starlingcapture.data.attached_image.AttachedImageRepository
import io.numbersprotocol.starlingcapture.data.caption.Caption
import io.numbersprotocol.starlingcapture.data.caption.CaptionRepository
import io.numbersprotocol.starlingcapture.data.information.InformationRepository
import io.numbersprotocol.starlingcapture.data.proof.Proof
import io.numbersprotocol.starlingcapture.data.publish_history.PublishHistoryRepository
import io.numbersprotocol.starlingcapture.data.publisher_response.PublisherResponseRepository
import io.numbersprotocol.starlingcapture.data.signature.SignatureRepository
import io.numbersprotocol.starlingcapture.util.Event
import io.numbersprotocol.starlingcapture.util.MimeType
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ProofViewModel(
    private val informationRepository: InformationRepository,
    private val signatureRepository: SignatureRepository,
    private val captionRepository: CaptionRepository,
    private val publishHistoryRepository: PublishHistoryRepository,
    private val attachedImageRepository: AttachedImageRepository,
    private val publisherResponseRepository: PublisherResponseRepository
) : ViewModel() {

    val proof = MutableLiveData<Proof>()
    val informationProviders = proof.switchMap {
        informationRepository.getProvidersByProofWithLiveData(it)
    }
    val signatures = proof.switchMap { signatureRepository.getByProofWithLiveData(it) }
    val isVideo = proof.map { it.mimeType == MimeType.MP4 }
    val hasPublished = proof.switchMap { proof ->
        publishHistoryRepository.getByProofWithFlow(proof).map { it.isNotEmpty() }.asLiveData()
    }
    val caption = proof.switchMap { captionRepository.getByProofWithLiveData(it) }
    val viewMediaEvent = MutableLiveData<Event<Unit>>()
    val editCaptionEvent = MutableLiveData<Event<String>>()
    val attachedImage = proof.switchMap {
        attachedImageRepository.getByProofWithFlow(it).asLiveData()
    }
    val publishers = proof.switchMap {
        publisherResponseRepository.getPublishersByProofWithLiveData(it)
    }

    fun viewMedia() {
        viewMediaEvent.value = Event(Unit)
    }

    fun editCaption() = viewModelScope.launch {
        val caption = captionRepository.getByProof(proof.value!!)?.text ?: ""
        editCaptionEvent.value = Event(caption)
    }

    fun saveCaption(text: String) = viewModelScope.launch {
        captionRepository.addOrUpdate(Caption(proof.value!!.hash, text))
    }
}