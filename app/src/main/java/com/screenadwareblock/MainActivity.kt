
package com.screenadwareblock

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var tvThreatCount: TextView
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var fabScan: FloatingActionButton
    
    private lateinit var adapter: SimpleAppListAdapter
    private val installedApps = mutableListOf<AppInfo>()
    private val threatApps = mutableListOf<AppInfo>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupRecyclerView()
        setupListeners()
        loadInstalledApps()
    }
    
    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        progressBar = findViewById(R.id.progressBar)
        tvThreatCount = findViewById(R.id.tvThreatCount)
        layoutEmpty = findViewById(R.id.layoutEmpty)
        fabScan = findViewById(R.id.fabScan)
    }
    
    private fun setupRecyclerView() {
        adapter = SimpleAppListAdapter(installedApps) { appInfo ->
            uninstallApp(appInfo)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
    
    private fun setupListeners() {
        fabScan.setOnClickListener {
            scanForThreats()
        }
        
        swipeRefreshLayout.setOnRefreshListener {
            loadInstalledApps()
        }
    }
    
    private fun loadInstalledApps() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val packageManager = packageManager
                val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                val apps = mutableListOf<AppInfo>()
                
                for (packageInfo in packages) {
                    val appInfo = AppInfo(
                        packageName = packageInfo.packageName,
                        appName = packageInfo.loadLabel(packageManager).toString(),
                        isSystemApp = (packageInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
                        threatLevel = ThreatLevel.SAFE
                    )
                    apps.add(appInfo)
                }
                
                withContext(Dispatchers.Main) {
                    installedApps.clear()
                    installedApps.addAll(apps)
                    adapter.notifyDataSetChanged()
                    swipeRefreshLayout.isRefreshing = false
                    
                    if (apps.isEmpty()) {
                        layoutEmpty.visibility = LinearLayout.VISIBLE
                        recyclerView.visibility = RecyclerView.GONE
                    } else {
                        layoutEmpty.visibility = LinearLayout.GONE
                        recyclerView.visibility = RecyclerView.VISIBLE
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(this@MainActivity, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun scanForThreats() {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                progressBar.visibility = ProgressBar.VISIBLE
            }
            
            try {
                threatApps.clear()
                
                // Basit tehdit tespit algoritması
                for (app in installedApps) {
                    val packageName = app.packageName.lowercase()
                    
                    when {
                        packageName.contains("cleaner") ||
                        packageName.contains("booster") ||
                        packageName.contains("battery") ||
                        packageName.contains("memory") ||
                        packageName.contains("speed") ||
                        packageName.contains("optimizer") ||
                        packageName.contains("antivirus") ||
                        packageName.contains("security") -> {
                            app.threatLevel = ThreatLevel.SUSPICIOUS
                            threatApps.add(app)
                        }
                        packageName.contains("ads") ||
                        packageName.contains("adware") ||
                        packageName.contains("popup") ||
                        packageName.contains("malware") -> {
                            app.threatLevel = ThreatLevel.MALWARE
                            threatApps.add(app)
                        }
                        else -> {
                            app.threatLevel = ThreatLevel.SAFE
                        }
                    }
                }
                
                withContext(Dispatchers.Main) {
                    progressBar.visibility = ProgressBar.GONE
                    tvThreatCount.text = threatApps.size.toString()
                    adapter.notifyDataSetChanged()
                    
                    val message = if (threatApps.isEmpty()) {
                        "Tehdit bulunamadı!"
                    } else {
                        "${threatApps.size} şüpheli uygulama bulundu!"
                    }
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = ProgressBar.GONE
                    Toast.makeText(this@MainActivity, "Tarama hatası: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun uninstallApp(appInfo: AppInfo) {
        try {
            if (appInfo.isSystemApp) {
                Toast.makeText(this, "Sistem uygulaması kaldırılamaz!", Toast.LENGTH_SHORT).show()
                return
            }
            
            val intent = Intent(Intent.ACTION_DELETE)
            intent.data = Uri.parse("package:${appInfo.packageName}")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Kaldırma hatası: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

data class AppInfo(
    val packageName: String,
    val appName: String,
    val isSystemApp: Boolean,
    var threatLevel: ThreatLevel
)

enum class ThreatLevel {
    SAFE, SUSPICIOUS, DANGEROUS, MALWARE
}
