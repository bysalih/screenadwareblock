
package com.screenadwareblock

data class AppInfo(
    val appName: String,
    val packageName: String,
    val hasOverlayPermission: Boolean,
    val isRunningService: Boolean,
    val isInVirusDB: Boolean
)
