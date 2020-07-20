package io.numbersprotocol.starlingcapture.feature.proof

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.numbersprotocol.starlingcapture.data.information.InformationRepository
import io.numbersprotocol.starlingcapture.data.proof.Proof
import io.numbersprotocol.starlingcapture.databinding.ItemInformationProviderBinding

class InformationProviderAdapter(
    private val informationRepository: InformationRepository,
    private val viewLifecycleOwner: LifecycleOwner,
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

        private val adapter = InformationAdapter()

        fun bind(item: String) {
            binding.provider = item
            binding.informationRecyclerView.adapter = adapter
            informationRepository.getByProofAndProviderWithLiveData(proof, item)
                .observe(viewLifecycleOwner) { adapter.submitList(it) }
            binding.executePendingBindings()
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
            override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem
        }
    }
}