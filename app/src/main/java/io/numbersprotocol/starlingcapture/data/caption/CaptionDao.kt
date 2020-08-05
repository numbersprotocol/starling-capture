package io.numbersprotocol.starlingcapture.data.caption

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
abstract class CaptionDao {

    @Query("SELECT * FROM Caption WHERE Caption.proofHash = :proofHash")
    abstract suspend fun queryByProofHash(proofHash: String): Caption?

    @Query("SELECT * FROM Caption WHERE Caption.proofHash = :proofHash")
    abstract fun queryByProofHashWithLiveData(proofHash: String): LiveData<Caption>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(caption: Caption): Long

    @Update
    abstract suspend fun update(caption: Caption): Int

    @Transaction
    open suspend fun upsert(caption: Caption) {
        val rowId = insert(caption)
        if (rowId == -1L) update(caption)
    }
}