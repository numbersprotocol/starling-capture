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
            enableLocaleInfo = false
            enableSensorInfo = InfoSnapshotConfig.collectSensorInfo
        }.snap()

        informationSet.add(
            Information(
                hash, name, "Timestamp",
                Instant.now().toString(),
                Information.Importance.LOW
            )
        )

        snapshot.settingsInfo.value?.androidIdHash?.value?.let {
            informationSet.add(
                Information(
                    hash, name, "Hash of Android ID",
                    it, Information.Importance.HIGH
                )
            )
        }

        snapshot.locationInfo.value?.apply {
            lastKnown.value?.apply {
                address.value?.apply {
                    informationSet.add(
                        Information(
                            hash, name, "Last Known GPS Address", this,
                            Information.Importance.HIGH, locationType
                        )
                    )
                }
                informationSet.addAll(
                    setOf(
                        Information(
                            hash, name, "Last Known GPS Latitude",
                            "$latitude", Information.Importance.HIGH, locationType
                        ),
                        Information(
                            hash, name, "Last Known GPS Longitude",
                            "$longitude", Information.Importance.HIGH, locationType
                        ),
                        Information(
                            hash, name, "Last Known GPS Accuracy",
                            "$accuracy", Information.Importance.LOW, locationType
                        ),
                        Information(
                            hash, name, "Last Known GPS Altitude",
                            "$altitude", Information.Importance.LOW, locationType
                        ),
                        Information(
                            hash, name, "Last Known GPS Bearing",
                            "$bearing", Information.Importance.LOW, locationType
                        ),
                        Information(
                            hash, name, "Last Known GPS Speed",
                            "$speed", Information.Importance.LOW, locationType
                        ),
                        Information(
                            hash, name, "Last Known GPS Timestamp",
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
                            hash, name, "Current GPS Address", this,
                            Information.Importance.HIGH, locationType
                        )
                    )
                }
                informationSet.addAll(
                    setOf(
                        Information(
                            hash, name, "Current GPS Latitude",
                            "$latitude", Information.Importance.HIGH, locationType
                        ),
                        Information(
                            hash, name, "Current GPS Longitude",
                            "$longitude", Information.Importance.HIGH, locationType
                        ),
                        Information(
                            hash, name, "Current GPS Accuracy",
                            "$accuracy", Information.Importance.LOW, locationType
                        ),
                        Information(
                            hash, name, "Current GPS Altitude",
                            "$altitude", Information.Importance.LOW, locationType
                        ),
                        Information(
                            hash, name, "Current GPS Bearing",
                            "$bearing", Information.Importance.LOW, locationType
                        ),
                        Information(
                            hash, name, "Current GPS Speed",
                            "$speed", Information.Importance.LOW, locationType
                        ),
                        Information(
                            hash, name, "Current GPS Timestamp",
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
                        hash, name, "Board", board,
                        Information.Importance.LOW, deviceType
                    ),
                    Information(
                        hash, name, "Brand", brand,
                        Information.Importance.LOW, deviceType
                    ),
                    Information(
                        hash, name, "Device Name",
                        device, Information.Importance.LOW, deviceType
                    ),
                    Information(
                        hash, name, "Device Build ID",
                        display, Information.Importance.LOW, deviceType
                    ),
                    Information(
                        hash, name, "Device Build Fingerprint",
                        fingerprint, Information.Importance.LOW, deviceType
                    ),
                    Information(
                        hash, name, "Hardware",
                        hardware, Information.Importance.LOW, deviceType
                    ),
                    Information(
                        hash, name, "Manufacturer",
                        manufacturer, Information.Importance.LOW, deviceType
                    ),
                    Information(
                        hash, name, "End Product Name",
                        model, Information.Importance.LOW, deviceType
                    ),
                    Information(
                        hash, name, "Overall Product Name",
                        product, Information.Importance.LOW, deviceType
                    ),
                    Information(
                        hash, name, "Device Build Tags",
                        tags, Information.Importance.LOW, deviceType
                    ),
                    Information(
                        hash, name, "Device Build Time",
                        buildTime.let { DateFormat.getInstance().format(it) }.toString(),
                        Information.Importance.LOW, deviceType
                    ),
                    Information(
                        hash, name, "Device Build Type",
                        type, Information.Importance.LOW, deviceType
                    )
                )
            )
        }

        snapshot.sensorInfo.value?.apply {
            informationSet.addAll(
                setOf(
                    Information(
                        hash, name, "Accelerometer",
                        "${accelerometer.value?.value ?: accelerometer.nullReason}",
                        Information.Importance.LOW, sensorType
                    ),
                    Information(
                        hash, name, "Gravity",
                        "${gravity.value?.value ?: gravity.nullReason}",
                        Information.Importance.LOW, sensorType
                    ),
                    Information(
                        hash, name, "Gyroscope",
                        "${gyroscope.value?.value ?: gyroscope.nullReason}",
                        Information.Importance.LOW, sensorType
                    ),
                    Information(
                        hash, name, "Light",
                        "${light.value?.value ?: light.nullReason}",
                        Information.Importance.LOW, sensorType
                    ),
                    Information(
                        hash, name, "Linear Accelerometer",
                        "${linearAcceleration.value?.value ?: linearAcceleration.nullReason}",
                        Information.Importance.LOW, sensorType
                    ),
                    Information(
                        hash, name, "Game Rotation Vector",
                        "${gameRotationVector.value?.value ?: gameRotationVector.nullReason}",
                        Information.Importance.LOW, sensorType
                    ),
                    Information(
                        hash, name, "Geomagnetic Rotation Vector",
                        "${geomagneticRotationVector.value?.value ?: geomagneticRotationVector.nullReason}",
                        Information.Importance.LOW, sensorType
                    ),
                    Information(
                        hash, name, "Magnetic Field",
                        "${magneticField.value?.value ?: magneticField.nullReason}",
                        Information.Importance.LOW, sensorType
                    ),
                    Information(
                        hash, name, "Rotation Vector",
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
        val sensorType = Information.Type(R.string.sensor, R.drawable.ic_settings_input_antenna)
    }
}