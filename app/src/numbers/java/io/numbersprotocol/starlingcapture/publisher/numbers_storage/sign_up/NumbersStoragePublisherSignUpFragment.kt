package io.numbersprotocol.starlingcapture.publisher.numbers_storage.sign_up

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.databinding.FragmentNumbersStoragePublisherSignupBinding
import io.numbersprotocol.starlingcapture.util.observeEvent
import io.numbersprotocol.starlingcapture.util.scopedLayoutFullScreen
import io.numbersprotocol.starlingcapture.util.snack
import kotlinx.android.synthetic.numbers.fragment_numbers_storage_publisher_signup.*
import kotlinx.coroutines.delay
import org.koin.androidx.viewmodel.ext.android.viewModel

class NumbersStoragePublisherSignUpFragment : Fragment() {

    private val numbersStoragePublisherSignUpViewModel: NumbersStoragePublisherSignUpViewModel by viewModel()

    init {
        scopedLayoutFullScreen = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FragmentNumbersStoragePublisherSignupBinding.inflate(inflater, container, false)
            .also { binding ->
                binding.lifecycleOwner = viewLifecycleOwner
                binding.viewModel = numbersStoragePublisherSignUpViewModel
                return binding.root
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        bindViewLifecycle()
    }

    private fun bindViewLifecycle() {
        numbersStoragePublisherSignUpViewModel.errorEvent.observeEvent(viewLifecycleOwner) {
            snack(it)
        }
        numbersStoragePublisherSignUpViewModel.successEvent.observeEvent(viewLifecycleOwner) {
            showSuccessfulMessage()
        }
    }

    private fun showSuccessfulMessage() = lifecycleScope.launchWhenStarted {
        snack(R.string.message_account_created)
        delay(1500)
        findNavController().navigateUp()
    }
}