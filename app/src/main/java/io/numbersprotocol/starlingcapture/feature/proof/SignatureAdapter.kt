package io.numbersprotocol.starlingcapture.feature.proof

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.numbersprotocol.starlingcapture.data.signature.Signature
import io.numbersprotocol.starlingcapture.databinding.ItemSignatureBinding

class SignatureAdapter : ListAdapter<Signature, SignatureAdapter.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSignatureBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(
        private val binding: ItemSignatureBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Signature) {
            binding.signature = item
            binding.executePendingBindings()
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Signature>() {
            override fun areItemsTheSame(oldItem: Signature, newItem: Signature) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Signature, newItem: Signature) =
                oldItem == newItem
        }
    }
}