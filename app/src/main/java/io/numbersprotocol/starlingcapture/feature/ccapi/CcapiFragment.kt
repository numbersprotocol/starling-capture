package io.numbersprotocol.starlingcapture.feature.ccapi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.input.getInputLayout
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import io.numbersprotocol.starlingcapture.databinding.FragmentCcapiBinding
import io.numbersprotocol.starlingcapture.source.canon.CanonCameraControlProvider.Companion.DEFAULT_ADDRESS
import io.numbersprotocol.starlingcapture.util.isIpAddress
import io.numbersprotocol.starlingcapture.util.observeEvent
import io.numbersprotocol.starlingcapture.util.snack
import kotlinx.android.synthetic.main.fragment_ccapi.*
import kotlinx.android.synthetic.main.menu_item_switch.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel

class CcapiFragment : Fragment() {

    private val ccapiViewModel: CcapiViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentCcapiBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = ccapiViewModel
        }.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        initializeToolbarSwitch()
        bindViewLifecycle()
    }

    @ExperimentalCoroutinesApi
    private fun initializeToolbarSwitch() {
        switchItem.setOnCheckedChangeListener { _, isChecked ->
            try {
                if (isChecked) ccapiViewModel.enable()
                else ccapiViewModel.disable()
            } catch (e: Exception) {
                snack(e)
            }
        }
    }

    @ExperimentalCoroutinesApi
    private fun bindViewLifecycle() {
        ccapiViewModel.isEnabled.observe(viewLifecycleOwner) { switchItem.isChecked = it }
        ccapiViewModel.editIpAddressAndPortEvent.observeEvent(viewLifecycleOwner) {
            showEditIpAddressDialog()
        }
        ccapiViewModel.editSlateEvent.observeEvent(viewLifecycleOwner) {
            showEditSlateDialog()
        }
    }

    private fun showEditIpAddressDialog() {
        var address: String? = null
        MaterialDialog(requireContext()).show {
            lifecycleOwner(viewLifecycleOwner)
            input(
                waitForPositiveButton = false,
                hint = DEFAULT_ADDRESS,
                prefill = "${ccapiViewModel.address.value}"
            ) { dialog, text ->
                val isValid = text.isIpAddress()
                dialog.setActionButtonEnabled(WhichButton.POSITIVE, isValid)
                dialog.getInputLayout().apply {
                    prefixText = "http://"
                    suffixText = "/ccapi"
                }
                if (isValid) address = text.toString()
            }
            positiveButton(android.R.string.ok) {
                address?.also { ccapiViewModel.setAddress(it) }
            }
            negativeButton(android.R.string.cancel) { dismiss() }
        }
    }

    private fun showEditSlateDialog() {
        var photographer = ""
        MaterialDialog(requireContext()).show {
            lifecycleOwner(viewLifecycleOwner)
            input { _, text -> photographer = text.toString() }
            positiveButton(android.R.string.ok) {
                ccapiViewModel.slatePhotographer.value = photographer
            }
            negativeButton(android.R.string.cancel) { dismiss() }
        }
    }
}