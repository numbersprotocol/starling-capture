package io.numbersprotocol.starlingcapture.feature.e_signature

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.numbersprotocol.starlingcapture.databinding.FragmentESignatureBinding
import kotlinx.android.synthetic.main.fragment_e_signature.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ESignatureFragment : Fragment() {

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
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeCameraView()
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
    }

    private fun initializeCameraView() {
        cameraView.setLifecycleOwner(viewLifecycleOwner)
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}