package io.numbersprotocol.starlingcapture.feature.information_provider_config

import android.view.View
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModel
import io.numbersprotocol.starlingcapture.collector.infosnapshot.InfoSnapshotConfig

class InformationProviderConfigViewModel : ViewModel() {

    val infoSnapshotCollectDeviceInfo = InfoSnapshotConfig.collectDeviceInfoLiveData
    val infoSnapshotCollectLocationInfo = InfoSnapshotConfig.collectLocationInfoLiveData
    val infoSnapshotCollectSensorInfo = InfoSnapshotConfig.collectSensorInfoLiveData

    fun onInfoSnapshotCollectDeviceInfoClick(view: View) {
        InfoSnapshotConfig.collectDeviceInfo = (view as SwitchCompat).isChecked
    }

    fun onInfoSnapshotCollectLocationInfoClick(view: View) {
        InfoSnapshotConfig.collectLocationInfo = (view as SwitchCompat).isChecked
    }

    fun onInfoSnapshotCollectSensorInfoClick(view: View) {
        InfoSnapshotConfig.collectSensorInfo = (view as SwitchCompat).isChecked
    }
}