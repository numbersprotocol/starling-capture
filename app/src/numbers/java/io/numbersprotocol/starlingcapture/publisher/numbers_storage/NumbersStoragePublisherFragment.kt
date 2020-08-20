package io.numbersprotocol.starlingcapture.publisher.numbers_storage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.databinding.FragmentNumbersStoragePublisherLoginBinding
import io.numbersprotocol.starlingcapture.util.navigateSafely
import io.numbersprotocol.starlingcapture.util.observeEvent
import io.numbersprotocol.starlingcapture.util.scopedLayoutFullScreen
import io.numbersprotocol.starlingcapture.util.snack
import kotlinx.android.synthetic.numbers.fragment_numbers_storage_publisher_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class NumbersStoragePublisherFragment : Fragment() {

    private val numbersStoragePublisherViewModel: NumbersStoragePublisherViewModel by viewModel()

    init {
        scopedLayoutFullScreen = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FragmentNumbersStoragePublisherLoginBinding.inflate(inflater, container, false)
            .also { binding ->
                binding.lifecycleOwner = viewLifecycleOwner
                binding.viewModel = numbersStoragePublisherViewModel
                return binding.root
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        bindViewLifecycle()
    }

    private fun bindViewLifecycle() {
        numbersStoragePublisherViewModel.signUpEvent.observeEvent(viewLifecycleOwner) {
            findNavController().navigateSafely(R.id.toNumbersStoragePublisherSignUpFragment)
        }
        numbersStoragePublisherViewModel.errorEvent.observeEvent(viewLifecycleOwner) { snack(it) }
    }
}