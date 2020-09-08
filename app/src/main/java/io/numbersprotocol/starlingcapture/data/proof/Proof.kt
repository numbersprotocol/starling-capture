package io.numbersprotocol.starlingcapture.data.proof

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import io.numbersprotocol.starlingcapture.util.MimeType
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.time.Instant

@JsonClass(generateAdapter = true)
@Entity
@Parcelize
data class Proof(
    @PrimaryKey val hash: String,
    val mimeType: MimeType,
    val timestamp: Long
) : Parcelable {
    @Ignore
    @IgnoredOnParcel
    val formattedTimestamp: String = Instant.ofEpochMilli(timestamp).run { toString() }
}