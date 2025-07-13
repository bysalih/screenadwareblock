package com.screenadwareblock.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "threat_signatures")
data class ThreatSignature(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val packageName: String? = null, // Specific package name (if known)
    val packagePattern: String? = null, // Regex pattern for package names
    val threatType: String, // "adware", "malware", "spyware", etc.
    val severity: Int, // 1-5 severity level
    val description: String,
    val detectionMethod: String, // "package_name", "permission", "behavior", etc.
    val isActive: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis()
)