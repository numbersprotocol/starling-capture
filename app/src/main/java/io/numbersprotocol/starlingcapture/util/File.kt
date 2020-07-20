package io.numbersprotocol.starlingcapture.util

import java.io.File
import java.io.InputStream

fun File.copyFromInputStream(inputStream: InputStream) = outputStream().use {
    inputStream.copyTo(it)
}