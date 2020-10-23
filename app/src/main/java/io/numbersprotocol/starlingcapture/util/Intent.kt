package io.numbersprotocol.starlingcapture.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import timber.log.Timber

fun Context.openLinkInBrowser(link: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
    if (intent.resolveActivity(packageManager) != null) startActivity(intent)
    else Timber.e("No activity can view the link: $link")
}