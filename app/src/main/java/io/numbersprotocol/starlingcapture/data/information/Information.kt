package io.numbersprotocol.starlingcapture.data.information

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.TypeConverter
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.data.proof.Proof

@Entity(
    primaryKeys = ["proofHash", "provider", "name"],
    foreignKeys = [ForeignKey(
        entity = Proof::class,
        parentColumns = ["hash"],
        childColumns = ["proofHash"],
        onDelete = CASCADE
    )]
)
data class Information(
    val proofHash: String,
    val provider: String,
    val name: String,
    val value: String,
    val importance: Importance,
    @Embedded(prefix = "type") val type: Type = Type.OTHER_TYPE
) {

    enum class Importance(val value: Int) {
        HIGH(10),
        LOW(5);

        companion object {
            fun fromValue(value: Int): Importance {
                values().forEach { if (it.value == value) return it }
                error("Cannot find the Importance with given value: $value")
            }
        }

        class RoomTypeConverter {
            @TypeConverter
            fun toImportance(value: Int) = fromValue(value)

            @TypeConverter
            fun fromImportance(importance: Importance) = importance.value
        }
    }

    data class Type(
        @StringRes val name: Int,
        @DrawableRes val icon: Int
    ) {
        companion object {
            val OTHER_TYPE = Type(R.string.others, R.drawable.ic_info)
        }
    }
}