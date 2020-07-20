package io.numbersprotocol.starlingcapture.data.caption

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import io.numbersprotocol.starlingcapture.data.proof.Proof

@Entity(
    foreignKeys = [ForeignKey(
        entity = Proof::class,
        parentColumns = ["hash"],
        childColumns = ["proofHash"],
        onDelete = CASCADE
    )]
)
data class Caption(
    @PrimaryKey val proofHash: String,
    val text: String
)