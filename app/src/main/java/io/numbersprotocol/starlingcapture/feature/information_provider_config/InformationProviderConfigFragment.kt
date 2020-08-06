package io.numbersprotocol.starlingcapture.feature.information_provider_config

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.numbersprotocol.starlingcapture.databinding.FragmentInformationProviderConfigBinding
import kotlinx.android.synthetic.main.fragment_information_provider_config.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class InformationProviderConfigFragment : Fragment() {

    private val informationProviderConfigViewModel: InformationProviderConfigViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FragmentInformationProviderConfigBinding.inflate(inflater, container, false)
            .also { binding ->
                binding.viewModel = informationProviderConfigViewModel
                binding.lifecycleOwner = viewLifecycleOwner
                return binding.root
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
    }
}