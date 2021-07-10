package com.deventhirran.carrental.ui.dashboard

import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.deventhirran.carrental.R

class AdsList(private val listAds: MutableList<ListAds>) : RecyclerView.Adapter<AdsList.ViewHolder>() {
    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.listRow_title)
        val price: TextView = itemView.findViewById(R.id.listRow_price)
        val selection: ConstraintLayout = itemView.findViewById(R.id.listRow_selection)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ads_list_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val loc = listAds[position]
        holder.title.text = loc.title
        holder.price.text = loc.price
        d("debugAdsList", "debugAdsListAdapter: ${loc.price}")
    }

    override fun getItemCount(): Int {
        return listAds.size
    }

}