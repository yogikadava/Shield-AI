package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import com.example.viewmodel.CyberThreat
import com.example.viewmodel.ShieldViewModel
import com.example.viewmodel.ThreatSeverity

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    viewModel: ShieldViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDeepNavy)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Header Agent identity bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "SENTINEL LOCAL DEPLOYMENT",
                        color = CyberAccentCyan,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = state.currentUser?.username?.let { "Welcome, agent $it" } ?: "Anonymous Terminal mode",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Hexagonal User Node avatar badge
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CyberCardBg)
                        .border(1.dp, CyberBlueSecondary.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = state.currentUser?.username?.take(2)?.uppercase() ?: "??"
                    Text(
                        text = initials,
                        color = CyberBlueSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Radar Circle Gauge
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                colors = CardDefaults.cardColors(containerColor = CyberDarkBg),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, CyberBlueSecondary.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "INTELLIGENCE MATRIX RISK FEEDBACK",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = CyberTextSecondary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Circular risk meter
                    Box(
                        modifier = Modifier.size(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Soft nested blur shadows drawn natively
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Outer ring
                            drawCircle(
                                color = CyberCardBg,
                                radius = size.minDimension / 2.05f,
                                style = Stroke(width = 1.dp.toPx())
                            )
                            // Core track
                            drawArc(
                                color = Color.White.copy(alpha = 0.05f),
                                startAngle = -220f,
                                sweepAngle = 260f,
                                useCenter = false,
                                topLeft = Offset(size.width * 0.1f, size.height * 0.1f),
                                size = Size(size.width * 0.8f, size.height * 0.8f),
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                            
                            // High contrast glowing sweep color determine by risk index
                            val sweepAngle = (state.systemRiskScore / 100f) * 260f
                            val sweepColor = when {
                                state.systemRiskScore > 40 -> CyberRedAlert
                                state.systemRiskScore > 20 -> CyberWarning
                                else -> CyberGreenSafe
                            }
                            drawArc(
                                color = sweepColor,
                                startAngle = -220f,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                topLeft = Offset(size.width * 0.1f, size.height * 0.1f),
                                size = Size(size.width * 0.8f, size.height * 0.8f),
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        // Center risk percentage info labels
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${state.systemRiskScore}%",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = Color.White
                            )
                            Text(
                                text = when {
                                    state.systemRiskScore > 40 -> "HOST COMPROMISED"
                                    state.systemRiskScore > 20 -> "ELEVATED CONCERN"
                                    else -> "HOST SECURED"
                                },
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.ExtraBold,
                                color = when {
                                    state.systemRiskScore > 40 -> CyberRedAlert
                                    state.systemRiskScore > 20 -> CyberWarning
                                    else -> CyberGreenSafe
                                },
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "AI Sentinel dynamically isolates digital threats",
                        fontSize = 11.sp,
                        color = CyberTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.triggerInteractiveFullScan() },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberBlueSecondary),
                        shape = RoundedCornerShape(100.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(44.dp)
                            .testTag("submit_button")
                    ) {
                        Icon(Icons.Default.QueryStats, contentDescription = null, tint = CyberDarkBg)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "START DEEP INTEL SCAN",
                            color = CyberDarkBg,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Quick stats grids mimicking the website's stats
            Text(
                text = "TELEMETRY OVERVIEW",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = CyberAccentCyan,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                maxItemsInEachRow = 2
            ) {
                // Stat 1
                StatsCard(
                    icon = Icons.Default.Language,
                    title = "URLs Audited",
                    value = "${state.checkedUrlsCount}",
                    color = CyberBlueSecondary,
                    modifier = Modifier.weight(1f)
                )
                // Stat 2
                StatsCard(
                    icon = Icons.Default.Sms,
                    title = "Scams Filtered",
                    value = "${state.scannedSmsCount}",
                    color = CyberAccentCyan,
                    modifier = Modifier.weight(1f)
                )
                // Stat 3
                StatsCard(
                    icon = Icons.Default.GppBad,
                    title = "Malicious Blocked",
                    value = "${state.blockedThreatsCount}",
                    color = CyberRedAlert,
                    modifier = Modifier.weight(1f)
                )
                // Stat 4
                StatsCard(
                    icon = Icons.Default.CompassCalibration,
                    title = "Base Core Guard",
                    value = if (state.isSafeBrowsingEnabled) "OPTIMAL" else "PAUSED",
                    color = CyberGreenSafe,
                    modifier = Modifier.weight(1f)
                )
            }

            // Recent threats logs Feed
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACTIVE DEFENSIVE INTELLIGENCE FEED",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Sync Count: ${state.databaseLogsCount}",
                    color = CyberAccentCyan,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Dynamic listed feed items
            if (state.recentScanningHistory.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberDarkBg, RoundedCornerShape(12.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = CyberTextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Zero warning signatures. System fully clear.",
                            color = CyberTextSecondary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    state.recentScanningHistory.take(4).forEach { threat ->
                        ThreatDashboardRow(threat = threat)
                    }
                }
            }
        }

        // Kinetic deep scan fullscreen simulation modal overlay
        AnimatedVisibility(
            visible = state.isFullScanModalOpen,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Dialog(
                onDismissRequest = { viewModel.closeFullScanModal() },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CyberDarkBg.copy(alpha = 0.96f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        // Cyber matrix glowing hub
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(120.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { state.fullScanProgress },
                                color = CyberAccentCyan,
                                strokeWidth = 5.dp,
                                modifier = Modifier.size(110.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.Radar,
                                contentDescription = null,
                                tint = CyberAccentCyan,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "SANDBOX COMPILATION TESTER",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = state.fullScanStatusLabel,
                            color = CyberAccentCyan,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Currently Auditing Path:",
                            color = CyberTextSecondary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Text(
                            text = state.fullScanActiveFile,
                            color = CyberTextPrimary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberCardBg)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatsCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CyberDarkBg),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.04f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color.White
            )

            Text(
                text = title,
                fontSize = 10.sp,
                color = CyberTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ThreatDashboardRow(threat: CyberThreat) {
    val highlightColor = when (threat.severity) {
        ThreatSeverity.CRITICAL -> CyberRedAlert
        ThreatSeverity.WARNING -> CyberWarning
        ThreatSeverity.SAFE -> CyberGreenSafe
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberDarkBg, RoundedCornerShape(12.dp))
            .border(0.5.dp, Color.White.copy(alpha = 0.03f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(highlightColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (threat.severity) {
                    ThreatSeverity.CRITICAL -> Icons.Default.GppBad
                    ThreatSeverity.WARNING -> Icons.Default.Warning
                    else -> Icons.Default.GppGood
                },
                contentDescription = null,
                tint = highlightColor,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = threat.title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = threat.description,
                color = CyberTextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(highlightColor)
        )
    }
}
