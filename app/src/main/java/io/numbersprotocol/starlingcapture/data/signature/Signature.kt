package io.numbersprotocol.starlingcapture.data.signature

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import com.squareup.moshi.JsonClass
import io.numbersprotocol.starlingcapture.data.proof.Proof

@JsonClass(generateAdapter = true)
@Entity(
    primaryKeys = ["proofHash", "provider", "signature", "publicKey"],
    foreignKeys = [ForeignKey(
        entity = Proof::class,
        parentColumns = ["hash"],
        childColumns = ["proofHash"],
        onDelete = CASCADE
    )]
)
data class Signature(
    val proofHash: String,
    val provider: String,
    val signature: String,
    val publicKey: String
)