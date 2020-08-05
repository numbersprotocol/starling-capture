package io.numbersprotocol.starlingcapture.publisher

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.data.proof.Proof

class PublisherManager(private val context: Context) {

    fun publishOrShowSelection(
        activity: Activity,
        proofs: Iterable<Proof>,
        lifecycleOwner: LifecycleOwner,
        callback: () -> Unit = {}
    ) {
        if (publisherConfigs.size == 1) {
            publisherConfigs.first().onPublish(context, proofs)
            callback()
        } else showSelection(activity, proofs, lifecycleOwner, callback)
    }

    private fun showSelection(
        activity: Activity,
        proofs: Iterable<Proof>,
        lifecycleOwner: LifecycleOwner,
        callback: () -> Unit = {}
    ) {
        val items = publisherConfigs
            .filter { it.isEnabled }
            .map { context.getString(it.name) }
        MaterialDialog(activity, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.select_a_publisher)
            message(R.string.message_enable_publishers_in_settings)
            lifecycleOwner(lifecycleOwner)
            listItems(items = items) { _, _, text ->
                publisherConfigs.find { context.getString(it.name) == text }!!
                    .onPublish(context, proofs)
                callback()
            }
        }
    }
}