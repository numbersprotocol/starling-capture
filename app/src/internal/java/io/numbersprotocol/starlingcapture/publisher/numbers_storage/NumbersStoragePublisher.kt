package io.numbersprotocol.starlingcapture.publisher.numbers_storage

import android.content.Context
import androidx.work.WorkerParameters
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.data.attached_image.AttachedImage
import io.numbersprotocol.starlingcapture.data.attached_image.AttachedImageRepository
import io.numbersprotocol.starlingcapture.data.caption.CaptionRepository
import io.numbersprotocol.starlingcapture.data.proof.Proof
import io.numbersprotocol.starlingcapture.data.proof.ProofRepository
import io.numbersprotocol.starlingcapture.data.publisher_response.PublisherResponse
import io.numbersprotocol.starlingcapture.data.publisher_response.PublisherResponseRepository
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
    private val publisherResponseRepository: PublisherResponseRepository by inject()
    private val numbersStorageApi: NumbersStorageApi by inject()

    override suspend fun publish(proof: Proof): Result {
        val createdMedia = createMedia(proof)
        val response = pollingMediaResponseWithCertifiedQrCode(createdMedia.id)
        storeCreateMediaResponse(response)
        return Result.success()
    }

    private suspend fun createMedia(proof: Proof): Media {
        val retryTimes = 3
        val retryWaitingTimeMillis = 5000L
        val metaJson = Serialization.generateInformationJson(proof)
        val signatureJson = Serialization.generateSignaturesJson(proof)
        val captionText = captionRepository.getByProof(proof)?.text ?: ""
        val attachedImage = attachedImageRepository.getByProof(proof)

        repeat(retryTimes) {
            try {
                val media = createMedia(proof, metaJson, captionText, signatureJson, attachedImage)
                Timber.i("Publish result: $media")
                return media
            } catch (e: Exception) {
                Timber.e("[retry $it] ${e.message}")
                if (it == retryTimes - 1) throw e
                delay(retryWaitingTimeMillis)
            }
        }
        throw IllegalStateException("Fail to create media on $name.")
    }

    private suspend fun createMedia(
        proof: Proof,
        information: String,
        caption: String,
        signatures: String,
        attachedImage: AttachedImage? = null,
    ) = if (attachedImage == null) numbersStorageApi.createMedia(
        authToken = numbersStoragePublisherConfig.authToken,
        rawFile = getProofRawMediaFileBodyPart(proof),
        targetProvider = TargetProvider.Numbers.toString(),
        information = information,
        signatures = signatures,
        caption = caption,
        tag = ""
    ) else numbersStorageApi.createMedia(
        authToken = numbersStoragePublisherConfig.authToken,
        rawFile = getProofRawMediaFileBodyPart(proof),
        targetProvider = TargetProvider.Signature.toString(),
        information = information,
        signatures = signatures,
        caption = caption,
        attachedImageFile = getAttachedImageFileBodyPart(attachedImage),
        tag = "dotted-sign"
    )

    private suspend fun pollingMediaResponseWithCertifiedQrCode(mediaId: String): Media {
        val pollingTimes = 100
        val pollingDelayInMillis = 3000L
        repeat(pollingTimes) {
            val media = numbersStorageApi.getMedia(numbersStoragePublisherConfig.authToken, mediaId)
            Timber.i("Polling media response: $media")
            if (media.certificateQrCode != null) return media
            delay(pollingDelayInMillis)
        }
        throw IllegalStateException("Fail to poll the media response with certified QR code.")
    }

    private suspend fun storeCreateMediaResponse(response: Media) {
        publisherResponseRepository.add(
            PublisherResponse(
                proofHash,
                name,
                R.string.dashboard_link,
                PublisherResponse.Type.Url,
                "$DASHBOARD_URL?mid=${response.id}"
            ),
            PublisherResponse(
                proofHash,
                name,
                R.string.certificate_qr_code,
                PublisherResponse.Type.Image,
                response.certificateQrCode!!
            )
        )
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
        private const val DASHBOARD_URL = "https://numbersprotocol.io/dia-certificate"

        enum class TargetProvider(private val string: String) {
            Numbers("Numbers"),
            Signature("Signature");

            override fun toString(): String {
                return string
            }
        }
    }
}