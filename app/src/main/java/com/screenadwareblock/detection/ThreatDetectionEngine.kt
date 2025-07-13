package com.screenadwareblock.detection

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.provider.Settings
import com.screenadwareblock.database.AppEntity
import com.screenadwareblock.database.ThreatSignature
import org.json.JSONArray
import org.json.JSONObject

class ThreatDetectionEngine(private val context: Context) {
    
    private val packageManager = context.packageManager
    
    // Şüpheli izinler listesi
    private val suspiciousPermissions = setOf(
        "android.permission.SYSTEM_ALERT_WINDOW",
        "android.permission.DEVICE_ADMIN",
        "android.permission.READ_SMS",
        "android.permission.SEND_SMS",
        "android.permission.CALL_PHONE",
        "android.permission.READ_CONTACTS",
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.RECORD_AUDIO",
        "android.permission.CAMERA",
        "android.permission.GET_ACCOUNTS"
    )
    
    // Reklam SDK'ları
    private val adSdkPatterns = listOf(
        "com.google.android.gms.ads",
        "com.facebook.ads",
        "com.unity3d.ads",
        "com.chartboost",
        "com.ironsource",
        "com.applovin"
    )
    
    suspend fun analyzeApp(packageInfo: PackageInfo, signatures: List<ThreatSignature>): AppEntity {
        val appInfo = packageInfo.applicationInfo
        val packageName = packageInfo.packageName
        val appName = appInfo.loadLabel(packageManager).toString()
        
        var threatLevel = 0
        val detectionReasons = mutableListOf<String>()
        val foundPermissions = mutableListOf<String>()
        
        // 1. İmza tabanlı tespit
        val signatureMatch = checkSignatures(packageName, signatures)
        if (signatureMatch != null) {
            threatLevel = maxOf(threatLevel, signatureMatch.severity)
            detectionReasons.add("Bilinen tehdit imzası: ${signatureMatch.description}")
        }
        
        // 2. İzin analizi
        val permissionThreat = analyzePermissions(packageInfo)
        threatLevel = maxOf(threatLevel, permissionThreat.first)
        if (permissionThreat.second.isNotEmpty()) {
            foundPermissions.addAll(permissionThreat.second)
            detectionReasons.add("Şüpheli izinler tespit edildi")
        }
        
        // 3. Paket ismi analizi
        val nameThreat = analyzePackageName(packageName)
        threatLevel = maxOf(threatLevel, nameThreat.first)
        if (nameThreat.second.isNotEmpty()) {
            detectionReasons.add(nameThreat.second)
        }
        
        // 4. Overlay izni kontrolü
        val hasOverlayPermission = Settings.canDrawOverlays(context)
        if (hasOverlayPermission && hasOverlayPermission(packageInfo)) {
            threatLevel = maxOf(threatLevel, 2)
            detectionReasons.add("Ekran üzerinde çizim izni")
        }
        
        // 5. Sistem uygulaması kontrolü
        val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        
        return AppEntity(
            packageName = packageName,
            appName = appName,
            threatLevel = threatLevel,
            hasOverlayPermission = hasOverlayPermission && hasOverlayPermission(packageInfo),
            hasAdminRights = hasDeviceAdminPermission(packageInfo),
            lastScanDate = System.currentTimeMillis(),
            isSystemApp = isSystemApp,
            suspiciousPermissions = JSONArray(foundPermissions).toString(),
            detectionReasons = JSONArray(detectionReasons).toString(),
            installDate = packageInfo.firstInstallTime,
            lastUpdateDate = packageInfo.lastUpdateTime,
            hasNetworkAccess = hasInternetPermission(packageInfo)
        )
    }
    
    private fun checkSignatures(packageName: String, signatures: List<ThreatSignature>): ThreatSignature? {
        // Tam paket ismi eşleşmesi
        signatures.find { it.packageName == packageName }?.let { return it }
        
        // Pattern eşleşmesi
        signatures.find { signature ->
            signature.packagePattern?.let { pattern ->
                try {
                    packageName.matches(Regex(pattern))
                } catch (e: Exception) {
                    false
                }
            } ?: false
        }?.let { return it }
        
        return null
    }
    
    private fun analyzePermissions(packageInfo: PackageInfo): Pair<Int, List<String>> {
        val permissions = packageInfo.requestedPermissions ?: return Pair(0, emptyList())
        val foundSuspicious = permissions.filter { it in suspiciousPermissions }
        
        val threatLevel = when {
            foundSuspicious.size >= 5 -> 3 // Çok sayıda şüpheli izin
            foundSuspicious.size >= 3 -> 2 // Orta seviye tehdit
            foundSuspicious.size >= 1 -> 1 // Düşük seviye tehdit
            else -> 0
        }
        
        return Pair(threatLevel, foundSuspicious)
    }
    
    private fun analyzePackageName(packageName: String): Pair<Int, String> {
        val lowerPackage = packageName.lowercase()
        
        return when {
            lowerPackage.contains("fake") -> Pair(3, "Sahte uygulama olabilir")
            lowerPackage.contains("ads") -> Pair(2, "Reklam içeren uygulama")
            lowerPackage.contains("cleaner") -> Pair(2, "Temizleyici uygulaması (genellikle gereksiz)")
            lowerPackage.contains("booster") -> Pair(2, "Güçlendirici uygulaması (genellikle etkisiz)")
            lowerPackage.contains("optimizer") -> Pair(1, "Optimizasyon uygulaması")
            lowerPackage.contains("speed") -> Pair(1, "Hız artırıcı uygulaması")
            else -> Pair(0, "")
        }
    }
    
    private fun hasOverlayPermission(packageInfo: PackageInfo): Boolean {
        return packageInfo.requestedPermissions?.contains("android.permission.SYSTEM_ALERT_WINDOW") == true
    }
    
    private fun hasDeviceAdminPermission(packageInfo: PackageInfo): Boolean {
        return packageInfo.requestedPermissions?.contains("android.permission.DEVICE_ADMIN") == true
    }
    
    private fun hasInternetPermission(packageInfo: PackageInfo): Boolean {
        return packageInfo.requestedPermissions?.contains("android.permission.INTERNET") == true
    }
}