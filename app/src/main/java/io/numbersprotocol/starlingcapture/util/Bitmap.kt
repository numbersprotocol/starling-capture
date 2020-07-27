package io.numbersprotocol.starlingcapture.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import java.io.File

fun File.decodeToBitmap(): Bitmap = BitmapFactory.decodeStream(inputStream())

fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat) {
    val stream = outputStream()
    bitmap.compress(format, 100, stream)
    stream.close()
}

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}