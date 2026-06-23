@file:OptIn(com.google.accompanist.permissions.ExperimentalPermissionsApi::class)

package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.os.Vibrator
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ui.theme.*
import com.example.viewmodel.CyberThreat
import com.google.accompanist.permissions.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CyberScannerScreen(
    onQrCodeDetected: (String) -> Unit,
    lastScannedData: String?,
    onClearScan: () -> Unit,
    recentThreats: List<CyberThreat>,
    modifier: Modifier = Modifier
) {
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDeepNavy)
    ) {
        if (cameraPermissionState.status.isGranted) {
            CameraPortal(
                onQrCodeDetected = onQrCodeDetected,
                lastScannedData = lastScannedData,
                onClearScan = onClearScan,
                recentThreats = recentThreats
            )
        } else {
            RequestPermissionPortal(
                permissionState = cameraPermissionState
            )
        }
    }
}

@Composable
fun CameraPortal(
    onQrCodeDetected: (String) -> Unit,
    lastScannedData: String?,
    onClearScan: () -> Unit,
    recentThreats: List<CyberThreat>
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var isFlashOn by remember { mutableStateOf(false) }
    var cameraControlInstance by remember { mutableStateOf<CameraControl?>(null) }
    var isSimulatingScan by remember { mutableStateOf(false) }

    // Pulsing scanning reticle laser path
    val infiniteTransition = rememberInfiniteTransition(label = "LaserGlow")
    val laserOffsetRatio by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LaserOffset"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Upper telemetry info card
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberDarkBg),
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "REAL-TIME SCANNING MATRIX",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Target QR codes or visual URLs to test safety.",
                        color = CyberTextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Flash toggle
                IconButton(
                    onClick = {
                        isFlashOn = !isFlashOn
                        cameraControlInstance?.enableTorch(isFlashOn)
                    },
                    modifier = Modifier
                        .background(CyberCardBg, RoundedCornerShape(8.dp))
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flash Toggle",
                        tint = if (isFlashOn) CyberAccentCyan else Color.White
                    )
                }
            }
        }

        // Camera Feed container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.3f)
                .background(Color.Black)
        ) {
            // Live CameraX view
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        try {
                            cameraProvider.unbindAll()
                            val camera = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview
                            )
                            cameraControlInstance = camera.cameraControl
                        } catch (exc: Exception) {
                            Toast.makeText(ctx, "Failed to start camera preview", Toast.LENGTH_SHORT).show()
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                }
            )

            // Tech HUD grid overlay
            Canvas(modifier = Modifier.fillMaxSize()) {
                val boxWidth = size.width * 0.65f
                val boxHeight = size.width * 0.65f
                val left = (size.width - boxWidth) / 2
                val top = (size.height - boxHeight) / 2

                val strokeWidth = 3.dp.toPx()
                val cornerLength = 24.dp.toPx()

                // Corner bracket: Top-Left
                drawLine(
                    color = CyberAccentCyan,
                    start = Offset(left, top),
                    end = Offset(left + cornerLength, top),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = CyberAccentCyan,
                    start = Offset(left, top),
                    end = Offset(left, top + cornerLength),
                    strokeWidth = strokeWidth
                )

                // Corner bracket: Top-Right
                drawLine(
                    color = CyberAccentCyan,
                    start = Offset(left + boxWidth, top),
                    end = Offset(left + boxWidth - cornerLength, top),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = CyberAccentCyan,
                    start = Offset(left + boxWidth, top),
                    end = Offset(left + boxWidth, top + cornerLength),
                    strokeWidth = strokeWidth
                )

                // Corner bracket: Bottom-Left
                drawLine(
                    color = CyberAccentCyan,
                    start = Offset(left, top + boxHeight),
                    end = Offset(left + cornerLength, top + boxHeight),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = CyberAccentCyan,
                    start = Offset(left, top + boxHeight),
                    end = Offset(left, top + boxHeight - cornerLength),
                    strokeWidth = strokeWidth
                )

                // Corner bracket: Bottom-Right
                drawLine(
                    color = CyberAccentCyan,
                    start = Offset(left + boxWidth, top + boxHeight),
                    end = Offset(left + boxWidth - cornerLength, top + boxHeight),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = CyberAccentCyan,
                    start = Offset(left + boxWidth, top + boxHeight),
                    end = Offset(left + boxWidth, top + boxHeight - cornerLength),
                    strokeWidth = strokeWidth
                )

                // Moving laser horizontal line
                val currentLaserY = top + (boxHeight * laserOffsetRatio)
                drawLine(
                    color = CyberRedAlert,
                    start = Offset(left, currentLaserY),
                    end = Offset(left + boxWidth, currentLaserY),
                    strokeWidth = 2.dp.toPx()
                )
            }

            // Quick Interactive simulated scan trigger
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isSimulatingScan) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberDarkBg.copy(alpha = 0.9f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            CircularProgressIndicator(
                                color = CyberAccentCyan,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "DECRYPTING THREAT VECTORS...",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isSimulatingScan = true
                                    delay(1600)
                                    isSimulatingScan = false
                                    // Trigger nice simulated feedback scan targets
                                    val scenarios = listOf(
                                        "https://chase-update-credential.phish-guard.com/verify",
                                        "https://giftcard-promotions-scam.net/redeem",
                                        "https://calm-sentinel-shield-ai.base44.app/dashboard",
                                        "https://google.com/safe-browsing"
                                    )
                                    val selected = scenarios.random()
                                    
                                    // Trigger quick rumble tactile feedback
                                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
                                    vibrator?.vibrate(100)

                                    onQrCodeDetected(selected)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberBluePrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SIMULATE PHISH SCAN",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Live Scanning Logs Feed (Below camera)
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberDeepNavy),
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "LATEST INSPECTION LOGS",
                    color = CyberTextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (lastScannedData != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberCardBg),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.DoneOutline,
                                        contentDescription = null,
                                        tint = CyberAccentCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "PROCESSED TARGET",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                TextButton(
                                    onClick = onClearScan,
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.height(24.dp)
                                ) {
                                    Text(
                                        text = "CLEAR SCAN",
                                        color = CyberRedAlert,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = lastScannedData,
                                color = CyberTextPrimary,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1,
                                modifier = Modifier.background(CyberDarkBg, RoundedCornerShape(4.dp)).padding(6.dp).fillMaxWidth()
                            )
                        }
                    }
                }

                // Scrollable Live Feed list of Threat DB logs
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recentThreats.take(3)) { threat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberCardBg, RoundedCornerShape(6.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = threat.title,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = threat.url ?: "General diagnostics check",
                                    color = CyberTextSecondary,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(
                                        color = when (threat.severity) {
                                            com.example.viewmodel.ThreatSeverity.CRITICAL -> CyberRedAlert.copy(alpha = 0.2f)
                                            com.example.viewmodel.ThreatSeverity.WARNING -> CyberWarning.copy(alpha = 0.2f)
                                            com.example.viewmodel.ThreatSeverity.SAFE -> CyberGreenSafe.copy(alpha = 0.2f)
                                        },
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = threat.severity.name,
                                    color = when (threat.severity) {
                                        com.example.viewmodel.ThreatSeverity.CRITICAL -> CyberRedAlert
                                        com.example.viewmodel.ThreatSeverity.WARNING -> CyberWarning
                                        com.example.viewmodel.ThreatSeverity.SAFE -> CyberGreenSafe
                                    },
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RequestPermissionPortal(
    permissionState: PermissionState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = "Camera Permission Required",
            tint = CyberAccentCyan,
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "CAMERA ACCESS PERMISSION",
            color = Color.White,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Shield AI uses the camera hardware to instantly scan safety barcodes, verify suspicious URL envelopes, and prevent visual fraudulent redirects. Tap below to enable secure device scanning.",
            color = CyberTextSecondary,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(0.85f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { permissionState.launchPermissionRequest() },
            colors = ButtonDefaults.buttonColors(containerColor = CyberBluePrimary),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Security, contentDescription = "Authorize")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "AUTHORIZE SHIELD DECODER",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
