package io.numbersprotocol.starlingcapture.data.serialization

import com.squareup.moshi.Moshi
import io.numbersprotocol.starlingcapture.data.information.Information
import io.numbersprotocol.starlingcapture.data.information.Information.Companion.comparator
import io.numbersprotocol.starlingcapture.data.information.InformationRepository
import io.numbersprotocol.starlingcapture.data.proof.Proof
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

class SortedProofInformation private constructor(
    val proof: Proof,
    val information: SortedSet<Information>
) : KoinComponent {

    private val moshi: Moshi by inject()

    init {
        checkInformationIsRelated()
    }

    private fun checkInformationIsRelated() {
        if (information.any { it.proofHash != proof.hash }) error("One or more information is not related.")
    }

    fun toJson(): String {
        val jsonAdapter = moshi.adapter(SortedProofInformation::class.java)
        return jsonAdapter.toJson(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SortedProofInformation

        if (proof != other.proof) return false
        if (information != other.information) return false

        return true
    }

    override fun hashCode(): Int {
        var result = proof.hashCode()
        result = 31 * result + information.hashCode()
        return result
    }

    companion object {

        fun create(
            proof: Proof,
            relatedInformation: Collection<Information>
        ): SortedProofInformation {
            return SortedProofInformation(proof, relatedInformation.toSortedSet(comparator))
        }

        suspend fun create(
            proof: Proof,
            informationRepository: InformationRepository
        ): SortedProofInformation {
            return create(proof, informationRepository.getByProof(proof))
        }
    }
}