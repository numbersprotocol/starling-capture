package io.numbersprotocol.starlingcapture.data.proof

import android.content.Context
import io.numbersprotocol.starlingcapture.util.Crypto
import timber.log.Timber
import java.io.File

class ProofRepository(
    context: Context,
    private val proofDao: ProofDao
) {

    private val rawFilesDir = context.filesDir.resolve("raw")

    fun getAllWithDataSource() = proofDao.queryAllWithDataSource()

    suspend fun getAll() = proofDao.queryAll()

    suspend fun getByHash(hash: String) = proofDao.queryByHash(hash)

    suspend fun add(proof: Proof) = proofDao.insert(proof)

    suspend fun remove(proofs: Collection<Proof>): Int {
        var deleteCount = 0
        proofs.forEach { proof -> deleteCount += remove(proof) }
        return deleteCount
    }

    suspend fun remove(proof: Proof): Int {
        val deleteCount = proofDao.delete(proof)
        removeRawFile(proof)
        return deleteCount
    }

    private fun removeRawFile(proof: Proof) {
        val rawFile = getRawFile(proof)
        if (rawFile.exists()) rawFile.delete()
        else Timber.e("Cannot delete raw file as file not exists: $rawFile")
    }

    fun getRawFile(proof: Proof) = rawFilesDir.resolve("${proof.hash}.${proof.mimeType.extension}")

    /**
     * Copy [file] to add raw file to internal storage.
     * @param file: The original source of raw file which will be copied.
     * @return: The file added in the internal storage. The name of the returned file will be its hash with original extension.
     */
    fun addRawFile(file: File): File {
        val fileHash = Crypto.sha256(file)
        return file.copyTo(rawFilesDir.resolve("$fileHash.${file.extension}"), overwrite = true)
    }
}