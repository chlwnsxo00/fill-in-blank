package com.example.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MainIndexEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemTitle:String,
    val text_start_Inner_Index: String
)
