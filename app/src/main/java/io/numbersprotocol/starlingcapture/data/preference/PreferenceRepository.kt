package io.numbersprotocol.starlingcapture.data.preference

import android.content.Context
import androidx.preference.PreferenceManager
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.util.stringLiveData
import io.numbersprotocol.starlingcapture.util.stringPref

class PreferenceRepository(context: Context) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    val darkModeLiveData = sharedPreferences.stringLiveData(
        context.getString(R.string.key_dark_mode),
        "-1" // MODE_NIGHT_FOLLOW_SYSTEM
    )

    var defaultPublicKey by sharedPreferences.stringPref(context.getString(R.string.key_default_public_key))

    var defaultPrivateKey by sharedPreferences.stringPref(context.getString(R.string.key_default_private_key))
}
