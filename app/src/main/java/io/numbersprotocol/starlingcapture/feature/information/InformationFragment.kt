package io.numbersprotocol.starlingcapture.feature.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.numbersprotocol.starlingcapture.databinding.FragmentInformationBinding
import kotlinx.android.synthetic.main.fragment_information.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel

class InformationFragment : Fragment() {

    private val informationViewModel: InformationViewModel by viewModel()
    private val args: InformationFragmentArgs by navArgs()
    private lateinit var binding: FragmentInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModel()
    }

    private fun initializeViewModel() {
        informationViewModel.apply {
            proof.value = args.proof
            provider.value = args.provider
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInformationBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = informationViewModel
        }
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindInformationRecyclerView()
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
    }

    @ExperimentalCoroutinesApi
    private fun bindInformationRecyclerView() {
        val informationAdapter = InformationAdapter()
        binding.recyclerView.adapter = informationAdapter
        informationViewModel.informationList.observe(viewLifecycleOwner) {
            informationAdapter.submitList(it)
        }
    }
}