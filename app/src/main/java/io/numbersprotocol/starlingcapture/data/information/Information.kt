package io.numbersprotocol.starlingcapture.data.information

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.TypeConverter
import com.squareup.moshi.JsonClass
import io.numbersprotocol.starlingcapture.data.proof.Proof

@JsonClass(generateAdapter = true)
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
    val importance: Importance
) : Comparable<Information> {

    override operator fun compareTo(other: Information) = comparator.compare(this, other)

    enum class Importance(val value: Int) {
        HIGH(10),
        LOW(5);

        companion object {
            fun fromValue(value: Int): Importance {
                values().forEach { if (it.value == value) return it }
                error("Cannot find the Importance with given value: $value")
            }
        }

        class Converter {
            @TypeConverter
            fun toImportance(value: Int) = fromValue(value)

            @TypeConverter
            fun fromImportance(importance: Importance) = importance.value
        }
    }

    companion object {
        val comparator = Comparator<Information> { a, b ->
            when {
                a.provider > b.provider -> 1
                a.provider < b.provider -> -1
                a.name > b.name -> 1
                a.name < b.name -> -1
                else -> 0
            }
        }
    }
}