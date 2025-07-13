package com.screenadwareblock.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.screenadwareblock.AdwareBlockApplication
import com.screenadwareblock.detection.ThreatDetectionEngine

class AppInstallReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "AppInstallReceiver"
    }
    
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        
        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REPLACED -> {
                val packageName = intent.data?.schemeSpecificPart
                Log.d(TAG, "New app installed/updated: $packageName")
                
                if (packageName != null) {
                    scanNewApp(context, packageName)
                }
            }
        }
    }
    
    private fun scanNewApp(context: Context, packageName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val application = context.applicationContext as AdwareBlockApplication
                val repository = application.repository
                val detectionEngine = ThreatDetectionEngine(context)
                
                val packageManager = context.packageManager
                val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
                val signatures = repository.getActiveSignatures()
                
                val appEntity = detectionEngine.analyzeApp(packageInfo, signatures)
                repository.insertApp(appEntity)
                
                Log.d(TAG, "Scanned new app: $packageName, threat level: ${appEntity.threatLevel}")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error scanning new app: $packageName", e)
            }
        }
    }
}