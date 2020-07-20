package io.numbersprotocol.starlingcapture.data.proof

import androidx.paging.DataSource
import androidx.room.*

@Dao
interface ProofDao {

    @Query("SELECT * FROM Proof")
    fun queryAllWithDataSource(): DataSource.Factory<Int, Proof>

    @Query("SELECT * FROM Proof")
    suspend fun queryAll(): List<Proof>

    @Query("SELECT * FROM Proof WHERE hash=:hash")
    suspend fun queryByHash(hash: String): Proof?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg proofs: Proof): List<Long>

    @Delete
    suspend fun delete(vararg proofs: Proof): Int
}