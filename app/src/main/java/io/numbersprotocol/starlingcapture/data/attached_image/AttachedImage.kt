package io.numbersprotocol.starlingcapture.data.attached_image

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import io.numbersprotocol.starlingcapture.data.proof.Proof
import io.numbersprotocol.starlingcapture.util.MimeType

@Entity(
    foreignKeys = [ForeignKey(
        entity = Proof::class,
        parentColumns = ["hash"],
        childColumns = ["proofHash"],
        onDelete = CASCADE
    )]
)
data class AttachedImage(
    @PrimaryKey val proofHash: String,
    val fileHash: String,
    val mimeType: MimeType
)