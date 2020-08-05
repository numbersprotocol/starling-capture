package io.numbersprotocol.starlingcapture.data.publish_history

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import io.numbersprotocol.starlingcapture.data.proof.Proof

@Entity(
    primaryKeys = ["proofHash", "publisher"],
    foreignKeys = [ForeignKey(
        entity = Proof::class,
        parentColumns = ["hash"],
        childColumns = ["proofHash"],
        onDelete = CASCADE
    )]
)
data class PublishHistory(
    val proofHash: String,
    val publisher: String,
    val timestamp: Long
)