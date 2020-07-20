package io.numbersprotocol.starlingcapture.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.numbersprotocol.starlingcapture.BaseActivity

var Fragment.scopedLayoutFullScreen: Boolean
    get() = (requireContext() as BaseActivity).layoutFullScreen
    set(value) {
        if (!value) {
            lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    super.onStart(owner)
                    (requireContext() as BaseActivity).layoutFullScreen = false
                }

                override fun onStop(owner: LifecycleOwner) {
                    super.onStop(owner)
                    (requireContext() as BaseActivity).layoutFullScreen = true
                }
            })
        }
    }