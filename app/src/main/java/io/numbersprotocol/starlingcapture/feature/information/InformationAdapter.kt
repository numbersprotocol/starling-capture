package io.numbersprotocol.starlingcapture.feature.information

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.numbersprotocol.starlingcapture.data.information.Information
import io.numbersprotocol.starlingcapture.databinding.ItemInformationBinding

class InformationAdapter : ListAdapter<Information, InformationAdapter.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemInformationBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(
        private val binding: ItemInformationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Information) {
            binding.information = item
            binding.executePendingBindings()
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Information>() {
            override fun areItemsTheSame(oldItem: Information, newItem: Information) =
                oldItem.proofHash == newItem.proofHash && oldItem.provider == newItem.provider

            override fun areContentsTheSame(oldItem: Information, newItem: Information) =
                oldItem == newItem
        }
    }
}