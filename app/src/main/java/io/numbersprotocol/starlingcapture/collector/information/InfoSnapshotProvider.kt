package io.numbersprotocol.starlingcapture.collector.information

import android.content.Context
import androidx.work.WorkerParameters
import io.numbers.infosnapshot.InfoSnapshotBuilder
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.data.information.Information
import org.koin.core.KoinComponent
import java.text.DateFormat

class InfoSnapshotProvider(
    context: Context,
    params: WorkerParameters
) : InformationProvider(context, params), KoinComponent {

    override val name = "InfoSnapshot"

    override suspend fun provide(): Collection<Information> {
        val snapshot = InfoSnapshotBuilder(context).apply {
            duration = 10000
            enableLocaleInfo = false
            enableSensorInfo = false
        }.snap()

        val lastKnownLatitude = snapshot.locationInfo.value?.lastKnown?.value?.latitude.toString()
        val lastKnownLongitude = snapshot.locationInfo.value?.lastKnown?.value?.longitude.toString()
        val lastKnownAddress = snapshot.locationInfo.value?.lastKnown?.value?.address.toString()
        val currentLatitude = snapshot.locationInfo.value?.current?.value?.latitude.toString()
        val currentLongitude = snapshot.locationInfo.value?.current?.value?.longitude.toString()
        val currentAddress = snapshot.locationInfo.value?.current?.value?.address.toString()
        val deviceInfo = snapshot.deviceInfo.value

        return setOf(
            Information(
                hash, name, context.getString(R.string.timestamp),
                DateFormat.getInstance().format(System.currentTimeMillis()),
                Information.Importance.LOW
            ),
            Information(
                hash, name, context.getString(R.string.hash_of_android_id),
                snapshot.settingsInfo.value?.androidIdHash?.value.toString(),
                Information.Importance.HIGH
            ),
            Information(
                hash, name, context.getString(R.string.last_knwon_location),
                "$lastKnownLatitude, $lastKnownLongitude",
                Information.Importance.HIGH
            ),
            Information(
                hash, name, context.getString(R.string.last_knwon_address), lastKnownAddress,
                Information.Importance.HIGH
            ),
            Information(
                hash, name, context.getString(R.string.current_location),
                "$currentLatitude, $currentLongitude",
                Information.Importance.HIGH
            ),
            Information(
                hash, name, context.getString(R.string.current_address), currentAddress,
                Information.Importance.HIGH
            ),
            Information(
                hash, name, context.getString(R.string.board), deviceInfo?.board.toString(),
                Information.Importance.LOW
            ),
            Information(
                hash, name, context.getString(R.string.brand), deviceInfo?.brand.toString(),
                Information.Importance.LOW
            ),
            Information(
                hash, name, context.getString(R.string.device_name),
                deviceInfo?.device.toString(),
                Information.Importance.LOW
            ),
            Information(
                hash, name, context.getString(R.string.device_build_id),
                deviceInfo?.display.toString(),
                Information.Importance.LOW
            ),
            Information(
                hash, name, context.getString(R.string.device_build_fingerprint),
                deviceInfo?.fingerprint.toString(),
                Information.Importance.LOW
            ),
            Information(
                hash, name, context.getString(R.string.hardware),
                deviceInfo?.hardware.toString(),
                Information.Importance.LOW
            ),
            Information(
                hash, name, context.getString(R.string.manufacturer),
                deviceInfo?.manufacturer.toString(),
                Information.Importance.LOW
            ),
            Information(
                hash, name, context.getString(R.string.end_product_name),
                deviceInfo?.model.toString(),
                Information.Importance.LOW
            ),
            Information(
                hash, name, context.getString(R.string.overall_product_name),
                deviceInfo?.product.toString(),
                Information.Importance.LOW
            ),
            Information(
                hash, name, context.getString(R.string.device_build_tags),
                deviceInfo?.tags.toString(),
                Information.Importance.LOW
            ),
            Information(
                hash, name, context.getString(R.string.device_build_time),
                deviceInfo?.buildTime?.let { DateFormat.getInstance().format(it) }.toString(),
                Information.Importance.LOW
            ),
            Information(
                hash, name, context.getString(R.string.device_build_type),
                deviceInfo?.type.toString(),
                Information.Importance.LOW
            )
        )
    }
}