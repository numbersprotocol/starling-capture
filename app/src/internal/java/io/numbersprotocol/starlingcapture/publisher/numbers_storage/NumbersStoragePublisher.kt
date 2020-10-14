package io.numbersprotocol.starlingcapture.publisher.numbers_storage

import android.content.Context
import androidx.work.WorkerParameters
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.data.attached_image.AttachedImage
import io.numbersprotocol.starlingcapture.data.attached_image.AttachedImageRepository
import io.numbersprotocol.starlingcapture.data.caption.CaptionRepository
import io.numbersprotocol.starlingcapture.data.proof.Proof
import io.numbersprotocol.starlingcapture.data.proof.ProofRepository
import io.numbersprotocol.starlingcapture.data.serialization.Serialization
import io.numbersprotocol.starlingcapture.publisher.ProofPublisher
import io.numbersprotocol.starlingcapture.util.MimeType
import kotlinx.coroutines.delay
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.io.File

class NumbersStoragePublisher(
    context: Context,
    params: WorkerParameters
) : ProofPublisher(context, params), KoinComponent {

    override val name = context.getString(R.string.numbers_storage)

    private val proofRepository: ProofRepository by inject()
    private val captionRepository: CaptionRepository by inject()
    private val attachedImageRepository: AttachedImageRepository by inject()
    private val numbersStorageApi: NumbersStorageApi by inject()

    override suspend fun publish(proof: Proof): Result {
        var retryTimes = 3
        val retryWaitingTimeMillis = 5000L
        var exception: Throwable
        val metaJson = Serialization.generateInformationJson(proof)
        val signatureJson = Serialization.generateSignaturesJson(proof)
        val captionText = captionRepository.getByProof(proof)?.text ?: ""
        val attachedImage = attachedImageRepository.getByProof(proof)

        while (true) {
            try {
                val result = if (attachedImage == null) {
                    numbersStorageApi.createMedia(
                        authToken = numbersStoragePublisherConfig.authToken,
                        rawFile = getProofRawMediaFileBodyPart(proof),
                        targetProvider = TargetProvider.Numbers.toString(),
                        information = metaJson,
                        signatures = signatureJson,
                        caption = captionText,
                        tag = ""
                    )
                } else {
                    Timber.i("Upload with attached image: $attachedImage")
                    numbersStorageApi.createMedia(
                        authToken = numbersStoragePublisherConfig.authToken,
                        rawFile = getProofRawMediaFileBodyPart(proof),
                        targetProvider = TargetProvider.Signature.toString(),
                        information = metaJson,
                        signatures = signatureJson,
                        caption = captionText,
                        attachedImageFile = getAttachedImageFileBodyPart(attachedImage),
                        tag = "dotted-sign"
                    )
                }
                Timber.i("Publish result: $result")
                return Result.success()
            } catch (e: Exception) {
                Timber.e("[retry ${3 - retryTimes}] ${e.message}")
                exception = e
                retryTimes -= 1
                if (retryTimes <= 0) break
                delay(retryWaitingTimeMillis)
            }
        }
        throw exception
    }

    private fun getProofRawMediaFileBodyPart(proof: Proof): MultipartBody.Part {
        val rawFile = proofRepository.getRawFile(proof)
        return convertFileToMultipartBodyPart("file", rawFile, proof.mimeType)
    }

    private fun getAttachedImageFileBodyPart(attachedImage: AttachedImage): MultipartBody.Part {
        val rawFile = attachedImageRepository.getRawFile(attachedImage)
        return convertFileToMultipartBodyPart("supporting_image", rawFile, MimeType.JPEG)
    }

    private fun convertFileToMultipartBodyPart(
        name: String,
        file: File,
        mimeType: MimeType
    ): MultipartBody.Part {
        val mediaType = MediaType.parse(mimeType.toString())
        val requestBody = RequestBody.create(mediaType, file)
        return MultipartBody.Part.createFormData(name, file.name, requestBody)
    }

    companion object {
        enum class TargetProvider(private val string: String) {
            Numbers("Numbers"),
            Signature("Signature");

            override fun toString(): String {
                return string
            }
        }
    }
}