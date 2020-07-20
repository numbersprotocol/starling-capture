package io.numbersprotocol.starlingcapture.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatorTest {

    @Test
    fun testIpAddressValidator() {
        val legal1 = "192.168.1.2:8080"
        assertTrue(legal1.isIpAddress())
        val legal2 = "192.168.1.2"
        assertTrue(legal2.isIpAddress())
        val illegal1 = "256.1.1.1:65535"
        assertFalse(illegal1.isIpAddress())
        val illegal2 = "1.1.1.1:70000"
        assertFalse(illegal2.isIpAddress())
    }
}