package com.persona.data

import Items
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blank.R

class itemAdapter(val itemlist:ArrayList<Items>) : RecyclerView.Adapter<itemAdapter.CustomViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item , parent, false)

        return CustomViewHolder(view)
    }
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.itemTitle.text = itemlist.get(position).itemTitle
    }

    override fun getItemCount(): Int {
        return itemlist.size
    }

    class CustomViewHolder(itemview : View): RecyclerView.ViewHolder(itemview) {
        val itemTitle = itemView.findViewById<TextView>(R.id.itemTitle)
    }
}

