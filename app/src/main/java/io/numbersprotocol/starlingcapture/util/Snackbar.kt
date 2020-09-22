package io.numbersprotocol.starlingcapture.util

import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

fun Fragment.snack(e: Throwable, length: Int = Snackbar.LENGTH_LONG, anchorView: View? = null) {
    Timber.e(e)
    Snackbar.make(requireView(), e.message ?: e.toString(), length).setAnchorView(anchorView).show()
}

fun Fragment.snack(message: String, length: Int = Snackbar.LENGTH_LONG, anchorView: View? = null) {
    Timber.i(message)
    Snackbar.make(requireView(), message, length).setAnchorView(anchorView).show()
}

fun Fragment.snack(
    @StringRes message: Int,
    length: Int = Snackbar.LENGTH_LONG,
    anchorView: View? = null
) {
    snack(requireContext().getString(message), length, anchorView)
}