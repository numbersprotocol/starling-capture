package io.numbersprotocol.starlingcapture.data.serialization

import com.squareup.moshi.Moshi
import io.numbersprotocol.starlingcapture.data.information.Information
import io.numbersprotocol.starlingcapture.data.proof.Proof
import io.numbersprotocol.starlingcapture.di.mainModule
import io.numbersprotocol.starlingcapture.util.MimeType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

class SortedProofInformationTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger()
        modules(mainModule)
    }

    private val moshi: Moshi by inject()
    private val hash = "6ac3c336e4094835293a3fed8a4b5fedde1b5e2626d9838fed50693bba00af0e"
    private val provider1 = "test_provider1"
    private val provider2 = "test_provider2"
    private lateinit var proof: Proof

    @Before
    fun setUp() {
        proof = Proof(hash, MimeType.JPEG, System.currentTimeMillis())
    }

    @Test
    fun shouldNotContainDifferentProofHash() {
        val differentHash = "9ac3c336e4094835293a3fed8a4b5fedde1b5e2626d9838fed50693bba00af0e"

        assertThrows(IllegalStateException::class.java) {
            SortedProofInformation.create(
                proof,
                listOf(
                    Information(differentHash, provider1, "longitude", "23.05"),
                    Information(hash, provider1, "happiness", "-52")
                )
            )
        }
    }

    @Test
    fun shouldHaveSameOrderWithSameData() {
        val information1 = Information(hash, provider1, "longitude", "23.05")
        val information2 = Information(hash, provider1, "happiness", "-52")
        val information3 = Information(hash, provider2, "happiness", "-52")
        val sortedProofInformation1 = SortedProofInformation.create(
            proof,
            listOf(information1, information3, information2)
        )
        val sortedProofInformation2 = SortedProofInformation.create(
            proof,
            listOf(information3, information2, information1)
        )

        val jsonAdapter = moshi.adapter(SortedProofInformation::class.java)

        assertEquals(
            jsonAdapter.toJson(sortedProofInformation1),
            jsonAdapter.toJson(sortedProofInformation2)
        )
    }

    @Test
    fun testJsonSerialization() {
        val sortedProofInformation = SortedProofInformation.create(
            proof,
            listOf(
                Information(hash, provider1, "longitude", "23.05"),
                Information(hash, provider1, "happiness", "-52")
            )
        )

        val jsonAdapter = moshi.adapter(SortedProofInformation::class.java)
        assertEquals(
            sortedProofInformation,
            jsonAdapter.fromJson(jsonAdapter.toJson(sortedProofInformation))
        )
    }
}