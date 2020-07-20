package io.numbersprotocol.starlingcapture.feature.storage

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.BasePermissionListener
import com.karumi.dexter.listener.single.CompositePermissionListener
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.data.proof.Proof
import io.numbersprotocol.starlingcapture.data.serialization.SaveProofRelatedDataWorker
import io.numbersprotocol.starlingcapture.databinding.FragmentStorageBinding
import io.numbersprotocol.starlingcapture.publisher.PublishersDialog
import io.numbersprotocol.starlingcapture.source.InternalCameraProvider
import io.numbersprotocol.starlingcapture.util.RecyclerViewItemListener
import io.numbersprotocol.starlingcapture.util.observeEvent
import io.numbersprotocol.starlingcapture.util.snack
import io.numbersprotocol.starlingcapture.util.themeColor
import kotlinx.android.synthetic.main.fragment_storage.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class StorageFragment(private val publishersDialog: PublishersDialog) : Fragment() {

    private val storageViewModel: StorageViewModel by stateViewModel()
    private val adapter = StorageAdapter(createRecyclerViewItemListener())
    private var actionMode: ActionMode? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FragmentStorageBinding.inflate(inflater, container, false).also { binding ->
            binding.lifecycleOwner = viewLifecycleOwner
            binding.viewModel = storageViewModel
            binding.recyclerView.adapter = adapter
            enableSharedTransition(binding)
            return binding.root
        }
    }

    private fun enableSharedTransition(binding: FragmentStorageBinding) {
        postponeEnterTransition()
        binding.recyclerView.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOptionsMenuListener()
        bindViewLifecycle()
    }

    private fun setOptionsMenuListener() {
        toolbar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.navigate_to_setting) findNavController().navigate(R.id.toSettingFragment)
            true
        }
    }

    private fun bindViewLifecycle() {
        storageViewModel.proofList.observe(viewLifecycleOwner) { adapter.submitList(it) }
        storageViewModel.newProofEvent.observeEvent(viewLifecycleOwner) { checkLocationPermission(::openNewProofOptionDialog) }
    }

    private fun checkLocationPermission(onGranted: () -> Unit) {
        val onGrantedListener = object : BasePermissionListener() {
            override fun onPermissionGranted(response: PermissionGrantedResponse?) = onGranted()
        }
        val onDeniedListener = DialogOnDeniedPermissionListener.Builder
            .withContext(requireContext())
            .withTitle(R.string.permission_denied)
            .withMessage(R.string.message_location_permission)
            .withButtonText(android.R.string.ok)
            .build()
        val permissionListeners = CompositePermissionListener(onGrantedListener, onDeniedListener)
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(permissionListeners)
            .check()
    }

    private fun openNewProofOptionDialog() {
        val items = listOf(
            getString(InternalCameraProvider.SupportedAction.IMAGE.title),
            getString(InternalCameraProvider.SupportedAction.VIDEO.title),
            getString(R.string.canon_ccapi)
        )
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            lifecycleOwner(viewLifecycleOwner)
            listItems(items = items) { _, _, text ->
                if (text == getString(R.string.canon_ccapi)) findNavController().navigate(R.id.toCcapiFragment)
                else startActivityForInternalCamera(text.toString())
            }
        }
    }

    private fun startActivityForInternalCamera(title: String) {
        val supportedAction =
            InternalCameraProvider.SupportedAction.from(title, requireContext())
        val intent = when (supportedAction) {
            InternalCameraProvider.SupportedAction.IMAGE -> storageViewModel.createImageCaptureIntent(
                this@StorageFragment
            )
            InternalCameraProvider.SupportedAction.VIDEO -> storageViewModel.createVideoCaptureIntent(
                this@StorageFragment
            )
        }
        startActivityForResult(intent, REQUEST_MEDIA_CAPTURE)
    }

    private fun createRecyclerViewItemListener(): RecyclerViewItemListener<Proof> =
        object : RecyclerViewItemListener<Proof>() {

            override fun onItemClick(item: Proof, itemView: View) {
                super.onItemClick(item, itemView)
                if (!adapter.isMultiSelected) navigateToProofFragment(item, itemView)
                else updateActionModeStatus()
            }

            override fun onItemLongClick(item: Proof) {
                super.onItemLongClick(item)
                if (!adapter.isMultiSelected) {
                    val activity = (requireActivity() as AppCompatActivity)
                    actionMode = activity.startSupportActionMode(createActionModeCallback())
                }
                updateActionModeStatus()
            }
        }

    private fun navigateToProofFragment(item: Proof, itemView: View) {
        findNavController().navigate(
            StorageFragmentDirections.toProofFragment(item),
            FragmentNavigatorExtras(itemView to "$item")
        )
    }

    private fun createActionModeCallback() = object : ActionMode.Callback {

        // Set status bar color to primary in action mode programmatically as it cannot be set in styles.xml currently.
        private val statusBarColor = requireActivity().window.statusBarColor

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            requireActivity().apply {
                menuInflater.inflate(R.menu.storage_action_mode, menu)
                window.statusBarColor = themeColor(R.attr.colorPrimary)
            }
            addProofButton.hide()
            adapter.isMultiSelected = true
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) = false

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.select_all -> lifecycleScope.launch {
                    adapter.selectAll()
                    updateActionModeStatus()
                }
                R.id.publish -> publishersDialog.show(
                    requireActivity(),
                    adapter.selectedItems,
                    viewLifecycleOwner
                ) { mode?.finish() }
                R.id.saveAs -> dispatchPickFolderIntent()
                R.id.delete -> showConfirmDialog {
                    storageViewModel.deleteProof(adapter.selectedItems)
                    mode?.finish()
                }
            }
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            adapter.isMultiSelected = false
            requireActivity().window.statusBarColor = statusBarColor
            addProofButton.show()
        }
    }

    private fun updateActionModeStatus() {
        if (adapter.selectedItems.size == 0) actionMode?.finish()
        else actionMode?.title = "${adapter.selectedItems.size}"
    }

    private fun dispatchPickFolderIntent() {
        val pickFolderIntent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        if (pickFolderIntent.resolveActivity(requireContext().packageManager) == null) {
            snack("No provider can handle ${Intent.ACTION_OPEN_DOCUMENT_TREE}.")
            return
        }
        startActivityForResult(pickFolderIntent, REQUEST_PICK_FOLDER)
    }

    private fun showConfirmDialog(onPositive: () -> Unit) {
        MaterialDialog(requireContext()).show {
            lifecycleOwner(viewLifecycleOwner)
            title(R.string.are_you_sure)
            message(R.string.message_are_you_sure)
            positiveButton(android.R.string.ok) { onPositive() }
            negativeButton(android.R.string.cancel)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) {
            if (resultCode != RESULT_CANCELED) snack("Bad result code from request ($requestCode): $resultCode")
            storageViewModel.cleanUpMediaOnFail()
            return
        }
        try {
            when (requestCode) {
                REQUEST_MEDIA_CAPTURE -> storageViewModel.storeMedia()
                REQUEST_PICK_FOLDER -> adapter.selectedItems.forEach {
                    SaveProofRelatedDataWorker.saveProofAs(requireContext(), it, data!!.data!!)
                }
                else -> snack("Unknown request code ($requestCode) from activity result.")
            }
        } catch (e: Exception) {
            snack(e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView.adapter = null
    }

    companion object {
        const val REQUEST_MEDIA_CAPTURE = 1
        const val REQUEST_PICK_FOLDER = 2
    }
}