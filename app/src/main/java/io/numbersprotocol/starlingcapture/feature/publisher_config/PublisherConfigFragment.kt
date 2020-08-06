package io.numbersprotocol.starlingcapture.feature.publisher_config

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.numbersprotocol.starlingcapture.databinding.FragmentPublisherConfigBinding
import io.numbersprotocol.starlingcapture.publisher.PublisherConfig
import io.numbersprotocol.starlingcapture.publisher.publisherConfigs
import io.numbersprotocol.starlingcapture.util.RecyclerViewItemListener
import kotlinx.android.synthetic.main.fragment_publisher_config.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PublisherConfigFragment : Fragment() {

    private val publisherConfigViewModel: PublisherConfigViewModel by viewModel()
    private val recyclerViewItemListener = object : RecyclerViewItemListener<PublisherConfig>() {
        override fun onItemClick(item: PublisherConfig, itemView: View) {
            super.onItemClick(item, itemView)
            findNavController().navigate(item.toFragmentAction)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FragmentPublisherConfigBinding.inflate(inflater, container, false).also { binding ->
            binding.lifecycleOwner = viewLifecycleOwner
            binding.viewModel = publisherConfigViewModel
            binding.recyclerView.adapter = PublisherConfigAdapter(recyclerViewItemListener).apply {
                submitList(publisherConfigs)
            }
            return binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
    }
}