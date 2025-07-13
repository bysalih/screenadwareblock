
package com.screenadwareblock

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppListAdapter(
    private val apps: List<AppInfo>,
    private val context: Context
) : RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val appName: TextView = view.findViewById(R.id.appName)
        val uninstallBtn: Button = view.findViewById(R.id.uninstallBtn)
        val warning: ImageView = view.findViewById(R.id.warningIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = apps.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        holder.appName.text = app.appName

        if (app.isInVirusDB || app.hasOverlayPermission) {
            holder.warning.visibility = View.VISIBLE
        } else {
            holder.warning.visibility = View.GONE
        }

        holder.uninstallBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_DELETE)
            intent.data = Uri.parse("package:${app.packageName}")
            context.startActivity(intent)
        }
    }
}
