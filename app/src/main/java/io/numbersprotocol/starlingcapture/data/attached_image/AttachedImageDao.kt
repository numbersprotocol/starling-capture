package io.numbersprotocol.starlingcapture.data.attached_image

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AttachedImageDao {
    @Query("SELECT * FROM AttachedImage WHERE AttachedImage.proofHash = :proofHash")
    suspend fun queryByProofHash(proofHash: String): AttachedImage?

    @Query("SELECT * FROM AttachedImage WHERE AttachedImage.proofHash = :proofHash")
    fun queryByProofHashWithFlow(proofHash: String): Flow<AttachedImage?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg attachedImage: AttachedImage): List<Long>
}