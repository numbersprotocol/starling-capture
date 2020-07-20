package io.numbersprotocol.starlingcapture.publisher.sample

import android.content.Context
import androidx.work.WorkerParameters
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.publisher.ProofPublisher
import kotlinx.coroutines.delay

class SamplePublisher(
    context: Context,
    params: WorkerParameters
) : ProofPublisher(context, params) {

    override val name = context.getString(R.string.sample_publisher)

    override suspend fun publish(): Result {
        delay(3000) // Pretend we are publishing the proof.
        return Result.success()
    }
}