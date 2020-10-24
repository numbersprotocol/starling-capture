package io.numbersprotocol.starlingcapture.data

import android.content.Context
import io.numbersprotocol.starlingcapture.util.sha256
import timber.log.Timber
import java.io.File

interface RawFileRepository {
    /**
     * Copy [file] to add raw file to internal storage.
     * @param file: The original source of raw file which will be copied.
     * @return: The file added in the internal storage. The name of the returned file will be its hash with original extension.
     */
    fun addRawFile(file: File): File
    fun getRawFile(fileName: String): File
    fun removeRawFile(file: File)
}

class RawFileRepositoryImpl(context: Context) : RawFileRepository {

    private val rawFilesDir = context.filesDir.resolve("raw")

    override fun addRawFile(file: File): File {
        val fileHash = file.sha256()
        return file.copyTo(rawFilesDir.resolve("$fileHash.${file.extension}"), overwrite = true)
    }

    override fun getRawFile(fileName: String) = rawFilesDir.resolve(fileName)

    override fun removeRawFile(file: File) {
        if (file.exists()) file.delete()
        else Timber.e("Cannot delete the raw file as the file does not exist: ${file.absoluteFile}")
    }
}