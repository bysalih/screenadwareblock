package com.screenadwareblock.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apps")
data class AppEntity(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val threatLevel: Int = 0, // 0=safe, 1=suspicious, 2=dangerous, 3=confirmed_adware
    val hasOverlayPermission: Boolean = false,
    val hasAdminRights: Boolean = false,
    val numberOfAds: Int = 0,
    val batteryUsage: Float = 0f,
    val lastScanDate: Long = System.currentTimeMillis(),
    val isSystemApp: Boolean = false,
    val suspiciousPermissions: String = "", // JSON string of permissions
    val detectionReasons: String = "", // JSON string of detection reasons
    val isWhitelisted: Boolean = false,
    val installDate: Long = 0L,
    val lastUpdateDate: Long = 0L,
    val appSize: Long = 0L,
    val hasNetworkAccess: Boolean = false
)