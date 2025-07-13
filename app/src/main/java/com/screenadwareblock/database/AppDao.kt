package com.screenadwareblock.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AppDao {
    
    @Query("SELECT * FROM apps ORDER BY threatLevel DESC, appName ASC")
    fun getAllApps(): LiveData<List<AppEntity>>
    
    @Query("SELECT * FROM apps WHERE threatLevel > 0 ORDER BY threatLevel DESC")
    fun getThreatApps(): LiveData<List<AppEntity>>
    
    @Query("SELECT * FROM apps WHERE packageName = :packageName")
    suspend fun getApp(packageName: String): AppEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: AppEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(apps: List<AppEntity>)
    
    @Update
    suspend fun updateApp(app: AppEntity)
    
    @Delete
    suspend fun deleteApp(app: AppEntity)
    
    @Query("DELETE FROM apps WHERE packageName = :packageName")
    suspend fun deleteAppByPackage(packageName: String)
    
    @Query("UPDATE apps SET isWhitelisted = :isWhitelisted WHERE packageName = :packageName")
    suspend fun updateWhitelistStatus(packageName: String, isWhitelisted: Boolean)
    
    @Query("SELECT COUNT(*) FROM apps WHERE threatLevel > 0")
    fun getThreatCount(): LiveData<Int>
}

@Dao
interface ThreatSignatureDao {
    
    @Query("SELECT * FROM threat_signatures WHERE isActive = 1")
    suspend fun getActiveSignatures(): List<ThreatSignature>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignature(signature: ThreatSignature)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignatures(signatures: List<ThreatSignature>)
    
    @Query("DELETE FROM threat_signatures")
    suspend fun clearAllSignatures()
}