package io.numbersprotocol.starlingcapture.feature.e_signature

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import io.numbersprotocol.starlingcapture.collector.ProofCollector
import io.numbersprotocol.starlingcapture.databinding.FragmentESignatureBinding
import io.numbersprotocol.starlingcapture.util.MimeType
import io.numbersprotocol.starlingcapture.util.observeEvent
import kotlinx.android.synthetic.main.fragment_e_signature.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class ESignatureFragment(private val proofCollector: ProofCollector) : Fragment() {

    private val eSignatureViewModel: ESignatureViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Support portrait orientation only.
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentESignatureBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = eSignatureViewModel
            fragment = this@ESignatureFragment
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeCameraView()
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        eSignatureViewModel.onOkClickedEvent.observeEvent(viewLifecycleOwner) { cameraView.takePicture() }
    }

    private fun initializeCameraView() {
        cameraView.setLifecycleOwner(viewLifecycleOwner)
        cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                val cachedPhotoFile = createCachedFile(MimeType.JPEG)
                result.toFile(cachedPhotoFile) { storeMedia(cachedPhotoFile) }
            }
        })
    }

    private fun createCachedFile(mimeType: MimeType): File {
        val timestamp = System.currentTimeMillis()
        return File.createTempFile(
            "$timestamp",
            ".${mimeType.extension}",
            requireContext().cacheDir
        )
    }

    fun storeMedia(cachedPhotoFile: File) = lifecycleScope.launch {
        val cachedSignatureFile = createCachedSignatureFile()
        proofCollector.storeAndCollect(
            cachedSignatureFile,
            MimeType.PNG,
            cachedPhotoFile,
            MimeType.JPEG
        )
        removeCachedFile(cachedPhotoFile)
        removeCachedFile(cachedSignatureFile)
        findNavController().navigateUp()
    }

    private fun createCachedSignatureFile(): File {
        val cachedSignatureFile = createCachedFile(MimeType.PNG)
        signaturePad.transparentSignatureBitmap.compress(
            Bitmap.CompressFormat.PNG,
            100,
            cachedSignatureFile.outputStream()
        )
        return cachedSignatureFile
    }

    private fun removeCachedFile(file: File) {
        if (file.exists()) file.delete()
    }

    fun onStartSigning() {
        eSignatureViewModel.isOkEnabled.value = true
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}