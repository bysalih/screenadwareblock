package com.screenadwareblock.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.screenadwareblock.database.AppEntity
import com.screenadwareblock.databinding.ItemAppEnhancedBinding
import org.json.JSONArray

class EnhancedAppListAdapter(
    private val onUninstallClick: (AppEntity) -> Unit,
    private val onWhitelistClick: (AppEntity) -> Unit
) : ListAdapter<AppEntity, EnhancedAppListAdapter.AppViewHolder>(AppDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemAppEnhancedBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AppViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AppViewHolder(
        private val binding: ItemAppEnhancedBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(app: AppEntity) {
            binding.apply {
                textAppName.text = app.appName
                textPackageName.text = app.packageName
                
                // Tehdit seviyesini göster
                when (app.threatLevel) {
                    0 -> {
                        cardApp.setCardBackgroundColor(Color.parseColor("#E8F5E8"))
                        textThreatLevel.text = "Güvenli"
                        textThreatLevel.setTextColor(Color.parseColor("#4CAF50"))
                    }
                    1 -> {
                        cardApp.setCardBackgroundColor(Color.parseColor("#FFF3E0"))
                        textThreatLevel.text = "Şüpheli"
                        textThreatLevel.setTextColor(Color.parseColor("#FF9800"))
                    }
                    2 -> {
                        cardApp.setCardBackgroundColor(Color.parseColor("#FFEBEE"))
                        textThreatLevel.text = "Tehlikeli"
                        textThreatLevel.setTextColor(Color.parseColor("#F44336"))
                    }
                    3 -> {
                        cardApp.setCardBackgroundColor(Color.parseColor("#FCE4EC"))
                        textThreatLevel.text = "Reklam/Kötü Amaçlı"
                        textThreatLevel.setTextColor(Color.parseColor("#E91E63"))
                    }
                }
                
                // Tehdit detaylarını göster
                val reasons = try {
                    val jsonArray = JSONArray(app.detectionReasons)
                    (0 until jsonArray.length()).map { jsonArray.getString(it) }
                } catch (e: Exception) {
                    emptyList()
                }
                
                if (reasons.isNotEmpty()) {
                    textThreatDetails.text = reasons.joinToString("\n• ", "• ")
                } else {
                    textThreatDetails.text = "Tehdit bulunamadı"
                }
                
                // İzinleri göster
                val permissions = try {
                    val jsonArray = JSONArray(app.suspiciousPermissions)
                    (0 until jsonArray.length()).map { jsonArray.getString(it) }
                } catch (e: Exception) {
                    emptyList()
                }
                
                if (permissions.isNotEmpty()) {
                    textSuspiciousPermissions.text = "Şüpheli İzinler: ${permissions.size}"
                } else {
                    textSuspiciousPermissions.text = "Şüpheli izin yok"
                }
                
                // Sistem uygulaması kontrolü
                if (app.isSystemApp) {
                    textSystemApp.text = "Sistem Uygulaması"
                    buttonUninstall.isEnabled = false
                    buttonUninstall.text = "Kaldırılamaz"
                } else {
                    textSystemApp.text = "Kullanıcı Uygulaması"
                    buttonUninstall.isEnabled = true
                    buttonUninstall.text = "Kaldır"
                }
                
                // Whitelist durumu
                toggleWhitelist.isChecked = app.isWhitelisted
                
                // Buton tıklamaları
                buttonUninstall.setOnClickListener {
                    if (!app.isSystemApp) {
                        onUninstallClick(app)
                    }
                }
                
                toggleWhitelist.setOnCheckedChangeListener { _, isChecked ->
                    onWhitelistClick(app)
                }
                
                // Yükleme tarihi
                val installDate = android.text.format.DateFormat.format("dd/MM/yyyy", app.installDate)
                textInstallDate.text = "Yükleme: $installDate"
            }
        }
    }

    class AppDiffCallback : DiffUtil.ItemCallback<AppEntity>() {
        override fun areItemsTheSame(oldItem: AppEntity, newItem: AppEntity): Boolean {
            return oldItem.packageName == newItem.packageName
        }

        override fun areContentsTheSame(oldItem: AppEntity, newItem: AppEntity): Boolean {
            return oldItem == newItem
        }
    }
}