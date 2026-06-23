package com.example.viewmodel

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MainActivity
import com.example.data.api.ShieldApiService
import com.example.data.api.ThreatAnalysisDto
import com.example.data.db.AppDatabase
import com.example.data.model.ScanHistoryEntity
import com.example.data.repository.ScanRepository
import com.example.utils.NetworkHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.UUID

enum class ThreatSeverity {
    SAFE, WARNING, CRITICAL
}

data class CyberThreat(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val url: String?,
    val severity: ThreatSeverity,
    val timestamp: Long = System.currentTimeMillis()
)

data class UserSession(
    val username: String,
    val email: String,
    val riskTolerance: String = "Low"
)

data class ShieldUiState(
    val isAppInitializing: Boolean = true,
    
    // Security controls
    val isSafeBrowsingEnabled: Boolean = true,
    val isPhishingProtectionEnabled: Boolean = true,
    val isSmsShieldEnabled: Boolean = true,
    
    // Threat statistics
    val checkedUrlsCount: Int = 182,
    val scannedSmsCount: Int = 41,
    val blockedThreatsCount: Int = 7,
    val systemRiskScore: Int = 14, // Real-time calculated risk index
    
    // Scan histories and feeds
    val recentScanningHistory: List<CyberThreat> = emptyList(),
    val databaseLogsCount: Int = 0,
    
    // URL scan state
    val currentUrlToScan: String = "",
    val scanResult: CyberThreat? = null,
    val isScanningInProgress: Boolean = false,
    
    // SMS scan state
    val smsSenderId: String = "",
    val smsMessageText: String = "",
    val smsScanResult: CyberThreat? = null,
    val isSmsScanningInProgress: Boolean = false,
    
    // User session
    val currentUser: UserSession? = UserSession("John Doe", "john.doe@shield.ai"),
    val activeAuthScreen: String = "NONE", // "NONE", "LOGIN", "REGISTER", "FORGOT", "RESET"
    val authEmailInput: String = "",
    val authPasswordInput: String = "",
    val authUsernameInput: String = "",
    val authConfirmPasswordInput: String = "",
    
    // Visual tabs and telemetry
    val selectedTab: Int = 0, // 0 = DASHBOARD, 1 = UTILS, 2 = FEED, 3 = PROFILE/GUARD
    val isOnline: Boolean = true,
    val lastScannedQrCode: String? = null,
    
    // Dedicated file upload / dynamic parsing
    val uploadedFileName: String? = null,
    val uploadedFileContent: String? = null,
    
    // Full interactive screen scan dialog simulation
    val isFullScanModalOpen: Boolean = false,
    val fullScanProgress: Float = 0.0f,
    val fullScanActiveFile: String = "",
    val fullScanStatusLabel: String = ""
)

class ShieldViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val networkHelper = NetworkHelper(context)
    
    // Initialize Room Room DB architecture
    private val database = AppDatabase.getDatabase(context)
    private val dao = database.scanHistoryDao()
    private val apiService = ShieldApiService.create()
    private val repository = ScanRepository(dao, apiService)

    private val _uiState = MutableStateFlow(ShieldUiState())
    val uiState: StateFlow<ShieldUiState> = _uiState.asStateFlow()

    init {
        // Collect network changes
        viewModelScope.launch {
            networkHelper.isOnline.collect { online ->
                _uiState.value = _uiState.value.copy(isOnline = online)
            }
        }

        // Live synchronizer mapping local database records to standard CyberThreat entries
        viewModelScope.launch {
            repository.allHistory.collect { entities ->
                if (entities.isEmpty()) {
                    // Prepopulate database with standard default items if empty
                    initializeSampleThreatDatabase()
                } else {
                    val mappedThreats = entities.map { entity ->
                        CyberThreat(
                            id = entity.id.toString(),
                            title = entity.analysisResult,
                            description = "Scanned ${entity.scanType} target: ${entity.target} at ${formatTimestamp(entity.timestamp)}",
                            url = entity.target,
                            severity = when (entity.severity.uppercase()) {
                                "CRITICAL" -> ThreatSeverity.CRITICAL
                                "WARNING" -> ThreatSeverity.WARNING
                                else -> ThreatSeverity.SAFE
                            },
                            timestamp = entity.timestamp
                        )
                    }
                    
                    // Dynamically calculate dynamic risk metrics based on logged threats
                    val criticalCount = entities.count { it.severity.uppercase() == "CRITICAL" }
                    val warningCount = entities.count { it.severity.uppercase() == "WARNING" }
                    val newRiskScore = (10 + (criticalCount * 12) + (warningCount * 4)).coerceIn(5, 95)

                    _uiState.value = _uiState.value.copy(
                        recentScanningHistory = mappedThreats,
                        databaseLogsCount = entities.size,
                        blockedThreatsCount = criticalCount + 3, // mock baseline
                        systemRiskScore = newRiskScore,
                        checkedUrlsCount = 182 + entities.count { it.scanType == "URL" },
                        scannedSmsCount = 41 + entities.count { it.scanType == "SMS" }
                    )
                }
            }
        }

        // Kinetic splash launcher delay
        viewModelScope.launch {
            delay(2800)
            _uiState.value = _uiState.value.copy(isAppInitializing = false)
        }
    }

    private suspend fun initializeSampleThreatDatabase() {
        val samples = listOf(
            Triple("URL", "https://secure-login-chase-update.info", "Verify Chase Bank credential leak"),
            Triple("QR", "https://free-qr-ticket-scam.net/direct-download", "QR targets malicious APK archive"),
            Triple("SMS", "Unusual account activity. Enter code https://verification-chase-id.com", "Phishing SMS spoofing Chase service"),
            Triple("URL", "https://google.com/safe-browsing", "Google Safe Browsing database sync link")
        )
        for (item in samples) {
            val severity = when {
                item.second.contains("chase") || item.second.contains("scam") -> "CRITICAL"
                item.second.contains("verification") -> "WARNING"
                else -> "SAFE"
            }
            repository.insertScan(
                type = item.first,
                target = item.second,
                analysisResult = item.third,
                severity = severity
            )
        }
    }

    private fun formatTimestamp(time: Long): String {
        val diff = System.currentTimeMillis() - time
        return when {
            diff < 60000 -> "just now"
            diff < 3600000 -> "${diff / 60000}m ago"
            else -> "today"
        }
    }

    fun setTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = index)
    }

    // Toggle guard states
    fun toggleSafeBrowsing(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isSafeBrowsingEnabled = enabled)
        val msg = if (enabled) "Shield DNS Web Guard triggered: ONLINE" else "Shield DNS Web Guard: STOPPED"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun togglePhishingProtection(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isPhishingProtectionEnabled = enabled)
    }

    fun toggleSmsShield(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isSmsShieldEnabled = enabled)
    }

    // Auth screen states and actions
    fun setAuthScreen(screen: String) {
        _uiState.value = _uiState.value.copy(activeAuthScreen = screen)
    }

    fun onUsernameInputChanged(value: String) {
        _uiState.value = _uiState.value.copy(authUsernameInput = value)
    }

    fun onEmailInputChanged(value: String) {
        _uiState.value = _uiState.value.copy(authEmailInput = value)
    }

    fun onPasswordInputChanged(value: String) {
        _uiState.value = _uiState.value.copy(authPasswordInput = value)
    }

    fun onConfirmPasswordInputChanged(value: String) {
        _uiState.value = _uiState.value.copy(authConfirmPasswordInput = value)
    }

    fun performLogin() {
        val email = _uiState.value.authEmailInput
        if (email.isBlank() || _uiState.value.authPasswordInput.isBlank()) {
            Toast.makeText(context, "Please populate all authentication fields", Toast.LENGTH_SHORT).show()
            return
        }
        val name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
        _uiState.value = _uiState.value.copy(
            currentUser = UserSession(name, email),
            activeAuthScreen = "NONE",
            authPasswordInput = "",
            authEmailInput = ""
        )
        Toast.makeText(context, "Welcome back, $name! Security console synced.", Toast.LENGTH_LONG).show()
    }

    fun performRegistration() {
        val username = _uiState.value.authUsernameInput
        val email = _uiState.value.authEmailInput
        if (username.isBlank() || email.isBlank() || _uiState.value.authPasswordInput.isBlank()) {
            Toast.makeText(context, "All registration parameters are mandatory", Toast.LENGTH_SHORT).show()
            return
        }
        if (_uiState.value.authPasswordInput != _uiState.value.authConfirmPasswordInput) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }
        _uiState.value = _uiState.value.copy(
            currentUser = UserSession(username, email),
            activeAuthScreen = "NONE",
            authUsernameInput = "",
            authEmailInput = "",
            authPasswordInput = "",
            authConfirmPasswordInput = ""
        )
        Toast.makeText(context, "Shield Account Created Successfully!", Toast.LENGTH_LONG).show()
    }

    fun performLogout() {
        _uiState.value = _uiState.value.copy(currentUser = null)
        Toast.makeText(context, "User logged out. Offline baseline shield active.", Toast.LENGTH_SHORT).show()
    }

    fun performForgotPassword() {
        if (_uiState.value.authEmailInput.isBlank()) {
            Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(context, "Password reset instructions dispatched to ${_uiState.value.authEmailInput}", Toast.LENGTH_LONG).show()
        _uiState.value = _uiState.value.copy(activeAuthScreen = "LOGIN")
    }

    // URL scanner control
    fun onUrlToScanChanged(url: String) {
        _uiState.value = _uiState.value.copy(currentUrlToScan = url, scanResult = null)
    }

    fun scanCustomUrl(urlToScan: String) {
        if (urlToScan.isBlank()) {
            Toast.makeText(context, "Please specify link address to analyze", Toast.LENGTH_SHORT).show()
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isScanningInProgress = true)
            
            // Query Repository layer (API first, fallback to local heuristics)
            val resultDto = repository.analyzeThreat("URL", urlToScan)
            delay(1200) // Aesthetic latency
            
            val threatResult = CyberThreat(
                title = resultDto.title,
                description = resultDto.description,
                url = urlToScan,
                severity = when (resultDto.severity) {
                    "CRITICAL" -> ThreatSeverity.CRITICAL
                    "WARNING" -> ThreatSeverity.WARNING
                    else -> ThreatSeverity.SAFE
                }
            )

            // Persist threat natively in local Room DB for records
            repository.insertScan(
                type = "URL",
                target = urlToScan,
                analysisResult = resultDto.title,
                severity = resultDto.severity
            )

            _uiState.value = _uiState.value.copy(
                isScanningInProgress = false,
                scanResult = threatResult
            )

            if (threatResult.severity == ThreatSeverity.CRITICAL) {
                triggerMockNotification(
                    title = "🚨 SHIELD INTEL: PHISHING DETECTED",
                    content = "Blocked navigation to suspicious link: ${threatResult.url}"
                )
            }
        }
    }

    // SMS scan control
    fun onSmsSenderChanged(id: String) {
        _uiState.value = _uiState.value.copy(smsSenderId = id)
    }

    fun onSmsMessageChanged(text: String) {
        _uiState.value = _uiState.value.copy(smsMessageText = text)
    }

    fun scanSmsMessage() {
        val text = _uiState.value.smsMessageText
        val sender = _uiState.value.smsSenderId.ifBlank { "Unknown Sender" }
        if (text.isBlank()) {
            Toast.makeText(context, "Please key in SMS message text to scan", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSmsScanningInProgress = true)
            
            val resultDto = repository.analyzeThreat("SMS", text, sender)
            delay(1400) // simulated parse scan time
            
            val mappedResult = CyberThreat(
                title = resultDto.title,
                description = resultDto.description,
                url = "SMS from $sender",
                severity = when (resultDto.severity) {
                    "CRITICAL" -> ThreatSeverity.CRITICAL
                    "WARNING" -> ThreatSeverity.WARNING
                    else -> ThreatSeverity.SAFE
                }
            )

            // Save to database
            repository.insertScan(
                type = "SMS",
                target = text,
                senderId = sender,
                analysisResult = resultDto.title,
                severity = resultDto.severity
            )

            _uiState.value = _uiState.value.copy(
                isSmsScanningInProgress = false,
                smsScanResult = mappedResult
            )

            if (mappedResult.severity == ThreatSeverity.CRITICAL) {
                triggerMockNotification(
                    title = "🛡️ SMS SHIELD: BRAND FRAUD INTERCEPTED",
                    content = "Flagged suspicious text message spoofing bank identifiers."
                )
            }
        }
    }

    // Dynamic QR handle
    fun handleScannedQrCode(qrContent: String) {
        _uiState.value = _uiState.value.copy(lastScannedQrCode = qrContent)
        viewModelScope.launch {
            val resultDto = repository.analyzeThreat("QR", qrContent)
            
            // Log to local DB
            repository.insertScan(
                type = "QR",
                target = qrContent,
                analysisResult = resultDto.title,
                severity = resultDto.severity
            )

            triggerMockNotification(
                title = "🛡️ SHIELD AI: QR BARCODE DECODED",
                content = "Evaluated safe routing details for: $qrContent"
            )
        }
    }

    fun clearLastQrScan() {
        _uiState.value = _uiState.value.copy(lastScannedQrCode = null)
    }

    // Room DB modifications from UI (e.g., threat feed screen)
    fun deleteThreatLog(idString: String) {
        viewModelScope.launch {
            idString.toLongOrNull()?.let { entityId ->
                repository.deleteScanById(entityId)
                Toast.makeText(context, "Signature removed from index log", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            repository.clearHistory()
            Toast.makeText(context, "Threat logs cache purged", Toast.LENGTH_SHORT).show()
        }
    }

    // Document txt file upload capability
    fun handleDocumentContentUpload(fileName: String, content: String) {
        _uiState.value = _uiState.value.copy(
            uploadedFileName = fileName,
            uploadedFileContent = content
        )
        Toast.makeText(context, "Uploaded security document: $fileName", Toast.LENGTH_SHORT).show()

        // Extract any URLs found within the txt document to help the user scan them instantly!
        val urlRegex = """(https?://[^\s\s]+)""".toRegex()
        val foundUrls = urlRegex.findAll(content).map { it.value }.toList()
        if (foundUrls.isNotEmpty()) {
            val primary = foundUrls.first()
            _uiState.value = _uiState.value.copy(currentUrlToScan = primary)
            Toast.makeText(context, "Extracted link ${primary.take(24)}... from text payload. Ready to scan!", Toast.LENGTH_LONG).show()
        }
    }

    fun clearUploadedDocument() {
        _uiState.value = _uiState.value.copy(
            uploadedFileName = null,
            uploadedFileContent = null
        )
    }

    fun readTextFileUri(uri: Uri) {
        viewModelScope.launch {
            try {
                val cr = context.contentResolver
                cr.openInputStream(uri)?.use { stream ->
                    val reader = BufferedReader(InputStreamReader(stream))
                    val stringBuilder = StringBuilder()
                    var line: String? = reader.readLine()
                    while (line != null) {
                        stringBuilder.append(line).append("\n")
                        line = reader.readLine()
                    }
                    val fileName = "UploadedText_${System.currentTimeMillis() % 10000}.txt"
                    handleDocumentContentUpload(fileName, stringBuilder.toString())
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to read text file package", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Full interactive screen diagnostics overlay scan dialog simulation
    fun triggerInteractiveFullScan() {
        _uiState.value = _uiState.value.copy(
            isFullScanModalOpen = true,
            fullScanProgress = 0.0f,
            fullScanActiveFile = "Initializing Kernels...",
            fullScanStatusLabel = "Active Scanning Sandbox"
        )

        viewModelScope.launch {
            val filesList = listOf(
                "system/framework/oat/arm64",
                "data/user/0/com.aistudio.shield/shared_prefs",
                "sys/devices/system/cpu/isolated",
                "vendor/etc/security/cacerts",
                "data/app/~~com.google.android.gms",
                "system/priv-app/SettingsProvider",
                "proc/self/net/tcp_active_handshakes",
                "storage/emulated/0/Downloads/invoice_pdf",
                "system/bin/app_process64_vtf",
                "dev/ashmem/sandbox_leak_detector",
                "Completed Intrusion Check"
            )

            for (i in 1..20) {
                delay(120)
                val progress = i / 20.0f
                val activeFile = filesList.getOrElse((i / 2).coerceIn(0, filesList.size - 1)) { "" }
                _uiState.value = _uiState.value.copy(
                    fullScanProgress = progress,
                    fullScanActiveFile = activeFile,
                    fullScanStatusLabel = "Analysing dynamic vulnerabilities... ${ (progress * 100).toInt() }%"
                )
            }

            delay(300)
            _uiState.value = _uiState.value.copy(
                fullScanActiveFile = "Finalizing Threat Matrix Report",
                fullScanStatusLabel = "System Sandbox Score Verified"
            )
            delay(500)
            _uiState.value = _uiState.value.copy(isFullScanModalOpen = false)
            triggerMockNotification(
                title = "🛡️ SentinelAI SYSTEM OK",
                content = "Full physical file system scan finalized. Zero root intrusions flagged."
            )
        }
    }

    fun closeFullScanModal() {
        _uiState.value = _uiState.value.copy(isFullScanModalOpen = false)
    }

    // Real Push / Status simulator notifications
    fun triggerMockNotification(title: String, content: String) {
        val channelId = "shield_ai_alerts"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Shield AI Security Sentinel"
            val descriptionText = "Push notices covering real-time phishing interceptors."
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
        } catch (e: SecurityException) {
            Toast.makeText(context, "System push notification dispatch limits error", Toast.LENGTH_SHORT).show()
        }
    }
}
