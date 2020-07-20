package io.numbersprotocol.starlingcapture.collector.information

import android.content.Context
import androidx.work.WorkerParameters
import io.numbers.infosnapshot.InfoSnapshotBuilder
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.data.information.Information
import io.numbersprotocol.starlingcapture.data.information.InformationRepository
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.text.DateFormat

class InfoSnapshotProvider(
    context: Context,
    params: WorkerParameters
) : InformationProvider(context, params), KoinComponent {

    override val provider = "InfoSnapshot"

    private val informationRepository: InformationRepository by inject()

    override suspend fun provideInformation(): Result {
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

        informationRepository.add(
            Information(
                hash, provider, context.getString(R.string.timestamp),
                DateFormat.getInstance().format(System.currentTimeMillis())
            ),
            Information(
                hash, provider, context.getString(R.string.hash_of_android_id),
                snapshot.settingsInfo.value?.androidIdHash?.value.toString()
            ),
            Information(
                hash, provider, context.getString(R.string.last_knwon_location),
                "$lastKnownLatitude, $lastKnownLongitude"
            ),
            Information(
                hash, provider, context.getString(R.string.last_knwon_address), lastKnownAddress
            ),
            Information(
                hash, provider, context.getString(R.string.current_location),
                "$currentLatitude, $currentLongitude"
            ),
            Information(
                hash, provider, context.getString(R.string.current_address), currentAddress
            ),
            Information(
                hash, provider, context.getString(R.string.board), deviceInfo?.board.toString()
            ),
            Information(
                hash, provider, context.getString(R.string.brand), deviceInfo?.brand.toString()
            ),
            Information(
                hash, provider, context.getString(R.string.device_name),
                deviceInfo?.device.toString()
            ),
            Information(
                hash, provider, context.getString(R.string.device_build_id),
                deviceInfo?.display.toString()
            ),
            Information(
                hash, provider, context.getString(R.string.device_build_fingerprint),
                deviceInfo?.fingerprint.toString()
            ),
            Information(
                hash, provider, context.getString(R.string.hardware),
                deviceInfo?.hardware.toString()
            ),
            Information(
                hash, provider, context.getString(R.string.manufacturer),
                deviceInfo?.manufacturer.toString()
            ),
            Information(
                hash, provider, context.getString(R.string.end_product_name),
                deviceInfo?.model.toString()
            ),
            Information(
                hash, provider, context.getString(R.string.overall_product_name),
                deviceInfo?.product.toString()
            ),
            Information(
                hash, provider, context.getString(R.string.device_build_tags),
                deviceInfo?.tags.toString()
            ),
            Information(
                hash, provider, context.getString(R.string.device_build_time),
                deviceInfo?.buildTime?.let { DateFormat.getInstance().format(it) }.toString()
            ),
            Information(
                hash, provider, context.getString(R.string.device_build_type),
                deviceInfo?.type.toString()
            )
        )

        return Result.success()
    }
}