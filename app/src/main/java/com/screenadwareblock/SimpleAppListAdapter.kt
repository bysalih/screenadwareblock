package com.screenadwareblock

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class SimpleAppListAdapter(
    private val apps: List<AppInfo>,
    private val onUninstallClick: (AppInfo) -> Unit
) : RecyclerView.Adapter<SimpleAppListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardApp: CardView = view.findViewById(R.id.cardApp)
        val textAppName: TextView = view.findViewById(R.id.textAppName)
        val textPackageName: TextView = view.findViewById(R.id.textPackageName)
        val textThreatLevel: TextView = view.findViewById(R.id.textThreatLevel)
        val textSystemApp: TextView = view.findViewById(R.id.textSystemApp)
        val buttonUninstall: Button = view.findViewById(R.id.buttonUninstall)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        
        holder.textAppName.text = app.appName
        holder.textPackageName.text = app.packageName
        
        // Tehdit seviyesine göre renk ayarlama
        when (app.threatLevel) {
            ThreatLevel.SAFE -> {
                holder.cardApp.setCardBackgroundColor(
                    holder.itemView.context.getColor(android.R.color.holo_green_light)
                )
                holder.textThreatLevel.text = "Güvenli"
            }
            ThreatLevel.SUSPICIOUS -> {
                holder.cardApp.setCardBackgroundColor(
                    holder.itemView.context.getColor(android.R.color.holo_orange_light)
                )
                holder.textThreatLevel.text = "Şüpheli"
            }
            ThreatLevel.DANGEROUS -> {
                holder.cardApp.setCardBackgroundColor(
                    holder.itemView.context.getColor(android.R.color.holo_red_light)
                )
                holder.textThreatLevel.text = "Tehlikeli"
            }
            ThreatLevel.MALWARE -> {
                holder.cardApp.setCardBackgroundColor(
                    holder.itemView.context.getColor(android.R.color.holo_red_dark)
                )
                holder.textThreatLevel.text = "Malware"
            }
        }
        
        // Sistem uygulaması kontrolü
        if (app.isSystemApp) {
            holder.textSystemApp.text = "Sistem Uygulaması"
            holder.textSystemApp.visibility = TextView.VISIBLE
            holder.buttonUninstall.isEnabled = false
            holder.buttonUninstall.text = "Kaldırılamaz"
        } else {
            holder.textSystemApp.visibility = TextView.GONE
            holder.buttonUninstall.isEnabled = true
            holder.buttonUninstall.text = "Kaldır"
        }
        
        holder.buttonUninstall.setOnClickListener {
            onUninstallClick(app)
        }
    }

    override fun getItemCount(): Int = apps.size
}