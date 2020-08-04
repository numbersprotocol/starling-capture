package io.numbersprotocol.starlingcapture.data.serialization

import com.squareup.moshi.Moshi
import io.numbersprotocol.starlingcapture.data.information.Information
import io.numbersprotocol.starlingcapture.data.information.InformationRepository
import io.numbersprotocol.starlingcapture.data.proof.Proof
import io.numbersprotocol.starlingcapture.data.serialization.SortedProofInformation.Companion.EssentialInformation.Companion.comparator
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

class SortedProofInformation private constructor(
    val proof: Proof,
    val information: SortedSet<EssentialInformation>
) : KoinComponent {

    private val moshi: Moshi by inject()

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
            return SortedProofInformation(
                proof,
                relatedInformation
                    .map { EssentialInformation.create(it) }
                    .toSortedSet(comparator)
            )
        }

        suspend fun create(
            proof: Proof,
            informationRepository: InformationRepository
        ): SortedProofInformation {
            return create(proof, informationRepository.getByProof(proof))
        }

        data class EssentialInformation(
            val provider: String,
            val name: String,
            val value: String
        ) : Comparable<EssentialInformation> {

            override operator fun compareTo(other: EssentialInformation) =
                comparator.compare(this, other)

            companion object {
                val comparator = Comparator<EssentialInformation> { a, b ->
                    when {
                        a.provider > b.provider -> 1
                        a.provider < b.provider -> -1
                        a.name > b.name -> 1
                        a.name < b.name -> -1
                        else -> 0
                    }
                }

                fun create(information: Information): EssentialInformation {
                    return EssentialInformation(
                        information.provider,
                        information.name,
                        information.value
                    )
                }
            }
        }
    }
}