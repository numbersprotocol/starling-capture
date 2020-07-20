package io.numbersprotocol.starlingcapture

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun test() {
        val a: String? = null

        a?.also {
            println(it)
        } ?: also {
            println("null")
        }
    }
}