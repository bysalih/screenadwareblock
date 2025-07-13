package com.screenadwareblock.viewmodel

import android.app.Application
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.screenadwareblock.AdwareBlockApplication
import com.screenadwareblock.database.AppEntity
import com.screenadwareblock.detection.ThreatDetectionEngine
import com.screenadwareblock.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: AppRepository = (application as AdwareBlockApplication).repository
    private val detectionEngine = ThreatDetectionEngine(application)
    
    val allApps: LiveData<List<AppEntity>> = repository.getAllApps()
    val threatApps: LiveData<List<AppEntity>> = repository.getThreatApps()
    val threatCount: LiveData<Int> = repository.getThreatCount()
    
    private val _isScanning = MutableLiveData<Boolean>()
    val isScanning: LiveData<Boolean> = _isScanning
    
    private val _scanProgress = MutableLiveData<Int>()
    val scanProgress: LiveData<Int> = _scanProgress
    
    private val _lastScanTime = MutableLiveData<Long>()
    val lastScanTime: LiveData<Long> = _lastScanTime
    
    fun scanAllApps() {
        viewModelScope.launch(Dispatchers.IO) {
            _isScanning.postValue(true)
            _scanProgress.postValue(0)
            
            try {
                val packageManager = getApplication<Application>().packageManager
                val packages = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
                val signatures = repository.getActiveSignatures()
                
                val totalApps = packages.size
                val scannedApps = mutableListOf<AppEntity>()
                
                packages.forEachIndexed { index, packageInfo ->
                    try {
                        val appEntity = detectionEngine.analyzeApp(packageInfo, signatures)
                        scannedApps.add(appEntity)
                        
                        val progress = ((index + 1) * 100) / totalApps
                        _scanProgress.postValue(progress)
                    } catch (e: Exception) {
                        // Hatalı uygulamaları göz ardı et
                    }
                }
                
                repository.insertApps(scannedApps)
                _lastScanTime.postValue(System.currentTimeMillis())
                
            } catch (e: Exception) {
                // Hata yönetimi
            } finally {
                _isScanning.postValue(false)
            }
        }
    }
    
    fun uninstallApp(packageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAppByPackage(packageName)
        }
    }
    
    fun whitelistApp(packageName: String, isWhitelisted: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateWhitelistStatus(packageName, isWhitelisted)
        }
    }
}