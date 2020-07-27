package io.numbersprotocol.starlingcapture.util

import java.util.*

fun ByteArray.asHex() =
    joinToString(separator = "") { String.format("%02x", (it.toInt() and 0xFF)) }

fun String.hexAsByteArray() = chunked(2)
    .map { it.toUpperCase(Locale.getDefault()).toInt(16).toByte() }
    .toByteArray()