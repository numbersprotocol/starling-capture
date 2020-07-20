package io.numbersprotocol.starlingcapture.feature.storage

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.toLiveData
import io.numbersprotocol.starlingcapture.collector.ProofCollector
import io.numbersprotocol.starlingcapture.data.proof.Proof
import io.numbersprotocol.starlingcapture.data.proof.ProofRepository
import io.numbersprotocol.starlingcapture.source.InternalCameraProvider
import io.numbersprotocol.starlingcapture.util.Event
import kotlinx.coroutines.launch

class StorageViewModel(
    savedStateHandle: SavedStateHandle,
    private val proofRepository: ProofRepository,
    proofCollector: ProofCollector
) : ViewModel() {

    val proofList = proofRepository.getAllWithDataSource().toLiveData(pageSize = 20)
    val newProofEvent = MutableLiveData<Event<Unit>>()
    private val internalCameraProvider = InternalCameraProvider(proofCollector, savedStateHandle)

    fun addProof() {
        newProofEvent.value = Event(Unit)
    }

    fun createImageCaptureIntent(fragment: Fragment) =
        internalCameraProvider.createImageCaptureIntent(fragment)

    fun createVideoCaptureIntent(fragment: Fragment) =
        internalCameraProvider.createVideoCaptureIntent(fragment)

    fun storeMedia() = viewModelScope.launch { internalCameraProvider.storeMedia() }

    fun cleanUpMediaOnFail() = internalCameraProvider.removeCachedMediaFile()

    fun deleteProof(items: Iterable<Proof>) {
        // Copy the items to avoid the original iterable has been modified in other place before deletion.
        val targets = items.toSet()
        viewModelScope.launch { proofRepository.remove(targets) }
    }
}