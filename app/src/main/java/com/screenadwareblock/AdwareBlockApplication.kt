package com.screenadwareblock

import android.app.Application
import com.screenadwareblock.database.AdwareDatabase
import com.screenadwareblock.repository.AppRepository

class AdwareBlockApplication : Application() {
    
    val database by lazy { AdwareDatabase.getDatabase(this) }
    val repository by lazy { AppRepository(database.appDao(), database.threatSignatureDao()) }
    
}