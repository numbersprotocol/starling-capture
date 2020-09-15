package io.numbersprotocol.starlingcapture.feature.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.numbersprotocol.starlingcapture.collector.ProofCollector
import io.numbersprotocol.starlingcapture.util.MimeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class CameraViewModel(private val proofCollector: ProofCollector) : ViewModel() {

    fun storeImage(cachedMediaFile: File) = viewModelScope.launch(Dispatchers.IO) {
        proofCollector.storeAndCollect(cachedMediaFile, MimeType.JPEG)
        if (cachedMediaFile.exists()) cachedMediaFile.delete()
    }
}