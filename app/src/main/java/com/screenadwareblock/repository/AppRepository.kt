package com.screenadwareblock.repository

import androidx.lifecycle.LiveData
import com.screenadwareblock.database.AppDao
import com.screenadwareblock.database.AppEntity
import com.screenadwareblock.database.ThreatSignature
import com.screenadwareblock.database.ThreatSignatureDao

class AppRepository(
    private val appDao: AppDao,
    private val threatSignatureDao: ThreatSignatureDao
) {
    
    fun getAllApps(): LiveData<List<AppEntity>> = appDao.getAllApps()
    
    fun getThreatApps(): LiveData<List<AppEntity>> = appDao.getThreatApps()
    
    fun getThreatCount(): LiveData<Int> = appDao.getThreatCount()
    
    suspend fun getApp(packageName: String): AppEntity? = appDao.getApp(packageName)
    
    suspend fun insertApp(app: AppEntity) = appDao.insertApp(app)
    
    suspend fun insertApps(apps: List<AppEntity>) = appDao.insertApps(apps)
    
    suspend fun updateApp(app: AppEntity) = appDao.updateApp(app)
    
    suspend fun deleteApp(app: AppEntity) = appDao.deleteApp(app)
    
    suspend fun deleteAppByPackage(packageName: String) = appDao.deleteAppByPackage(packageName)
    
    suspend fun updateWhitelistStatus(packageName: String, isWhitelisted: Boolean) = 
        appDao.updateWhitelistStatus(packageName, isWhitelisted)
    
    suspend fun getActiveSignatures(): List<ThreatSignature> = threatSignatureDao.getActiveSignatures()
    
    suspend fun insertSignatures(signatures: List<ThreatSignature>) = 
        threatSignatureDao.insertSignatures(signatures)
    
}