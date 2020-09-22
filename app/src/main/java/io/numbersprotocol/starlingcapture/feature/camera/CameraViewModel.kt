package io.numbersprotocol.starlingcapture.feature.camera

import androidx.lifecycle.*
import com.otaliastudios.cameraview.controls.Mode
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.collector.ProofCollector
import io.numbersprotocol.starlingcapture.util.MimeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.io.File

class CameraViewModel(private val proofCollector: ProofCollector) : ViewModel() {

    val cameraMode = MutableLiveData(Mode.PICTURE)
    val isTakingVideo = MutableLiveData(false)
    val captureButtonIcon =
        combine(cameraMode.asFlow(), isTakingVideo.asFlow()) { mode, isTakingVideo ->
            when {
                mode == Mode.PICTURE -> R.drawable.ic_capture
                isTakingVideo -> R.drawable.ic_stop
                else -> R.drawable.ic_fiber_manual_record
            }
        }.asLiveData(timeoutInMs = 0)

    fun storeMedia(cachedMediaFile: File, mimeType: MimeType) =
        viewModelScope.launch(Dispatchers.IO) {
            proofCollector.storeAndCollect(cachedMediaFile, mimeType)
            if (cachedMediaFile.exists()) cachedMediaFile.delete()
        }

    fun switchMode() {
        cameraMode.value = if (cameraMode.value == Mode.PICTURE) Mode.VIDEO else Mode.PICTURE
    }
}