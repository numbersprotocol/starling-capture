package io.numbersprotocol.starlingcapture.feature.storage

import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.api.load
import com.google.android.material.card.MaterialCardView
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.data.proof.Proof
import io.numbersprotocol.starlingcapture.data.proof.ProofRepository
import io.numbersprotocol.starlingcapture.di.CoilImageLoader
import io.numbersprotocol.starlingcapture.util.MimeType
import io.numbersprotocol.starlingcapture.util.RecyclerViewItemListener
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named

class StorageAdapter(
    private val listener: RecyclerViewItemListener<Proof>
) : PagedListAdapter<Proof, StorageAdapter.ViewHolder>(diffCallback), KoinComponent {

    private val proofRepository: ProofRepository by inject()
    private val imageLoader: ImageLoader by inject(named(CoilImageLoader.SmallThumb))

    var isMultiSelected = false
        set(value) {
            field = value
            if (!field) {
                selectedItems.clear()
                notifyDataSetChanged()
            }
        }
    val selectedItems = mutableSetOf<Proof>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_proof, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    suspend fun selectAll() {
        selectedItems.addAll(proofRepository.getAll())
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val thumbCardView: MaterialCardView = itemView.findViewById(R.id.thumbCardView)
        private val thumbImageView: ImageView = itemView.findViewById(R.id.thumbImageView)
        private val videoIndicator: ImageView = itemView.findViewById(R.id.videoIndicator)

        fun bind(item: Proof?) {
            thumbCardView.isChecked = selectedItems.contains(item)
            if (item == null) {
                bindLoading()
            } else {
                bindListener(item)
                thumbCardView.transitionName = "$item"
                bindThumbImage(item)
                if (item.mimeType == MimeType.MP4) videoIndicator.visibility = VISIBLE
            }
        }

        private fun bindLoading() {
            thumbImageView.setImageResource(android.R.color.transparent)
        }

        private fun bindListener(item: Proof) {
            thumbCardView.setOnClickListener {
                if (isMultiSelected) selectItem(item)
                listener.onItemClick(item, thumbCardView)
            }
            thumbCardView.setOnLongClickListener {
                selectItem(item)
                listener.onItemLongClick(item)
                true
            }
        }

        private fun bindThumbImage(proof: Proof) {
            val rawMediaFile = proofRepository.getRawFile(proof)
            thumbImageView.load(rawMediaFile, imageLoader = imageLoader)
        }

        private fun selectItem(item: Proof) {
            if (selectedItems.contains(item)) {
                selectedItems.remove(item)
                thumbCardView.isChecked = false
            } else {
                selectedItems.add(item)
                thumbCardView.isChecked = true
            }
        }
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Proof>() {

            override fun areItemsTheSame(oldItem: Proof, newItem: Proof) =
                oldItem.hash == newItem.hash

            override fun areContentsTheSame(oldItem: Proof, newItem: Proof) = oldItem == newItem

        }
    }
}