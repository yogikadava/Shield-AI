package com.example.ui.screens

import android.net.Uri
import android.webkit.ValueCallback
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.ShieldViewModel

@Composable
fun MainScreen(
    viewModel: ShieldViewModel,
    onShowFileChooser: (ValueCallback<Array<Uri>>?) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // 1. Force Agent Authentication if session is empty
    if (uiState.currentUser == null) {
        AuthOverlayPortal(
            viewModel = viewModel,
            activeScreen = if (uiState.activeAuthScreen == "NONE") "LOGIN" else uiState.activeAuthScreen,
            modifier = modifier
        )
    } else if (uiState.activeAuthScreen != "NONE") {
        AuthOverlayPortal(
            viewModel = viewModel,
            activeScreen = uiState.activeAuthScreen,
            modifier = modifier
        )
    } else {
        // 2. Render Main 100% Native Security Suite
        Scaffold(
            modifier = modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    containerColor = CyberDarkBg,
                    tonalElevation = 10.dp
                ) {
                    // TAB 0: DASHBOARD
                    NavigationBarItem(
                        selected = uiState.selectedTab == 0,
                        onClick = { viewModel.setTab(0) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.selectedTab == 0) Icons.Filled.Language else Icons.Outlined.Language,
                                contentDescription = "Dashboard Hub"
                            )
                        },
                        label = {
                            Text(
                                text = "DASHBOARD",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CyberAccentCyan,
                            selectedTextColor = CyberAccentCyan,
                            indicatorColor = CyberCardBg,
                            unselectedIconColor = CyberTextSecondary,
                            unselectedTextColor = CyberTextSecondary
                        )
                    )

                    // TAB 1: UTILS (URL Phishing, Messages Scam, QR scanner)
                    NavigationBarItem(
                        selected = uiState.selectedTab == 1,
                        onClick = { viewModel.setTab(1) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.selectedTab == 1) Icons.Filled.FactCheck else Icons.Outlined.FactCheck,
                                contentDescription = "Core Utilities"
                            )
                        },
                        label = {
                            Text(
                                text = "DECODER",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CyberAccentCyan,
                            selectedTextColor = CyberAccentCyan,
                            indicatorColor = CyberCardBg,
                            unselectedIconColor = CyberTextSecondary,
                            unselectedTextColor = CyberTextSecondary
                        )
                    )

                    // TAB 2: HISTORY (ROOM DB LOGS)
                    NavigationBarItem(
                        selected = uiState.selectedTab == 2,
                        onClick = { viewModel.setTab(2) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.selectedTab == 2) Icons.Filled.FolderZip else Icons.Outlined.FolderZip,
                                contentDescription = "Database Logs"
                            )
                        },
                        label = {
                            Text(
                                text = "THREAT LOGS",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CyberAccentCyan,
                            selectedTextColor = CyberAccentCyan,
                            indicatorColor = CyberCardBg,
                            unselectedIconColor = CyberTextSecondary,
                            unselectedTextColor = CyberTextSecondary
                        )
                    )

                    // TAB 3: PROFILE / SYSTEM GUARDS
                    NavigationBarItem(
                        selected = uiState.selectedTab == 3,
                        onClick = { viewModel.setTab(3) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.selectedTab == 3) Icons.Filled.GppGood else Icons.Outlined.GppGood,
                                contentDescription = "System Guards Configuration"
                            )
                        },
                        label = {
                            Text(
                                text = "PROFILE",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CyberAccentCyan,
                            selectedTextColor = CyberAccentCyan,
                            indicatorColor = CyberCardBg,
                            unselectedIconColor = CyberTextSecondary,
                            unselectedTextColor = CyberTextSecondary
                        )
                    )
                }
            },
            contentWindowInsets = WindowInsets.systemBars
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (uiState.selectedTab) {
                    0 -> DashboardScreen(viewModel = viewModel)
                    1 -> SecureUtilsScreen(viewModel = viewModel)
                    2 -> ThreatHistoryScreen(viewModel = viewModel)
                    3 -> SystemGuardScreen(viewModel = viewModel)
                }
            }
        }
    }
}
