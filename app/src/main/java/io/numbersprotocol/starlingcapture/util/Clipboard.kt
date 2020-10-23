package io.numbersprotocol.starlingcapture.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat.getSystemService

fun Context.copyToClipboard(text: CharSequence) =
    getSystemService(this, ClipboardManager::class.java)?.also {
        val clipData = ClipData.newPlainText(text, text)
        it.setPrimaryClip(clipData)
    }