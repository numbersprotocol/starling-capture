package io.numbersprotocol.starlingcapture.feature.publisher_config

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.numbersprotocol.starlingcapture.databinding.ItemPublisherBinding
import io.numbersprotocol.starlingcapture.publisher.PublisherConfig
import io.numbersprotocol.starlingcapture.util.RecyclerViewItemListener

class PublisherConfigAdapter(
    private val listener: RecyclerViewItemListener<PublisherConfig>
) : ListAdapter<PublisherConfig, PublisherConfigAdapter.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPublisherBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(
        private val binding: ItemPublisherBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PublisherConfig) {
            binding.publisher = item
            binding.root.setOnClickListener { listener.onItemClick(item, binding.root) }
            binding.executePendingBindings()
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<PublisherConfig>() {
            override fun areItemsTheSame(oldItem: PublisherConfig, newItem: PublisherConfig) =
                oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: PublisherConfig, newItem: PublisherConfig) =
                oldItem.name == newItem.name
        }
    }
}