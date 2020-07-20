@file:Suppress("UNUSED_VARIABLE")

package io.numbersprotocol.starlingcapture.publisher.numbers_storage

import android.content.Context
import androidx.work.WorkerParameters
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.data.caption.CaptionRepository
import io.numbersprotocol.starlingcapture.data.proof.Proof
import io.numbersprotocol.starlingcapture.data.proof.ProofRepository
import io.numbersprotocol.starlingcapture.data.serialization.Serialization
import io.numbersprotocol.starlingcapture.publisher.ProofPublisher
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class NumbersStoragePublisher(
    context: Context,
    params: WorkerParameters
) : ProofPublisher(context, params), KoinComponent {

    override val name = context.getString(R.string.numbers_storage)

    private val proofRepository: ProofRepository by inject()
    private val captionRepository: CaptionRepository by inject()
    private val numbersStorageApi: NumbersStorageApi by inject()

    override suspend fun publish(): Result {
        val proof = proofRepository.getByHash(proofHash)!!
        val metaJson = Serialization.generateInformationJson(proof)
        val signatureJson = Serialization.generateSignaturesJson(proof)
        val captionText = captionRepository.getByProof(proof)?.text ?: ""

        val result = numbersStorageApi.createMedia(
            authToken = numbersStoragePublisherConfig.authToken,
            rawFile = convertFileToMultipartBodyPart(proof),
            targetProvider = TargetProvider.Numbers.toString(),
            information = metaJson,
            signatures = signatureJson,
            caption = captionText,
            tag = "intern-camp"
        )
        Timber.i("Publish result: $result")
        return Result.success()
    }

    private fun convertFileToMultipartBodyPart(proof: Proof): MultipartBody.Part {
        val rawFile = proofRepository.getRawFile(proof)
        val mediaType = MediaType.parse(proof.mimeType.toString())
        val requestBody = RequestBody.create(mediaType, rawFile)
        return MultipartBody.Part.createFormData("file", rawFile.name, requestBody)
    }

    companion object {
        enum class TargetProvider(private val string: String) {
            Numbers("Numbers");

            override fun toString(): String {
                return string
            }
        }
    }
}