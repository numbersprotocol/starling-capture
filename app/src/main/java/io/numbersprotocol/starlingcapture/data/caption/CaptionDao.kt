package io.numbersprotocol.starlingcapture.data.caption

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CaptionDao {

    @Query("SELECT * FROM Caption WHERE Caption.proofHash = :proofHash")
    suspend fun queryByProofHash(proofHash: String): Caption?

    @Query("SELECT * FROM Caption WHERE Caption.proofHash = :proofHash")
    fun queryByProofHashWithLiveData(proofHash: String): LiveData<Caption>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(caption: Caption): Long

    @Update
    suspend fun update(caption: Caption): Int

    @Query("DELETE FROM Caption WHERE Caption.proofHash = :proofHash")
    suspend fun deleteByProofHash(proofHash: String)
}