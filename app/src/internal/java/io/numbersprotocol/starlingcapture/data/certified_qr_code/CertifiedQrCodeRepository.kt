package io.numbersprotocol.starlingcapture.data.certified_qr_code

import android.content.Context
import io.numbersprotocol.starlingcapture.data.RawFileRepository
import io.numbersprotocol.starlingcapture.data.RawFileRepositoryImpl
import io.numbersprotocol.starlingcapture.util.MimeType
import io.numbersprotocol.starlingcapture.util.copyFromInputStream
import java.io.File
import java.io.InputStream

class CertifiedQrCodeRepository(
    private val context: Context
) : RawFileRepository by RawFileRepositoryImpl(context) {
    fun add(stream: InputStream, mimeType: MimeType): File {
        val tempFile = File.createTempFile(
            "${System.currentTimeMillis()}",
            ".${mimeType.extension}",
            context.cacheDir
        )
        tempFile.copyFromInputStream(stream)
        val savedFile = addRawFile(tempFile)
        tempFile.delete()
        return savedFile
    }
}