
package com.screenadwareblock

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val pm = packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val appList = packages.map {
            val hasOverlay = Settings.canDrawOverlays(this)
            AppInfo(
                appName = it.loadLabel(pm).toString(),
                packageName = it.packageName,
                hasOverlayPermission = hasOverlay,
                isRunningService = false,
                isInVirusDB = VirusDatabase.isInfected(it.packageName)
            )
        }

        adapter = AppListAdapter(appList, this)
        recyclerView.adapter = adapter
    }
}
