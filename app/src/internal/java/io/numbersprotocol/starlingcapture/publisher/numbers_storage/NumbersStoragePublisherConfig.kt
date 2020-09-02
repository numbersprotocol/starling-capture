package io.numbersprotocol.starlingcapture.publisher.numbers_storage

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.publisher.ProofPublisher
import io.numbersprotocol.starlingcapture.publisher.PublisherConfig
import io.numbersprotocol.starlingcapture.util.stringLiveData
import io.numbersprotocol.starlingcapture.util.stringPref

class NumbersStoragePublisherConfig : PublisherConfig(
    R.string.numbers_storage,
    R.drawable.ic_n,
    R.id.toNumbersStoragePublisherLoginFragment,
    { context, proofs ->
        proofs.forEach { proof ->
            val publishRequest = OneTimeWorkRequestBuilder<NumbersStoragePublisher>()
                .setInputData(workDataOf(ProofPublisher.KEY_PROOF_HASH to proof.hash))
                .build()
            WorkManager.getInstance(context).enqueue(publishRequest)
        }
    }) {

    // FIXME: use encrypted shared preferences to store the token
    var authToken by sharedPreference.stringPref(KEY_AUTH_TOKEN)
    var userName by sharedPreference.stringPref(KEY_USER_NAME)
    val userNameLiveData = sharedPreference.stringLiveData(KEY_USER_NAME)
    var email by sharedPreference.stringPref(KEY_EMAIL)
    val emailLiveData = sharedPreference.stringLiveData(KEY_EMAIL)

    companion object {
        private const val KEY_AUTH_TOKEN = "KEY_AUTH_TOKEN"
        private const val KEY_USER_NAME = "KEY_USER_NAME"
        private const val KEY_EMAIL = "KEY_EMAIL"
    }
}

val numbersStoragePublisherConfig = NumbersStoragePublisherConfig()