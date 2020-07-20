package io.numbersprotocol.starlingcapture.data.signature

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SignatureDao {

    @Query("SELECT * FROM Signature WHERE Signature.proofHash = :proofHash")
    suspend fun queryByProofHash(proofHash: String): List<Signature>

    @Query("SELECT * FROM Signature WHERE Signature.proofHash = :proofHash")
    fun queryByProofHashWithLiveData(proofHash: String): LiveData<List<Signature>>

    @Insert
    suspend fun insert(vararg signatures: Signature): List<Long>

    @Query("DELETE FROM Signature WHERE Signature.proofHash = :proofHash")
    suspend fun deleteByProofHash(proofHash: String): Int
}