package io.numbersprotocol.starlingcapture.collector

import android.content.Context
import androidx.work.WorkerParameters
import io.numbersprotocol.starlingcapture.R
import io.numbersprotocol.starlingcapture.data.information.Information
import io.numbersprotocol.starlingcapture.data.information.InformationRepository
import org.koin.core.inject

abstract class InformationProvider(
    context: Context, params: WorkerParameters
) : CollectWorker(context, params) {

    private val informationRepository: InformationRepository by inject()

    override suspend fun collect() {
        notifyStartCollecting(R.string.message_collecting_proof_information)
        informationRepository.add(*provide().toTypedArray())
    }

    abstract suspend fun provide(): Collection<Information>
}