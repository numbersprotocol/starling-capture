package io.numbersprotocol.starlingcapture.data.publisher_response

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query

@Dao
interface PublisherResponseDao {

    @Query("SELECT PublisherResponse.publisher FROM PublisherResponse WHERE PublisherResponse.proofHash = :proofHash GROUP BY publisher")
    fun queryPublishersByProofHashWithLiveData(proofHash: String): LiveData<List<String>>

    @Query("SELECT * FROM PublisherResponse WHERE PublisherResponse.proofHash = :proofHash AND PublisherResponse.publisher = :publisher")
    fun queryByProofHashAndPublisherWithLiveData(
        proofHash: String,
        publisher: String
    ): LiveData<List<PublisherResponse>>

    @Insert(onConflict = IGNORE)
    suspend fun insert(vararg publisherResponses: PublisherResponse): List<Long>
}