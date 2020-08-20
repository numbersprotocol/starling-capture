package io.numbersprotocol.starlingcapture.feature.setting

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.navigation.fragment.findNavController
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.Preference.SummaryProvider
import androidx.preference.PreferenceFragmentCompat
import io.numbersprotocol.starlingcapture.BuildConfig.VERSION_NAME
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.collector.signature.zion.ZionApi
import io.numbersprotocol.starlingcapture.util.navigateSafely

class PreferenceFragment : PreferenceFragmentCompat() {

    private val ellipsisSummaryProvider = SummaryProvider { preference: EditTextPreference ->
        val ellipsisThreshold = 60
        val text = preference.text
        if (text.length > ellipsisThreshold) "${text.take(ellipsisThreshold)}..."
        else text
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        initInformationPreferences()
        initSignaturePreferences()
        initPublisherPreferences()
        initAboutPreferences()
    }

    private fun initInformationPreferences() {
        findPreference<Preference>(getString(R.string.key_information_provider))?.setOnPreferenceClickListener {
            findNavController().navigateSafely(R.id.toInformationProviderConfigFragment)
            true
        }
    }

    private fun initSignaturePreferences() {
        initDefaultSignature()
        initZionSignature()
    }

    private fun initDefaultSignature() {
        findPreference<EditTextPreference>(R.string.key_default_public_key)?.apply {
            summaryProvider = ellipsisSummaryProvider
        }
        findPreference<EditTextPreference>(R.string.key_default_private_key)?.apply {
            summaryProvider = ellipsisSummaryProvider
        }
    }

    private fun initZionSignature() =
        findPreference<Preference>(R.string.key_sign_with_zion)?.apply {
            isEnabled = ZionApi.isSupported
            summaryProvider = SummaryProvider<Preference> {
                if (!ZionApi.isSupported) {
                    "${getString(R.string.unsupported_device)}: ${ZionApi.deviceName}"
                } else {
                    ""
                }
            }
            setOnPreferenceClickListener {
                findNavController().navigateSafely(R.id.toZionFragment)
                true
            }
        }

    private fun initPublisherPreferences() =
        findPreference<Preference>(R.string.key_publisher)?.setOnPreferenceClickListener {
            findNavController().navigateSafely(R.id.toPublisherGraph)
            true
        }

    private fun initAboutPreferences() = findPreference<Preference>(R.string.key_version)?.apply {
        summary = VERSION_NAME
    }

    private fun <T : Preference?> findPreference(@StringRes key: Int): T? {
        return findPreference<T>(getString(key))
    }
}