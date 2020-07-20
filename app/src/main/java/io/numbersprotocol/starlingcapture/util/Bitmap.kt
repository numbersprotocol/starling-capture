package io.numbersprotocol.starlingcapture.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import java.io.ByteArrayOutputStream
import java.io.File

fun File.decodeToBitmap(): Bitmap = readBytes().decodeToBitmap()

fun ByteArray.decodeToBitmap(): Bitmap = BitmapFactory.decodeByteArray(this, 0, size)

fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat) {
    val stream = ByteArrayOutputStream()
    bitmap.compress(format, 100, stream)
    writeBytes(stream.toByteArray())
}

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}