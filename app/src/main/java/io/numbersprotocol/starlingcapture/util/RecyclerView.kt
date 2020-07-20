package io.numbersprotocol.starlingcapture.util

import android.view.View

abstract class RecyclerViewItemListener<T> {
    open fun onItemClick(item: T, itemView: View) {}
    open fun onItemLongClick(item: T) {}
}