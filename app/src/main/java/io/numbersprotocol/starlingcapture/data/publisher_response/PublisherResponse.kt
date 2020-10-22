package io.numbersprotocol.starlingcapture.data.publisher_response

import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.TypeConverter
import io.numbersprotocol.starlingcapture.data.proof.Proof

@Entity(
    primaryKeys = ["proofHash", "publisher", "name"],
    foreignKeys = [ForeignKey(
        entity = Proof::class,
        parentColumns = ["hash"],
        childColumns = ["proofHash"],
        onDelete = CASCADE
    )]
)
data class PublisherResponse(
    val proofHash: String,
    val publisher: String,
    @StringRes val name: Int,
    val type: Type,
    val content: String
) {

    enum class Type {
        Image, Url;

        class RoomTypeConverter {
            @TypeConverter
            fun toType(value: String) = valueOf(value)

            @TypeConverter
            fun fromType(type: Type) = type.name
        }
    }
}