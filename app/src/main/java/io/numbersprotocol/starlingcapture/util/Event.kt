package io.numbersprotocol.starlingcapture.util

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

class Event<out T>(private val content: T) {
    @Suppress("MemberVisibilityCanBePrivate")
    var hashBeenHandled = false

    fun getContentIfNotHandled(): T? = if (hashBeenHandled) {
        null
    } else {
        hashBeenHandled = true
        content
    }
}

@MainThread
inline fun <T> LiveData<out Event<T>>.observeEvent(
    owner: LifecycleOwner,
    crossinline onEventUnhandledContent: (T) -> Unit
) = observe(owner) {
    it.getContentIfNotHandled()?.let(onEventUnhandledContent)
}