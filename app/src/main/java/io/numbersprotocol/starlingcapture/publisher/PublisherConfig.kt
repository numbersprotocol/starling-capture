package io.numbersprotocol.starlingcapture.publisher

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import io.numbersprotocol.starlingcapture.data.proof.Proof
import io.numbersprotocol.starlingcapture.util.booleanLiveData
import io.numbersprotocol.starlingcapture.util.booleanPref
import org.koin.core.KoinComponent
import org.koin.core.inject

open class PublisherConfig(
    @StringRes val name: Int,
    @DrawableRes val icon: Int,
    @IdRes val toFragmentAction: Int,
    val onPublish: (Context, Iterable<Proof>) -> Unit
) : KoinComponent {

    protected val context: Context by inject()

    @Suppress("MemberVisibilityCanBePrivate")
    protected val sharedPreference: SharedPreferences =
        context.getSharedPreferences(PUBLISHER_CONFIG, MODE_PRIVATE)
    val isEnabledLiveData =
        sharedPreference.booleanLiveData("${context.getString(name)}_$KEY_IS_ENABLED")
    var isEnabled by sharedPreference.booleanPref("${context.getString(name)}_$KEY_IS_ENABLED")

    companion object {
        private const val PUBLISHER_CONFIG = "PUBLISHER_CONFIG"
        private const val KEY_IS_ENABLED = "KEY_IS_ENABLED"
    }
}