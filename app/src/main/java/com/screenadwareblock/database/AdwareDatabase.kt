package com.screenadwareblock.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [AppEntity::class, ThreatSignature::class],
    version = 1,
    exportSchema = false
)
abstract class AdwareDatabase : RoomDatabase() {
    
    abstract fun appDao(): AppDao
    abstract fun threatSignatureDao(): ThreatSignatureDao
    
    companion object {
        @Volatile
        private var INSTANCE: AdwareDatabase? = null
        
        fun getDatabase(context: Context): AdwareDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AdwareDatabase::class.java,
                    "adware_database"
                )
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
    
    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.threatSignatureDao())
                }
            }
        }
        
        private suspend fun populateDatabase(dao: ThreatSignatureDao) {
            // Pre-populate with known adware signatures
            val signatures = listOf(
                ThreatSignature(
                    packageName = "com.fake.cleaner",
                    threatType = "adware",
                    severity = 4,
                    description = "Sahte temizleyici uygulaması - reklam gösterir",
                    detectionMethod = "package_name"
                ),
                ThreatSignature(
                    packageName = "com.ads.camera.booster",
                    threatType = "adware", 
                    severity = 3,
                    description = "Kamera güçlendirici yalanı ile reklam gösterir",
                    detectionMethod = "package_name"
                ),
                ThreatSignature(
                    packagePattern = ".*\\.ads\\..*",
                    threatType = "adware",
                    severity = 3,
                    description = "Paket isminde 'ads' bulunan şüpheli uygulama",
                    detectionMethod = "package_pattern"
                ),
                ThreatSignature(
                    packagePattern = ".*cleaner.*",
                    threatType = "potentially_unwanted",
                    severity = 2,
                    description = "Temizleyici uygulamaları genellikle gereksiz reklamlar içerir",
                    detectionMethod = "package_pattern"
                ),
                ThreatSignature(
                    packagePattern = ".*booster.*",
                    threatType = "potentially_unwanted",
                    severity = 2,
                    description = "Güçlendirici uygulamalar genellikle etkisiz ve reklamlı",
                    detectionMethod = "package_pattern"
                )
            )
            dao.insertSignatures(signatures)
        }
    }
}