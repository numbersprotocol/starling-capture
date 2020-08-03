package io.numbersprotocol.starlingcapture.data.information

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InformationDao {

    @Query("SELECT * FROM Information WHERE Information.proofHash = :proofHash")
    suspend fun queryByProofHash(proofHash: String): List<Information>

    @Query("SELECT Information.provider FROM Information WHERE Information.proofHash = :proofHash GROUP BY provider")
    fun queryProvidersByProofHashWithLiveData(proofHash: String): LiveData<List<String>>

    @Query("SELECT * FROM Information WHERE Information.proofHash = :proofHash AND Information.provider = :provider AND Information.importance >= :importance")
    fun queryByProofHashAndProviderWithFlow(
        proofHash: String,
        provider: String,
        importance: Information.Importance = Information.Importance.LOW
    ): Flow<List<Information>>

    @Insert
    suspend fun insert(vararg information: Information): List<Long>

    @Query("DELETE FROM Information WHERE Information.proofHash = :proofHash")
    suspend fun deleteByProofHash(proofHash: String): Int
}