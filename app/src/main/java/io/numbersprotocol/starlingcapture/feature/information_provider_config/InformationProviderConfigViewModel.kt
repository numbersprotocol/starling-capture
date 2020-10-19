package io.numbersprotocol.starlingcapture.feature.information_provider_config

import android.view.View
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModel
import io.numbersprotocol.starlingcapture.collector.infosnapshot.InfoSnapshotConfig
import io.numbersprotocol.starlingcapture.collector.proofmode.ProofModeConfig

class InformationProviderConfigViewModel : ViewModel() {

    val infoSnapshotCollectDeviceInfo = InfoSnapshotConfig.collectDeviceInfoLiveData
    val infoSnapshotCollectLocaleInfo = InfoSnapshotConfig.collectLocaleInfoLiveData
    val infoSnapshotCollectLocationInfo = InfoSnapshotConfig.collectLocationInfoLiveData
    val infoSnapshotCollectSensorInfo = InfoSnapshotConfig.collectSensorInfoLiveData
    val proofModeSupported = ProofModeConfig.isSupported
    val proofModeEnabled = ProofModeConfig.isEnabledLiveData

    fun onInfoSnapshotCollectDeviceInfoClick(view: View) {
        InfoSnapshotConfig.collectDeviceInfo = (view as SwitchCompat).isChecked
    }

    fun onInfoSnapshotCollectLocaleInfoClick(view: View) {
        InfoSnapshotConfig.collectLocaleInfo = (view as SwitchCompat).isChecked
    }

    fun onInfoSnapshotCollectLocationInfoClick(view: View) {
        InfoSnapshotConfig.collectLocationInfo = (view as SwitchCompat).isChecked
    }

    fun onInfoSnapshotCollectSensorInfoClick(view: View) {
        InfoSnapshotConfig.collectSensorInfo = (view as SwitchCompat).isChecked
    }

    fun onProofModeEnabledClick(view: View) {
        if ((view as SwitchCompat).isChecked) ProofModeConfig.enable()
        else ProofModeConfig.disable()
    }
}