package io.numbersprotocol.starlingcapture.collector.infosnapshot

import android.content.Context
import androidx.work.WorkerParameters
import io.numbers.infosnapshot.InfoSnapshotBuilder
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.collector.InformationAndSignatureProvider
import io.numbersprotocol.starlingcapture.data.information.Information
import org.koin.core.KoinComponent
import java.text.DateFormat
import java.time.Instant

class InfoSnapshotProvider(
    context: Context,
    params: WorkerParameters
) : InformationAndSignatureProvider(context, params), KoinComponent {

    override val name = InfoSnapshotConfig.name

    private val informationSet = mutableSetOf<Information>()

    override suspend fun provideInformation(): Collection<Information>? {
        val snapshot = InfoSnapshotBuilder(context).apply {
            duration = 10000
            enableDeviceInfo = InfoSnapshotConfig.collectDeviceInfo
            enableLocationInfo = InfoSnapshotConfig.collectLocationInfo
            enableLocaleInfo = InfoSnapshotConfig.collectLocaleInfo
            enableSensorInfo = InfoSnapshotConfig.collectSensorInfo
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
                address.value?.apply {
                    informationSet.add(
                        Information(
                            hash, name, context.getString(R.string.last_knwon_gps_address), this,
                            Information.Importance.HIGH, locationType
                        )
                    )
                }
                informationSet.addAll(
                    setOf(
                        Information(
                            hash, name, context.getString(R.string.last_knwon_gps_latitude),
                            "$latitude", Information.Importance.HIGH, locationType
                        ),
                        Information(
                            hash, name, context.getString(R.string.last_knwon_gps_longitude),
                            "$longitude", Information.Importance.HIGH, locationType
                        ),
                        Information(
                            hash, name, context.getString(R.string.last_knwon_gps_accuracy),
                            "$accuracy", Information.Importance.LOW, locationType
                        ),
                        Information(
                            hash, name, context.getString(R.string.last_knwon_gps_altitude),
                            "$altitude", Information.Importance.LOW, locationType
                        ),
                        Information(
                            hash, name, context.getString(R.string.last_knwon_gps_bearing),
                            "$bearing", Information.Importance.LOW, locationType
                        ),
                        Information(
                            hash, name, context.getString(R.string.last_knwon_gps_speed),
                            "$speed", Information.Importance.LOW, locationType
                        ),
                        Information(
                            hash, name, context.getString(R.string.last_knwon_gps_timestamp),
                            "${Instant.ofEpochMilli(time)}",
                            Information.Importance.LOW, locationType
                        )
                    )
                )
            }
            current.value?.apply {
                address.value?.apply {
                    informationSet.add(
                        Information(
                            hash, name, context.getString(R.string.current_gps_address), this,
                            Information.Importance.HIGH, locationType
                        )
                    )
                }
                informationSet.addAll(
                    setOf(
                        Information(
                            hash, name, context.getString(R.string.current_gps_latitude),
                            "$latitude", Information.Importance.HIGH, locationType
                        ),
                        Information(
                            hash, name, context.getString(R.string.current_gps_longitude),
                            "$longitude", Information.Importance.HIGH, locationType
                        ),
                        Information(
                            hash, name, context.getString(R.string.current_gps_accuracy),
                            "$accuracy", Information.Importance.LOW, locationType
                        ),
                        Information(
                            hash, name, context.getString(R.string.current_gps_altitude),
                            "$altitude", Information.Importance.LOW, locationType
                        ),
                        Information(
                            hash, name, context.getString(R.string.current_gps_bearing),
                            "$bearing", Information.Importance.LOW, locationType
                        ),
                        Information(
                            hash, name, context.getString(R.string.current_gps_speed),
                            "$speed", Information.Importance.LOW, locationType
                        ),
                        Information(
                            hash, name, context.getString(R.string.current_gps_timestamp),
                            "${Instant.ofEpochMilli(time)}",
                            Information.Importance.LOW, locationType
                        )
                    )
                )
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

        snapshot.localeInfo.value?.also {
            informationSet.addAll(
                setOf(
                    Information(
                        hash, name, context.getString(R.string.name), it.name,
                        Information.Importance.LOW, localeType
                    ),
                    Information(
                        hash, name, context.getString(R.string.country), it.country,
                        Information.Importance.LOW, localeType
                    ),
                    Information(
                        hash, name, context.getString(R.string.variant), it.variant,
                        Information.Importance.LOW, localeType
                    ),
                    Information(
                        hash, name, context.getString(R.string.language), it.language,
                        Information.Importance.LOW, localeType
                    ),
                    Information(
                        hash, name, context.getString(R.string.script), it.script,
                        Information.Importance.LOW, localeType
                    )
                )
            )
        }

        snapshot.sensorInfo.value?.apply {
            informationSet.addAll(
                setOf(
                    Information(
                        hash, name, context.getString(R.string.accelerometer),
                        "${accelerometer.value?.value ?: accelerometer.nullReason}",
                        Information.Importance.LOW, sensorType
                    ),
                    Information(
                        hash, name, context.getString(R.string.gravity),
                        "${gravity.value?.value ?: gravity.nullReason}",
                        Information.Importance.LOW, sensorType
                    ),
                    Information(
                        hash, name, context.getString(R.string.gyroscope),
                        "${gyroscope.value?.value ?: gyroscope.nullReason}",
                        Information.Importance.LOW, sensorType
                    ),
                    Information(
                        hash, name, context.getString(R.string.light),
                        "${light.value?.value ?: light.nullReason}",
                        Information.Importance.LOW, sensorType
                    ),
                    Information(
                        hash, name, context.getString(R.string.linear_accelerometer),
                        "${linearAcceleration.value?.value ?: linearAcceleration.nullReason}",
                        Information.Importance.LOW, sensorType
                    ),
                    Information(
                        hash, name, context.getString(R.string.game_rotation_vector),
                        "${gameRotationVector.value?.value ?: gameRotationVector.nullReason}",
                        Information.Importance.LOW, sensorType
                    ),
                    Information(
                        hash, name, context.getString(R.string.geomagnetic_rotation_vector),
                        "${geomagneticRotationVector.value?.value ?: geomagneticRotationVector.nullReason}",
                        Information.Importance.LOW, sensorType
                    ),
                    Information(
                        hash, name, context.getString(R.string.magnetic_field),
                        "${magneticField.value?.value ?: magneticField.nullReason}",
                        Information.Importance.LOW, sensorType
                    ),
                    Information(
                        hash, name, context.getString(R.string.rotation_vector),
                        "${rotationVector.value?.value ?: rotationVector.nullReason}",
                        Information.Importance.LOW, sensorType
                    )
                )
            )
        }

        return informationSet
    }

    companion object {
        val locationType = Information.Type(R.string.location, R.drawable.ic_location)
        val deviceType = Information.Type(R.string.device, R.drawable.ic_device_information)
        val localeType = Information.Type(R.string.locale, R.drawable.ic_language)
        val sensorType = Information.Type(R.string.sensor, R.drawable.ic_settings_input_antenna)
    }
}