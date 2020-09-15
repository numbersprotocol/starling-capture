package io.numbersprotocol.starlingcapture.feature.storage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.toLiveData
import io.numbersprotocol.starlingcapture.data.proof.Proof
import io.numbersprotocol.starlingcapture.data.proof.ProofRepository
import io.numbersprotocol.starlingcapture.util.Event
import kotlinx.coroutines.launch

class StorageViewModel(private val proofRepository: ProofRepository) : ViewModel() {

    val proofList = proofRepository.getAllWithDataSource().toLiveData(pageSize = 20)
    val newProofEvent = MutableLiveData<Event<Unit>>()

    fun addProof() {
        newProofEvent.value = Event(Unit)
    }

    fun deleteProof(items: Iterable<Proof>) {
        // Copy the items to avoid the original iterable has been modified in other place before deletion.
        val targets = items.toSet()
        viewModelScope.launch { proofRepository.remove(targets) }
    }
}