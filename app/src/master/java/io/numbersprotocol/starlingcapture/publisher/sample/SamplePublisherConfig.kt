package io.numbersprotocol.starlingcapture.publisher.sample

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.publisher.ProofPublisher
import io.numbersprotocol.starlingcapture.publisher.PublisherConfig

val samplePublisherConfig = PublisherConfig(
    R.string.sample_publisher,
    R.drawable.ic_publish,
    R.id.toSamplePublisherFragment
) { context, proofs ->
    proofs.forEach { proof ->
        val publishRequest = OneTimeWorkRequestBuilder<SamplePublisher>()
            .setInputData(workDataOf(ProofPublisher.KEY_PROOF_HASH to proof.hash))
            .build()
        WorkManager.getInstance(context).enqueue(publishRequest)
    }
}