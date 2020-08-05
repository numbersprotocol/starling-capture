package io.numbersprotocol.starlingcapture.data.publish_history

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PublishHistoryDao {

    @Query("SELECT * FROM PublishHistory WHERE PublishHistory.proofHash = :proofHash")
    abstract fun queryByProofHashWithFlow(proofHash: String): Flow<List<PublishHistory>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(publishHistory: PublishHistory): Long

    @Update
    abstract suspend fun update(publishHistory: PublishHistory): Int

    @Transaction
    open suspend fun upsert(publishHistory: PublishHistory) {
        val rawId = insert(publishHistory)
        if (rawId == -1L) update(publishHistory)
    }
}