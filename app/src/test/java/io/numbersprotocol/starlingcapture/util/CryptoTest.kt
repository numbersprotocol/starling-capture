package io.numbersprotocol.starlingcapture.util

import org.junit.Assert.assertArrayEquals
import org.junit.Test

class CryptoTest {

    @Test
    fun testByteArrayToHexConversion() {
        val bytes = "hello".toByteArray(Charsets.UTF_8)
        assertArrayEquals(bytes, bytes.asHex().hexAsByteArray())
    }
}