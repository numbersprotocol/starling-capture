package io.numbersprotocol.starlingcapture.feature.audio

import android.content.pm.ActivityInfo
import android.media.MediaRecorder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import io.numbersprotocol.starlingcapture.databinding.FragmentAudioBinding
import io.numbersprotocol.starlingcapture.util.MimeType
import kotlinx.android.synthetic.main.fragment_audio.*
import kotlinx.android.synthetic.main.fragment_audio.navBackButton
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class AudioFragment : Fragment() {

    private val audioViewModel: AudioViewModel by viewModel()
    private lateinit var audioFile: File
    private lateinit var mediaRecorder: MediaRecorder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentAudioBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = audioViewModel
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navBackButton.setOnClickListener { findNavController().navigateUp() }
        captureAudioButton.setOnClickListener { onRecordAudioClicked() }
    }

    private fun onRecordAudioClicked() {
        if (audioViewModel.isRecordingAudio.value == true) {
            try {
                mediaRecorder.stop()
                mediaRecorder.release()
                audioViewModel.storeMedia(audioFile, MimeType.MP3)
                Toast.makeText(context, "Audio Recording Stopped", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Failed to save audio ", Toast.LENGTH_SHORT).show()
            } finally {
                audioViewModel.isRecordingAudio.value = false
            }
        } else {
            try {
                audioFile = createCachedMediaFile(MimeType.MP3)
                mediaRecorder = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
                    setOutputFile(audioFile)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                    prepare()
                    start()
                }
                audioViewModel.isRecordingAudio.value = true
                Toast.makeText(context, "Audio Recording Started", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Failed to start recording", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun createCachedMediaFile(mimeType: MimeType): File {
        val timestamp = System.currentTimeMillis()
        return File.createTempFile(
            "$timestamp",
            ".${mimeType.extension}",
            requireContext().cacheDir
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaRecorder()
        removeCachedAudioFile()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    private fun releaseMediaRecorder() {
        try {
            mediaRecorder.stop()
        } catch (e: IllegalStateException) {
            // Happens when mediaRecorder was already stopped no need to handle this exception
        } finally {
            mediaRecorder.release()
        }

    }

    private fun removeCachedAudioFile() {
        if (audioFile.exists()) audioFile.delete()
    }

}