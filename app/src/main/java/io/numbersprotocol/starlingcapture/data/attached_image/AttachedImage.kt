package io.numbersprotocol.starlingcapture.data.attached_image

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import io.numbersprotocol.starlingcapture.data.proof.Proof
import io.numbersprotocol.starlingcapture.util.MimeType

@Entity(
    primaryKeys = ["proofHash", "fileHash"],
    foreignKeys = [ForeignKey(
        entity = Proof::class,
        parentColumns = ["hash"],
        childColumns = ["proofHash"],
        onDelete = CASCADE
    )]
)
data class AttachedImage(
    val proofHash: String,
    val fileHash: String,
    val mimeType: MimeType
)