package io.numbersprotocol.starlingcapture.collector.information.infosnapshot

import android.content.Context
import io.numbersprotocol.starlingcapture.util.booleanLiveData
import io.numbersprotocol.starlingcapture.util.booleanPref
import org.koin.core.KoinComponent
import org.koin.core.inject

object InfoSnapshotConfig : KoinComponent {

    private val context: Context by inject()
    private const val INFOSNAPSHOT = "INFOSNAPSHOT"
    private val sharedPreferences = context.getSharedPreferences(INFOSNAPSHOT, Context.MODE_PRIVATE)

    private const val KEY_COLLECT_DEVICE_INFO = "KEY_COLLECT_DEVICE_INFO"
    var collectDeviceInfo by sharedPreferences.booleanPref(KEY_COLLECT_DEVICE_INFO, true)
    val collectDeviceInfoLiveData =
        sharedPreferences.booleanLiveData(KEY_COLLECT_DEVICE_INFO, true)
    private const val KEY_COLLECT_LOCATION_INFO = "KEY_COLLECT_LOCATION_INFO"
    var collectLocationInfo by sharedPreferences.booleanPref(KEY_COLLECT_LOCATION_INFO, true)
    val collectLocationInfoLiveData =
        sharedPreferences.booleanLiveData(KEY_COLLECT_LOCATION_INFO, true)
}