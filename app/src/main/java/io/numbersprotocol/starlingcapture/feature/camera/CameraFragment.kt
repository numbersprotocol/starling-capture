package io.numbersprotocol.starlingcapture.feature.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.databinding.FragmentCameraBinding
import io.numbersprotocol.starlingcapture.util.MimeType
import io.numbersprotocol.starlingcapture.util.snack
import kotlinx.android.synthetic.main.fragment_camera.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class CameraFragment : Fragment() {

    private val cameraViewModel: CameraViewModel by viewModel()

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
    }

    private fun initializeCameraView() {
        cameraView.setLifecycleOwner(viewLifecycleOwner)
        cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                val cachedMediaFile = createCachedMediaFile(MimeType.JPEG)
                result.toFile(cachedMediaFile) { cameraViewModel.storeImage(it!!) }
                snack(R.string.media_captured, anchorView = captureButton)
            }
        })
        captureButton.setOnClickListener { cameraView.takePicture() }
    }

    private fun createCachedMediaFile(@Suppress("SameParameterValue") mimeType: MimeType): File {
        val timestamp = System.currentTimeMillis()
        return File.createTempFile(
            "$timestamp",
            ".${mimeType.extension}",
            requireContext().cacheDir
        )
    }
}