package org.starlinglab.starlingcapture.publisher.starling_integrity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
//import io.numbersprotocol.starlingcapture.databinding.FragmentNumbersStoragePublisherLoginBinding
import io.numbersprotocol.starlingcapture.databinding.FragmentStarlingIntegrityPublisherLoginBinding
import io.numbersprotocol.starlingcapture.util.observeEvent
import io.numbersprotocol.starlingcapture.util.scopedLayoutFullScreen
import io.numbersprotocol.starlingcapture.util.snack
//import kotlinx.android.synthetic.internal.fragment_numbers_storage_publisher_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class StarlingIntegrityPublisherFragment : Fragment() {

    private val starlingIntegrityPublisherViewModel: StarlingIntegrityPublisherViewModel by viewModel()

    init {
        scopedLayoutFullScreen = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FragmentStarlingIntegrityPublisherLoginBinding.inflate(inflater, container, false)
            .also { binding ->
                binding.lifecycleOwner = viewLifecycleOwner
                binding.viewModel = starlingIntegrityPublisherViewModel
                return binding.root
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
//        bindViewLifecycle()
    }

    private fun bindViewLifecycle() {
//        numbersStoragePublisherViewModel.signUpEvent.observeEvent(viewLifecycleOwner) {
//            findNavController().navigateSafely(R.id.toNumbersStoragePublisherSignUpFragment)
//        }
//        starlingIntegrityPublisherViewModel.errorEvent.observeEvent(viewLifecycleOwner) { snack(it) }
    }
}