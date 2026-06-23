package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import com.example.viewmodel.CyberThreat
import com.example.viewmodel.ShieldViewModel
import com.example.viewmodel.ThreatSeverity

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ThreatHistoryScreen(
    viewModel: ShieldViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var searchKeyword by remember { mutableStateOf("") }
    var severityFilter by remember { mutableStateOf<ThreatSeverity?>(null) } // null = show all
    var selectedThreatDetail by remember { mutableStateOf<CyberThreat?>(null) }

    val filteredHistory = remember(state.recentScanningHistory, searchKeyword, severityFilter) {
        state.recentScanningHistory.filter { threat ->
            val matchesSearch = threat.title.contains(searchKeyword, ignoreCase = true) ||
                    (threat.url?.contains(searchKeyword, ignoreCase = true) ?: false)
            val matchesSeverity = severityFilter == null || threat.severity == severityFilter
            matchesSearch && matchesSeverity
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDeepNavy)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SECURE ROOM LOGS INDEX",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Real-time threat registries logged natively",
                        color = CyberTextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                IconButton(
                    onClick = { viewModel.clearAllLogs() },
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberRedAlert.copy(alpha = 0.15f))
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Purge Cache",
                        tint = CyberRedAlert
                    )
                }
            }

            // Search Bar Input
            OutlinedTextField(
                value = searchKeyword,
                onValueChange = { searchKeyword = it },
                placeholder = { Text("Search logs (e.g. chase, sms, net)", color = CyberTextMuted, fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CyberAccentCyan) },
                trailingIcon = {
                    if (searchKeyword.isNotEmpty()) {
                        IconButton(onClick = { searchKeyword = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search", tint = CyberTextSecondary)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
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

            // Filtering Chips
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // All Chip
                FilterChip(
                    selected = severityFilter == null,
                    onClick = { severityFilter = null },
                    label = { Text("ALL SIGNATURES", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = CyberDarkBg,
                        labelColor = CyberTextSecondary,
                        selectedContainerColor = CyberAccentCyan,
                        selectedLabelColor = CyberDarkBg
                    )
                )

                // Critical Chip
                FilterChip(
                    selected = severityFilter == ThreatSeverity.CRITICAL,
                    onClick = { severityFilter = ThreatSeverity.CRITICAL },
                    label = { Text("CRITICAL", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = CyberDarkBg,
                        labelColor = CyberTextSecondary,
                        selectedContainerColor = CyberRedAlert,
                        selectedLabelColor = Color.White
                    )
                )

                // Warning Chip
                FilterChip(
                    selected = severityFilter == ThreatSeverity.WARNING,
                    onClick = { severityFilter = ThreatSeverity.WARNING },
                    label = { Text("WARNING", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = CyberDarkBg,
                        labelColor = CyberTextSecondary,
                        selectedContainerColor = CyberWarning,
                        selectedLabelColor = CyberDarkBg
                    )
                )

                // Safe Chip
                FilterChip(
                    selected = severityFilter == ThreatSeverity.SAFE,
                    onClick = { severityFilter = ThreatSeverity.SAFE },
                    label = { Text("SAFE", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = CyberDarkBg,
                        labelColor = CyberTextSecondary,
                        selectedContainerColor = CyberGreenSafe,
                        selectedLabelColor = Color.White
                    )
                )
            }

            // Database scrollable list
            if (filteredHistory.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CyberDarkBg)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = CyberTextMuted,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "NO THREAT SIGNATURES MATCHED",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Try loosening dynamic search parameters",
                            color = CyberTextSecondary,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(filteredHistory, key = { it.id }) { threat ->
                        CyberHistoryRow(
                            threat = threat,
                            onDelete = { viewModel.deleteThreatLog(threat.id) },
                            onSelect = { selectedThreatDetail = threat }
                        )
                    }
                }
            }
        }

        // Expanded Threat Intelligence Dialogue Modal
        selectedThreatDetail?.let { activeDetail ->
            ThreatDetailDialog(
                threat = activeDetail,
                onDismiss = { selectedThreatDetail = null }
            )
        }
    }
}

@Composable
fun CyberHistoryRow(
    threat: CyberThreat,
    onDelete: () -> Unit,
    onSelect: () -> Unit
) {
    val statusColor = when (threat.severity) {
        ThreatSeverity.CRITICAL -> CyberRedAlert
        ThreatSeverity.WARNING -> CyberWarning
        ThreatSeverity.SAFE -> CyberGreenSafe
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .testTag("task_item_card"),
        colors = CardDefaults.cardColors(containerColor = CyberDarkBg),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.03f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (threat.severity) {
                        ThreatSeverity.CRITICAL -> Icons.Default.GppBad
                        ThreatSeverity.WARNING -> Icons.Default.Warning
                        ThreatSeverity.SAFE -> Icons.Default.GppGood
                    },
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = threat.title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = threat.url ?: "Database Diagnostics Check",
                    color = CyberTextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove Signature",
                    tint = CyberTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun ThreatDetailDialog(
    threat: CyberThreat,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        val detailColor = when (threat.severity) {
            ThreatSeverity.CRITICAL -> CyberRedAlert
            ThreatSeverity.WARNING -> CyberWarning
            ThreatSeverity.SAFE -> CyberGreenSafe
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = CyberDarkBg),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            border = BorderStroke(1.dp, detailColor.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = when (threat.severity) {
                        ThreatSeverity.CRITICAL -> Icons.Default.GppBad
                        ThreatSeverity.WARNING -> Icons.Default.Warning
                        ThreatSeverity.SAFE -> Icons.Default.GppGood
                    },
                    contentDescription = null,
                    tint = detailColor,
                    modifier = Modifier.size(56.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "THREAT ANALYSIS MATCH REPORT",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = detailColor,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = threat.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Audit Target / Data",
                    fontSize = 10.sp,
                    color = CyberTextSecondary,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Text(
                    text = threat.url ?: "Diagnostics Scan Event",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberDeepNavy)
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Security Engineers Verdict",
                    fontSize = 10.sp,
                    color = CyberTextSecondary,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Text(
                    text = threat.description,
                    color = CyberTextPrimary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = detailColor),
                    shape = RoundedCornerShape(100.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "CLOSE VERDICT REPORT",
                        fontFamily = FontFamily.Monospace,
                        color = if (threat.severity == ThreatSeverity.WARNING) CyberDarkBg else Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
