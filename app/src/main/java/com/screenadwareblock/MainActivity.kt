
package com.screenadwareblock

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.screenadwareblock.adapter.EnhancedAppListAdapter
import com.screenadwareblock.databinding.ActivityMainBinding
import com.screenadwareblock.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: EnhancedAppListAdapter
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        setupSwipeRefresh()
        setupFab()
        observeViewModel()
        
        // İlk tarama
        if (savedInstanceState == null) {
            viewModel.scanAllApps()
        }
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Reklam Engelleyici"
    }
    
    private fun setupRecyclerView() {
        adapter = EnhancedAppListAdapter(
            onUninstallClick = { app -> uninstallApp(app.packageName) },
            onWhitelistClick = { app -> viewModel.whitelistApp(app.packageName, !app.isWhitelisted) }
        )
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.scanAllApps()
        }
    }
    
    private fun setupFab() {
        binding.fabScan.setOnClickListener {
            viewModel.scanAllApps()
        }
    }
    
    private fun observeViewModel() {
        viewModel.allApps.observe(this) { apps ->
            adapter.submitList(apps)
            updateEmptyState(apps.isEmpty())
        }
        
        viewModel.isScanning.observe(this) { isScanning ->
            binding.swipeRefresh.isRefreshing = isScanning
            binding.progressBar.visibility = if (isScanning) View.VISIBLE else View.GONE
            binding.fabScan.isEnabled = !isScanning
        }
        
        viewModel.scanProgress.observe(this) { progress ->
            binding.progressBar.progress = progress
        }
        
        viewModel.threatCount.observe(this) { count ->
            supportActionBar?.subtitle = if (count > 0) {
                "$count tehdit tespit edildi"
            } else {
                "Hiç tehdit bulunamadı"
            }
        }
        
        viewModel.lastScanTime.observe(this) { time ->
            if (time > 0) {
                val timeStr = android.text.format.DateFormat.format("dd/MM/yyyy HH:mm", time)
                Toast.makeText(this, "Son tarama: $timeStr", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
    
    private fun uninstallApp(packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.parse("package:$packageName")
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Uygulama kaldırılamadı: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_scan -> {
                viewModel.scanAllApps()
                true
            }
            R.id.action_threats_only -> {
                // Sadece tehditli uygulamaları göster
                viewModel.threatApps.observe(this) { threatApps ->
                    adapter.submitList(threatApps)
                }
                true
            }
            R.id.action_all_apps -> {
                // Tüm uygulamaları göster
                viewModel.allApps.observe(this) { allApps ->
                    adapter.submitList(allApps)
                }
                true
            }
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
