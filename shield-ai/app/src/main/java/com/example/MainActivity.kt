package com.example

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.ValueCallback
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.MainScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ShieldViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ShieldViewModel by viewModels()

    // File upload callback reference for WebView uploads
    private var fileUploadCallback: ValueCallback<Array<Uri>>? = null

    // Register active file picker intent launcher
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val uris = if (data != null) {
                val clipData = data.clipData
                if (clipData != null) {
                    val list = mutableListOf<Uri>()
                    for (i in 0 until clipData.itemCount) {
                        list.add(clipData.getItemAt(i).uri)
                    }
                    list.toTypedArray()
                } else {
                    val singleUri = data.data
                    if (singleUri != null) arrayOf(singleUri) else null
                }
            } else {
                null
            }
            fileUploadCallback?.onReceiveValue(uris)
        } else {
            // Cancel callback to prevent WebView from freezing
            fileUploadCallback?.onReceiveValue(null)
        }
        fileUploadCallback = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = androidx.activity.SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = androidx.activity.SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )

        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiState.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = com.example.ui.theme.CyberDeepNavy
                ) {
                    if (uiState.isAppInitializing) {
                        SplashScreen()
                    } else {
                        MainScreen(
                            viewModel = viewModel,
                            onShowFileChooser = { callback ->
                                triggerFileSelection(callback)
                            }
                        )
                    }
                }
            }
        }

        // Parse initial intent if opened via deep link
        handleDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val uri = intent?.data
        if (uri != null) {
            val linkString = uri.toString()
            viewModel.setTab(2) // Jump to Ops Center panel immediately
            viewModel.onUrlToScanChanged(linkString)
            viewModel.scanCustomUrl(linkString)
            Toast.makeText(this, "Shield AI Intercepted Dangerous Threat", Toast.LENGTH_LONG).show()
        }
    }

    private fun triggerFileSelection(callback: ValueCallback<Array<Uri>>?) {
        fileUploadCallback = callback
        val chooseFileIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*" // Allow all file choosing formats inside portal
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        filePickerLauncher.launch(Intent.createChooser(chooseFileIntent, "Upload Attachment"))
    }
}
