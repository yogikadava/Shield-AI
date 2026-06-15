package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val scanType: String, // "URL", "QR", "SMS"
    val target: String,
    val senderId: String? = null, // for SMS
    val analysisResult: String,
    val severity: String, // "SAFE", "WARNING", "CRITICAL"
    val timestamp: Long = System.currentTimeMillis()
)
