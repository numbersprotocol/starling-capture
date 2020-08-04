package io.numbersprotocol.starlingcapture.data.serialization

import com.squareup.moshi.Moshi
import io.numbersprotocol.starlingcapture.data.information.Information
import io.numbersprotocol.starlingcapture.data.proof.Proof
import io.numbersprotocol.starlingcapture.di.mainModule
import io.numbersprotocol.starlingcapture.util.MimeType
import org.junit.Assert.assertEquals
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
    private val name1 = "longitude"
    private val value1 = "23.05"
    private val name2 = "happiness"
    private val value2 = "-52"
    private lateinit var proof: Proof

    @Before
    fun setUp() {
        proof = Proof(hash, MimeType.JPEG, System.currentTimeMillis())
    }

    @Test
    fun shouldHaveSameOrderWithSameData() {
        val information1 =
            Information(hash, provider1, name1, value1, Information.Importance.LOW)
        val information2 =
            Information(hash, provider1, name2, value2, Information.Importance.LOW)
        val information3 =
            Information(hash, provider2, name2, value2, Information.Importance.LOW)
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
                Information(hash, provider1, name1, value1, Information.Importance.LOW),
                Information(hash, provider1, name2, value2, Information.Importance.LOW)
            )
        )

        val jsonAdapter = moshi.adapter(SortedProofInformation::class.java)
        assertEquals(
            sortedProofInformation,
            jsonAdapter.fromJson(jsonAdapter.toJson(sortedProofInformation))
        )
    }
}