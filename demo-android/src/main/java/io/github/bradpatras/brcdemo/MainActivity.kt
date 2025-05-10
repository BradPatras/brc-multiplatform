package io.github.bradpatras.brcdemo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.bradpatras.basicremoteconfigs.BasicRemoteConfigs
import io.github.bradpatras.brcdemo.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val CONFIG_URL = "https://github.com/BradPatras/brc-multiplatform/raw/main/simple-config.json"

class MainActivity : AppCompatActivity() {
    private val brc: BasicRemoteConfigs = BasicRemoteConfigs(remoteUrl = CONFIG_URL)
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fetchBtn.setOnClickListener {
            updateUI(null, true)
            lifecycleScope.launch {
                getConfigs()
            }
        }

        binding.clearBtn.setOnClickListener {
            updateUI(null, false)
            brc.clearCache()
        }
    }

    private suspend fun getConfigs() = coroutineScope {
        try {
            brc.fetchConfigs()
            withContext(Dispatchers.Main) {
                updateUI(
                    brc.getKeys().joinToString(separator = ",\n"),
                    false
                )
            }
        } catch (error: Throwable) {
            withContext(Dispatchers.Main) {
                updateUI(
                    "Encountered an error when fetching configs",
                    false
                )
            }
        }
    }

    private fun updateUI(newText: String?, isLoading: Boolean) {
        binding.centerTv.text = newText
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }
}