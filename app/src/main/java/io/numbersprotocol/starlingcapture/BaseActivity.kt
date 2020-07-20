package io.numbersprotocol.starlingcapture

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.numbersprotocol.starlingcapture.databinding.ActivityBaseBinding
import kotlinx.android.synthetic.main.activity_base.*
import org.koin.androidx.fragment.android.setupKoinFragmentFactory

class BaseActivity : AppCompatActivity() {

    var layoutFullScreen = false
        set(value) {
            if (value) enableLayoutFullScreen()
            else disableLayoutFullScreen()
            field = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupKoinFragmentFactory()
        ActivityBaseBinding.inflate(layoutInflater).also { binding ->
            binding.lifecycleOwner = this
            setContentView(binding.root)
        }
        layoutFullScreen = true
    }

    private fun enableLayoutFullScreen() {
        rootLayout.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }

    private fun disableLayoutFullScreen() {
        rootLayout.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }
}