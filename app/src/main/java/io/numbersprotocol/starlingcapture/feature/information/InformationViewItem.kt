package io.numbersprotocol.starlingcapture.feature.information

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.data.information.Information
import io.numbersprotocol.starlingcapture.databinding.ItemInformationBinding
import io.numbersprotocol.starlingcapture.databinding.ItemInformationTypeBinding

class TypeItem(private val type: Information.Type) : BindableItem<ItemInformationTypeBinding>() {

    override fun getLayout() = R.layout.item_information_type
    override fun initializeViewBinding(view: View) = ItemInformationTypeBinding.bind(view)
    override fun bind(viewBinding: ItemInformationTypeBinding, position: Int) {
        viewBinding.type = type
    }
}

class InformationItem(
    private val information: Information
) : BindableItem<ItemInformationBinding>() {

    override fun getLayout() = R.layout.item_information
    override fun initializeViewBinding(view: View) = ItemInformationBinding.bind(view)
    override fun bind(viewBinding: ItemInformationBinding, position: Int) {
        viewBinding.information = information
    }
}