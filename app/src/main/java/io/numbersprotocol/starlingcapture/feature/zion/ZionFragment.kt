package io.numbersprotocol.starlingcapture.feature.zion

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.callbacks.onCancel
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.collector.signature.zion.SessionSignature
import io.numbersprotocol.starlingcapture.collector.signature.zion.ZionApi
import io.numbersprotocol.starlingcapture.databinding.FragmentZionBinding
import io.numbersprotocol.starlingcapture.util.isPositiveInteger
import io.numbersprotocol.starlingcapture.util.observeEvent
import io.numbersprotocol.starlingcapture.util.snack
import kotlinx.android.synthetic.main.fragment_zion.toolbar
import kotlinx.android.synthetic.main.menu_item_switch.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ZionFragment(
    private val zionApi: ZionApi,
    private val sessionSignature: SessionSignature
) : Fragment() {

    private val zionViewModel: ZionViewModel by viewModel()
    private lateinit var sessionSignatureSwitch: SwitchCompat

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FragmentZionBinding.inflate(inflater, container, false).also { binding ->
            binding.lifecycleOwner = viewLifecycleOwner
            binding.viewModel = zionViewModel
            sessionSignatureSwitch = binding.sessionSignatureSwitch
            return binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        initializeToolbarSwitch()
        bindViewLifecycle()
    }

    private fun initializeToolbarSwitch() {
        switchItem.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launchWhenResumed {
                try {
                    if (isChecked) {
                        zionApi.enable()
                        if (!zionApi.hasCreatedSeed()) showSeedGenerationDialog()
                    } else zionApi.disable()
                } catch (e: Exception) {
                    snack(e)
                    disableZionSafely()
                }
            }
        }
    }

    private fun showSeedGenerationDialog() {
        val items = listOf(
            getString(R.string.create_new_seed),
            getString(R.string.restore_old_seed)
        )
        MaterialDialog(requireContext()).show {
            lifecycleOwner(viewLifecycleOwner)
            listItems(items = items) { _, _, text ->
                lifecycleScope.launch {
                    try {
                        when (text) {
                            getString(R.string.create_new_seed) -> zionApi.createSeed()
                            getString(R.string.restore_old_seed) -> zionApi.restoreSeed()
                        }
                    } catch (e: Exception) {
                        snack(e)
                        disableZionSafely()
                    }
                }
            }
            negativeButton(android.R.string.cancel) {
                disableZionSafely()
                dismiss()
            }
            onCancel { disableZionSafely() }
        }
    }

    private fun disableZionSafely() = lifecycleScope.launch {
        try {
            zionApi.disable()
        } catch (e: Exception) {
            snack(e)
        }
    }

    private fun bindViewLifecycle() {
        zionApi.isEnabledLiveData.observe(viewLifecycleOwner) { switchItem.isChecked = it }
        zionViewModel.errorEvent.observeEvent(viewLifecycleOwner) { snack(it) }
        zionViewModel.enableSessionSignatureEvent.observeEvent(viewLifecycleOwner) {
            showSessionSignatureCreationDialog()
        }
    }

    private fun showSessionSignatureCreationDialog() {
        var durationInMinutes = 480L
        MaterialDialog(requireContext()).show {
            lifecycleOwner(viewLifecycleOwner)
            title(R.string.session_duration)
            message(R.string.message_session_duration)
            input(
                inputType = InputType.TYPE_CLASS_NUMBER,
                prefill = "480",
                waitForPositiveButton = false
            ) { dialog, text ->
                val isValid = text.isPositiveInteger()
                dialog.setActionButtonEnabled(WhichButton.POSITIVE, isValid)
                if (isValid) durationInMinutes = text.toString().toLong()
            }
            positiveButton(android.R.string.ok) { sessionSignature.enable(durationInMinutes) }
            negativeButton(android.R.string.cancel) {
                sessionSignatureSwitch.isChecked = false
                dismiss()
            }
            onCancel { sessionSignatureSwitch.isChecked = false }
        }
    }
}