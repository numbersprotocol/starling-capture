package org.starlinglab.starlingcapture.publisher.starling_integrity

import android.content.Context
import androidx.work.WorkerParameters
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.data.caption.CaptionRepository
import io.numbersprotocol.starlingcapture.data.proof.Proof
import io.numbersprotocol.starlingcapture.data.proof.ProofRepository
import io.numbersprotocol.starlingcapture.data.serialization.Serialization
import io.numbersprotocol.starlingcapture.publisher.ProofPublisher
import kotlinx.coroutines.delay
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class StarlingIntegrityPublisher(
    context: Context,
    params: WorkerParameters
) : ProofPublisher(context, params), KoinComponent {

    override val name = context.getString(R.string.starling_integrity)

    private val proofRepository: ProofRepository by inject()
    private val captionRepository: CaptionRepository by inject()
    private val starlingIntegrityApi: StarlingIntegrityApi by inject()

    override suspend fun publish(proof: Proof): Result {
        var retryTimes = 3
        val retryWaitingTimeMillis = 5000L
        var exception: Throwable
        val metaJson = Serialization.generateInformationJson(proof)
        val signatureJson = Serialization.generateSignaturesJson(proof)
        val captionText = captionRepository.getByProof(proof)?.text ?: ""

        while (true) {
            try {
                val result = starlingIntegrityApi.createMedia(
                    authToken = starlingIntegrityPublisherConfig.authToken,
                    rawFile = convertFileToMultipartBodyPart(proof),
                    targetProvider = TargetProvider.Starling.toString(),
                    information = metaJson,
                    signatures = signatureJson,
                    caption = captionText,
                    tag = "intern-camp"
                )
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

    private fun convertFileToMultipartBodyPart(proof: Proof): MultipartBody.Part {
        val rawFile = proofRepository.getRawFile(proof)
        val mediaType = MediaType.parse(proof.mimeType.toString())
        val requestBody = RequestBody.create(mediaType, rawFile)
        return MultipartBody.Part.createFormData("file", rawFile.name, requestBody)
    }

    companion object {
        enum class TargetProvider(private val string: String) {
            Starling("Starling");

            override fun toString(): String {
                return string
            }
        }
    }
}