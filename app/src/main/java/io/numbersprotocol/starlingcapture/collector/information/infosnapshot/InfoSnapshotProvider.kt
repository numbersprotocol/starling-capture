package io.numbersprotocol.starlingcapture.collector.information.infosnapshot

import android.content.Context
import androidx.work.WorkerParameters
import io.numbers.infosnapshot.InfoSnapshotBuilder
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.collector.information.InformationProvider
import io.numbersprotocol.starlingcapture.data.information.Information
import org.koin.core.KoinComponent
import java.text.DateFormat
import java.time.Instant

class InfoSnapshotProvider(
    context: Context,
    params: WorkerParameters
) : InformationProvider(context, params), KoinComponent {

    override val name = "InfoSnapshot"

    private val informationSet = mutableSetOf<Information>()

    override suspend fun provide(): Collection<Information> {
        val snapshot = InfoSnapshotBuilder(context).apply {
            duration = 10000
            enableDeviceInfo = InfoSnapshotConfig.collectDeviceInfo
            enableLocationInfo = InfoSnapshotConfig.collectLocationInfo
            enableLocaleInfo = false
            enableSensorInfo = false
        }.snap()

        informationSet.add(
            Information(
                hash, name, context.getString(R.string.timestamp),
                Instant.now().toString(),
                Information.Importance.LOW
            )
        )

        snapshot.settingsInfo.value?.androidIdHash?.value?.let {
            informationSet.add(
                Information(
                    hash, name, context.getString(R.string.hash_of_android_id),
                    it, Information.Importance.HIGH
                )
            )
        }

        snapshot.locationInfo.value?.apply {
            lastKnown.value?.apply {
                informationSet.add(
                    Information(
                        hash, name, context.getString(R.string.last_knwon_location),
                        "$latitude, $longitude", Information.Importance.HIGH, locationType
                    )
                )
                address.value?.apply {
                    informationSet.add(
                        Information(
                            hash, name, context.getString(R.string.last_knwon_address), this,
                            Information.Importance.HIGH, locationType
                        )
                    )
                }
            }
            current.value?.apply {
                informationSet.add(
                    Information(
                        hash, name, context.getString(R.string.current_location),
                        "$latitude, $longitude", Information.Importance.HIGH, locationType
                    )
                )
                address.value?.apply {
                    informationSet.add(
                        Information(
                            hash, name, context.getString(R.string.current_address), this,
                            Information.Importance.HIGH, locationType
                        )
                    )
                }
            }
        }

        snapshot.deviceInfo.value?.apply {
            informationSet.addAll(
                setOf(
                    Information(
                        hash, name, context.getString(R.string.board), board,
                        Information.Importance.LOW, deviceType
                    ),
                    Information(
                        hash, name, context.getString(R.string.brand), brand,
                        Information.Importance.LOW, deviceType
                    ),
                    Information(
                        hash, name, context.getString(R.string.device_name),
                        device, Information.Importance.LOW, deviceType
                    ),
                    Information(
                        hash, name, context.getString(R.string.device_build_id),
                        display, Information.Importance.LOW, deviceType
                    ),
                    Information(
                        hash, name, context.getString(R.string.device_build_fingerprint),
                        fingerprint, Information.Importance.LOW, deviceType
                    ),
                    Information(
                        hash, name, context.getString(R.string.hardware),
                        hardware, Information.Importance.LOW, deviceType
                    ),
                    Information(
                        hash, name, context.getString(R.string.manufacturer),
                        manufacturer, Information.Importance.LOW, deviceType
                    ),
                    Information(
                        hash, name, context.getString(R.string.end_product_name),
                        model, Information.Importance.LOW, deviceType
                    ),
                    Information(
                        hash, name, context.getString(R.string.overall_product_name),
                        product, Information.Importance.LOW, deviceType
                    ),
                    Information(
                        hash, name, context.getString(R.string.device_build_tags),
                        tags, Information.Importance.LOW, deviceType
                    ),
                    Information(
                        hash, name, context.getString(R.string.device_build_time),
                        buildTime.let { DateFormat.getInstance().format(it) }.toString(),
                        Information.Importance.LOW, deviceType
                    ),
                    Information(
                        hash, name, context.getString(R.string.device_build_type),
                        type, Information.Importance.LOW, deviceType
                    )
                )
            )
        }

        return informationSet
    }

    companion object {
        val locationType = Information.Type(R.string.location, R.drawable.ic_location)
        val deviceType = Information.Type(R.string.device, R.drawable.ic_device_information)
    }
}