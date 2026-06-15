package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.ShieldViewModel

@Composable
fun SystemGuardScreen(
    viewModel: ShieldViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDeepNavy)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile identity header
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberDarkBg),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.03f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(CyberBlueSecondary.copy(alpha = 0.15f))
                        .border(1.dp, CyberBlueSecondary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ManageAccounts, contentDescription = null, tint = CyberBlueSecondary)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.currentUser?.username ?: "Anonymous Field Node",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = state.currentUser?.email ?: "baseline_node@shield.ai",
                        color = CyberTextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Button(
                    onClick = { viewModel.performLogout() },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberRedAlert.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("LOGOUT", fontSize = 10.sp, color = CyberRedAlert, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }
        }

        // Active Protection Controls
        Text(
            text = "SYSTEM ACTIVE GUARDS",
            color = CyberAccentCyan,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = CyberDarkBg),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.03f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Toggle 1
                GuardToggleItem(
                    icon = Icons.Default.Language,
                    title = "Safe Browsing Guard",
                    description = "Intercept malicious DNS spoof campaigns.",
                    checked = state.isSafeBrowsingEnabled,
                    onCheckedChange = { viewModel.toggleSafeBrowsing(it) }
                )

                // Divider
                HorizontalDivider(color = Color.White.copy(alpha = 0.04f))

                // Toggle 2
                GuardToggleItem(
                    icon = Icons.Default.Security,
                    title = "Anti-Phishing Shield",
                    description = "Isolate brand-mimic harvesting forms.",
                    checked = state.isPhishingProtectionEnabled,
                    onCheckedChange = { viewModel.togglePhishingProtection(it) }
                )

                // Divider
                HorizontalDivider(color = Color.White.copy(alpha = 0.04f))

                // Toggle 3
                GuardToggleItem(
                    icon = Icons.Default.Sms,
                    title = "SMS Scam Filter",
                    description = "Parse and quarantine text urgent CTAs.",
                    checked = state.isSmsShieldEnabled,
                    onCheckedChange = { viewModel.toggleSmsShield(it) }
                )
            }
        }

        // Simulation Station section
        Text(
            text = "PUSH NOTIFICATION TEST PANEL",
            color = CyberAccentCyan,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = CyberDarkBg),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.03f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Verify proper system alert delivery on this mobile device by firing artificial mock push alerts.",
                    color = CyberTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.triggerMockNotification(
                                title = "🚨 SECURE DNS RE-SHIELDED",
                                content = "Dynamic DNS blacklist signatures updated successfully."
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCardBg),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "SAFE TEST ALERT",
                            fontSize = 10.sp,
                            color = CyberGreenSafe,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.triggerMockNotification(
                                title = "🔥 PHISHING ALERT BLOCKED",
                                content = "Attempted navigation to bit.ly/credit-chase-update arrested."
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCardBg),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "CRITICAL SPAM TEST",
                            fontSize = 10.sp,
                            color = CyberRedAlert,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GuardToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (checked) CyberAccentCyan else CyberTextMuted,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                color = CyberTextSecondary,
                fontSize = 11.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CyberDarkBg,
                checkedTrackColor = CyberAccentCyan,
                uncheckedThumbColor = CyberTextMuted,
                uncheckedTrackColor = CyberCardBg
            )
        )
    }
}
