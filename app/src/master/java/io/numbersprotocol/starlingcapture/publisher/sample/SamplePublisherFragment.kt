package io.numbersprotocol.starlingcapture.publisher.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import io.numbersprotocol.starlingcapture.databinding.FragmentSamplePublisherBinding
import kotlinx.android.synthetic.main.menu_item_switch.*
import kotlinx.android.synthetic.master.fragment_sample_publisher.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SamplePublisherFragment : Fragment() {

    private val samplePublisherViewModel: SamplePublisherViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FragmentSamplePublisherBinding.inflate(inflater, container, false).also { binding ->
            binding.lifecycleOwner = viewLifecycleOwner
            binding.viewModel = samplePublisherViewModel
            return binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        initializeToolbarSwitch()
    }

    private fun initializeToolbarSwitch() {
        samplePublisherConfig.isEnabledLiveData.observe(viewLifecycleOwner) {
            switchItem.isChecked = it
        }
        switchItem.setOnCheckedChangeListener { _, isChecked ->
            samplePublisherConfig.isEnabled = isChecked
        }
    }
}