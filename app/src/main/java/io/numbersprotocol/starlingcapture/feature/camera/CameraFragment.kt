package io.numbersprotocol.starlingcapture.feature.camera

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Mode
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.databinding.FragmentCameraBinding
import io.numbersprotocol.starlingcapture.util.MimeType
import io.numbersprotocol.starlingcapture.util.snack
import kotlinx.android.synthetic.main.fragment_camera.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class CameraFragment : Fragment() {

    private val cameraViewModel: CameraViewModel by viewModel()
    private lateinit var videoFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        videoFile = createCachedMediaFile(MimeType.MP4)

        // Support portrait orientation only.
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentCameraBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = cameraViewModel
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeCameraView()
        navBackButton.setOnClickListener { findNavController().navigateUp() }
        cameraViewModel.cameraMode.observe(viewLifecycleOwner) { onModeUpdated(it) }
    }

    private fun initializeCameraView() {
        cameraView.setLifecycleOwner(viewLifecycleOwner)
        cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                val cachedMediaFile = createCachedMediaFile(MimeType.JPEG)
                result.toFile(cachedMediaFile) { cameraViewModel.storeMedia(it!!, MimeType.JPEG) }
                snack(R.string.media_captured, anchorView = captureButton)
            }

            override fun onVideoTaken(result: VideoResult) {
                super.onVideoTaken(result)
                cameraViewModel.storeMedia(result.file, MimeType.MP4)
                snack(R.string.media_captured, anchorView = captureButton)
            }

            override fun onVideoRecordingStart() {
                super.onVideoRecordingStart()
                cameraViewModel.isTakingVideo.value = true
            }

            override fun onVideoRecordingEnd() {
                super.onVideoRecordingEnd()
                cameraViewModel.isTakingVideo.value = false
            }
        })
    }

    private fun onModeUpdated(mode: Mode) {
        cameraView.mode = mode
        if (mode == Mode.PICTURE) captureButton.setOnClickListener { cameraView.takePicture() }
        else captureButton.setOnClickListener { onRecordClicked() }
    }

    private fun onRecordClicked() {
        if (cameraView.isTakingVideo) cameraView.stopVideo()
        else {
            videoFile = createCachedMediaFile(MimeType.MP4)
            cameraView.takeVideo(videoFile)
        }
    }

    private fun createCachedMediaFile(mimeType: MimeType): File {
        val timestamp = System.currentTimeMillis()
        return File.createTempFile(
            "$timestamp",
            ".${mimeType.extension}",
            requireContext().cacheDir
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        removeCachedVideoFile()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    private fun removeCachedVideoFile() {
        if (videoFile.exists()) videoFile.delete()
    }
}