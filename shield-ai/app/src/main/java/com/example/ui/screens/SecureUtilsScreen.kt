package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.CyberThreat
import com.example.viewmodel.ShieldViewModel
import com.example.viewmodel.ThreatSeverity

@Composable
fun SecureUtilsScreen(
    viewModel: ShieldViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var selectedToolSubTab by remember { mutableStateOf(0) } // 0 = URL Phishing, 1 = Scam SMS, 2 = QR Guard

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDeepNavy)
    ) {
        // TabRow uses automatically configured indicators
        TabRow(
            selectedTabIndex = selectedToolSubTab,
            containerColor = CyberDarkBg,
            contentColor = CyberAccentCyan
        ) {
            Tab(
                selected = selectedToolSubTab == 0,
                onClick = { selectedToolSubTab = 0 },
                text = { Text("URL SECURE", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedToolSubTab == 1,
                onClick = { selectedToolSubTab = 1 },
                text = { Text("SMS SHIELD", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.QuestionAnswer, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedToolSubTab == 2,
                onClick = { selectedToolSubTab = 2 },
                text = { Text("QR RADAR", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (selectedToolSubTab) {
                0 -> PhishingUrlUtil(viewModel = viewModel)
                1 -> ScamSmsUtil(viewModel = viewModel)
                2 -> CyberScannerScreen(
                    onQrCodeDetected = { viewModel.handleScannedQrCode(it) },
                    lastScannedData = state.lastScannedQrCode,
                    onClearScan = { viewModel.clearLastQrScan() },
                    recentThreats = state.recentScanningHistory
                )
            }
        }
    }
}

@Composable
fun PhishingUrlUtil(viewModel: ShieldViewModel) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val documentFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.readTextFileUri(uri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberDarkBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Troubleshoot,
                        contentDescription = null,
                        tint = CyberAccentCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DEEP PHISHING DECODER",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Verifies web address signatures against live DNS spoof hashes, certificate blacklists, and redirects pattern trees.",
                    color = CyberTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Live Link Input
        OutlinedTextField(
            value = state.currentUrlToScan,
            onValueChange = { viewModel.onUrlToScanChanged(it) },
            placeholder = { Text("https://example-scam-banking.xyz", color = CyberTextMuted, fontSize = 12.sp) },
            label = { Text("Endpoint Web Address", color = CyberTextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = Color.White),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberAccentCyan,
                unfocusedBorderColor = CyberCardBg,
                focusedContainerColor = CyberDarkBg,
                unfocusedContainerColor = CyberDarkBg
            ),
            shape = RoundedCornerShape(12.dp)
        )

        // Native Document file upload section
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberDarkBg),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.04f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.uploadedFileName ?: "No File Attached",
                        color = if (state.uploadedFileName != null) CyberAccentCyan else Color.White,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (state.uploadedFileName != null) "Extracted file content ready" else "Upload txt with links to scan",
                        color = CyberTextSecondary,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                if (state.uploadedFileName != null) {
                    IconButton(onClick = { viewModel.clearUploadedDocument() }) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Clear file", tint = CyberRedAlert)
                    }
                } else {
                    Button(
                        onClick = { documentFileLauncher.launch("text/plain") },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCardBg),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(14.dp), tint = CyberBlueSecondary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("LOAD FILE", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = CyberBlueSecondary)
                    }
                }
            }
        }

        Button(
            onClick = { viewModel.scanCustomUrl(state.currentUrlToScan) },
            colors = ButtonDefaults.buttonColors(containerColor = CyberBluePrimary),
            shape = RoundedCornerShape(12.dp),
            enabled = !state.isScanningInProgress,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            if (state.isScanningInProgress) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("SCANNING SIGNATURES...", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Default.Radar, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("RUN SANDBOX DECRYPTION", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            }
        }

        state.scanResult?.let { threatResult ->
            val strokeColor = when (threatResult.severity) {
                ThreatSeverity.CRITICAL -> CyberRedAlert
                ThreatSeverity.WARNING -> CyberWarning
                ThreatSeverity.SAFE -> CyberGreenSafe
            }

            val containerColorBg = strokeColor.copy(alpha = 0.08f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(containerColorBg)
                    .border(1.dp, strokeColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (threatResult.severity) {
                                ThreatSeverity.CRITICAL -> Icons.Default.GppBad
                                ThreatSeverity.WARNING -> Icons.Default.Warning
                                ThreatSeverity.SAFE -> Icons.Default.GppGood
                            },
                            contentDescription = null,
                            tint = strokeColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = threatResult.title.uppercase(),
                            color = strokeColor,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = threatResult.description,
                        color = Color.White,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Metadata: ${threatResult.url}",
                        color = CyberTextSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ScamSmsUtil(viewModel: ShieldViewModel) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberDarkBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SmsFailed,
                        contentDescription = null,
                        tint = CyberAccentCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NLP MESSAGE SHIELD",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Mitrate spam trigger words, fake emergency alerts, urgent CTA anchors, and unlisted service endpoints in raw SMS inputs.",
                    color = CyberTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = state.smsSenderId,
                onValueChange = { viewModel.onSmsSenderChanged(it) },
                placeholder = { Text("e.g. +1-202-555-0143", color = CyberTextMuted, fontSize = 11.sp) },
                label = { Text("Sender Identification", color = CyberTextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberAccentCyan,
                    unfocusedBorderColor = CyberCardBg,
                    focusedContainerColor = CyberDarkBg,
                    unfocusedContainerColor = CyberDarkBg
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        OutlinedTextField(
            value = state.smsMessageText,
            onValueChange = { viewModel.onSmsMessageChanged(it) },
            placeholder = { Text("Paste suspicious text body here...", color = CyberTextMuted, fontSize = 12.sp) },
            label = { Text("SMS Message Text Payload", color = CyberTextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            textStyle = LocalTextStyle.current.copy(color = Color.White),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberAccentCyan,
                unfocusedBorderColor = CyberCardBg,
                focusedContainerColor = CyberDarkBg,
                unfocusedContainerColor = CyberDarkBg
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Button(
            onClick = { viewModel.scanSmsMessage() },
            colors = ButtonDefaults.buttonColors(containerColor = CyberBluePrimary),
            shape = RoundedCornerShape(12.dp),
            enabled = !state.isSmsScanningInProgress,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            if (state.isSmsScanningInProgress) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("ANALYSING TEXT METRICS...", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Default.Security, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("SCAN SMS PAYLOAD", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            }
        }

        state.smsScanResult?.let { threat ->
            val strokeColor = when (threat.severity) {
                ThreatSeverity.CRITICAL -> CyberRedAlert
                ThreatSeverity.WARNING -> CyberWarning
                ThreatSeverity.SAFE -> CyberGreenSafe
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(strokeColor.copy(alpha = 0.08f))
                    .border(1.dp, strokeColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (threat.severity) {
                                ThreatSeverity.CRITICAL -> Icons.Default.GppBad
                                ThreatSeverity.WARNING -> Icons.Default.Warning
                                ThreatSeverity.SAFE -> Icons.Default.GppGood
                            },
                            contentDescription = null,
                            tint = strokeColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = threat.title.uppercase(),
                            color = strokeColor,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = threat.description,
                        color = Color.White,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
