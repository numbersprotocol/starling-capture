package io.numbersprotocol.starlingcapture.feature.audio

import androidx.lifecycle.*
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.collector.ProofCollector
import io.numbersprotocol.starlingcapture.util.MimeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class AudioViewModel(private val proofCollector: ProofCollector) : ViewModel() {
    val isRecordingAudio = MutableLiveData(false)

    val captureAudioButtonIcon = isRecordingAudio.map { isRecording ->
        when {
            isRecording -> R.drawable.ic_stop
            else -> R.drawable.ic_fiber_manual_record
        }
    }

    fun storeMedia(cachedMediaFile: File, mimeType: MimeType) =
        viewModelScope.launch(Dispatchers.IO) {
            proofCollector.storeAndCollect(cachedMediaFile, mimeType)
            if (cachedMediaFile.exists()) cachedMediaFile.delete()
        }
}