package io.numbersprotocol.starlingcapture.util

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.util.CardPreviewTransformer.Companion.currentItemHorizontalSpacing

fun ViewPager2.enableCardPreview() {
    offscreenPageLimit = 1
    setPageTransformer(CardPreviewTransformer(context))
    addItemDecoration(CardPreviewDecoration(context))
}

class CardPreviewTransformer(private val context: Context) : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        val nextItemVisibleSizeValue = context.resources.getDimension(nextItemVisibleSize)

        page.translationX =
            -(nextItemVisibleSizeValue * 2 + currentItemHorizontalSpacing) * position
    }

    companion object {
        @DimenRes
        const val nextItemVisibleSize = R.dimen.keyline_3
        const val currentItemHorizontalSpacing = 0
    }
}

class CardPreviewDecoration(private val context: Context) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val nextItemVisibleSizeValue =
            context.resources.getDimension(CardPreviewTransformer.nextItemVisibleSize)

        outRect.right = (nextItemVisibleSizeValue + currentItemHorizontalSpacing).toInt()
        outRect.left = (nextItemVisibleSizeValue + currentItemHorizontalSpacing).toInt()
    }

}