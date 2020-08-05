package io.numbersprotocol.starlingcapture.feature.proof

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.numbersprotocol.starlingcapture.data.information.Information
import io.numbersprotocol.starlingcapture.data.information.InformationRepository
import io.numbersprotocol.starlingcapture.data.proof.Proof
import io.numbersprotocol.starlingcapture.databinding.ItemInformationProviderBinding
import io.numbersprotocol.starlingcapture.databinding.ItemSimpleInformationBinding
import io.numbersprotocol.starlingcapture.util.RecyclerViewItemListener

class InformationProviderAdapter(
    private val viewLifecycleOwner: LifecycleOwner,
    private val listener: RecyclerViewItemListener<String>,
    private val informationRepository: InformationRepository,
    private val proof: Proof
) : ListAdapter<String, InformationProviderAdapter.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemInformationProviderBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(
        private val binding: ItemInformationProviderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val adapter = SimpleInformationAdapter()

        fun bind(item: String) {
            binding.root.transitionName = item
            binding.provider = item
            binding.informationRecyclerView.adapter = adapter
            informationRepository
                .getByProofAndProviderWithFlow(proof, item, Information.Importance.HIGH)
                .asLiveData(timeoutInMs = 0)
                .observe(viewLifecycleOwner) {
                    adapter.submitList(it)
                }
            binding.viewAllButton.setOnClickListener { listener.onItemClick(item, itemView) }
            binding.executePendingBindings()
        }
    }

    class SimpleInformationAdapter :
        ListAdapter<Information, SimpleInformationAdapter.ViewHolder>(diffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemSimpleInformationBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.bind(getItem(position))

        inner class ViewHolder(
            private val binding: ItemSimpleInformationBinding
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

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
            override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem
        }
    }
}