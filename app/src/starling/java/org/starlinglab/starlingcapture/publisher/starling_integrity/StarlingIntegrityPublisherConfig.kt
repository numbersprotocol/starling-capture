package org.starlinglab.starlingcapture.publisher.starling_integrity

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.publisher.ProofPublisher
import io.numbersprotocol.starlingcapture.publisher.PublisherConfig
import io.numbersprotocol.starlingcapture.util.stringPref

class StarlingIntegrityPublisherConfig : PublisherConfig(
    R.string.starling_integrity,
    R.drawable.ic_starling,
    R.id.toStarlingIntegrityPublisherLoginFragment,
    { context, proofs ->
        proofs.forEach { proof ->
            val publishRequest = OneTimeWorkRequestBuilder<StarlingIntegrityPublisher>()
                .setInputData(workDataOf(ProofPublisher.KEY_PROOF_HASH to proof.hash))
                .build()
            WorkManager.getInstance(context).enqueue(publishRequest)
        }
    }) {

    // FIXME: use encrypted shared preferences to store the token
    var authToken by sharedPreference.stringPref(KEY_AUTH_TOKEN)

    companion object {
        private const val KEY_AUTH_TOKEN = "KEY_AUTH_TOKEN"
    }
}

val starlingIntegrityPublisherConfig = StarlingIntegrityPublisherConfig()