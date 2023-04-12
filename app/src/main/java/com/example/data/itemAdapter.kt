package com.persona.data

import Items
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.blank.InnerIndexActivity
import com.example.blank.MainIndexActivity
import com.example.blank.PlayActivity
import com.example.blank.R

class itemAdapter(val itemlist:ArrayList<Items>) : RecyclerView.Adapter<itemAdapter.CustomViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item , parent, false)

        return CustomViewHolder(view)
    }
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.itemTitle.text = itemlist.get(position).itemTitle
        holder.btn_start_Inner_Index.setOnClickListener {
            // 버튼 클릭 이벤트 처리
            val intent = Intent(holder.itemView.context, InnerIndexActivity::class.java)
            holder.itemView.context.startActivity(intent)
        }
        holder.btn_start_Play.setOnClickListener {
            // 버튼 클릭 이벤트 처리
            val intent = Intent(holder.itemView.context, PlayActivity::class.java)
            holder.itemView.context.startActivity(intent)
        }
        holder.btn_remove_list.setOnClickListener {
            // 버튼 클릭 이벤트 처리
            itemlist.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int {
        return itemlist.size
    }

    class CustomViewHolder(itemview : View): RecyclerView.ViewHolder(itemview) {
        val itemTitle = itemView.findViewById<TextView>(R.id.itemTitle)
        val btn_start_Inner_Index = itemView.findViewById<AppCompatButton>(R.id.btn_start_Inner_Index)
        val btn_start_Play = itemView.findViewById<TextView>(R.id.btn_start_Play)
        val btn_remove_list = itemView.findViewById<AppCompatButton>(R.id.btn_remove_list)
    }
}

