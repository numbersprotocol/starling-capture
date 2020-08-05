package io.numbersprotocol.starlingcapture.feature.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialContainerTransform
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.numbersprotocol.starlingcapture.databinding.FragmentInformationBinding
import kotlinx.android.synthetic.main.fragment_information.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel

class InformationFragment : Fragment() {

    private val informationViewModel: InformationViewModel by viewModel()
    private val args: InformationFragmentArgs by navArgs()
    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform()
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
        FragmentInformationBinding.inflate(inflater, container, false).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = informationViewModel
            return it.root
        }
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindInformationRecyclerView()
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
    }

    @ExperimentalCoroutinesApi
    private fun bindInformationRecyclerView() {
        recyclerView.adapter = adapter
        informationViewModel.informationGroup.observe(viewLifecycleOwner) {
            adapter.addAll(it.map { (type, informationList) ->
                Section(TypeItem(type)).apply {
                    addAll(informationList.map { information -> InformationItem(information) })
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView.adapter = null
    }
}