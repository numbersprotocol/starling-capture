package io.numbersprotocol.starlingcapture.util

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

fun Fragment.snack(e: Throwable, length: Int = Snackbar.LENGTH_LONG) {
    Timber.e(e)
    Snackbar.make(requireView(), e.message ?: e.toString(), length).show()
}

fun Fragment.snack(message: String, length: Int = Snackbar.LENGTH_LONG) {
    Timber.i(message)
    Snackbar.make(requireView(), message, length).show()
}

fun Fragment.snack(@StringRes message: Int, length: Int = Snackbar.LENGTH_LONG) {
    snack(requireContext().getString(message), length)
}