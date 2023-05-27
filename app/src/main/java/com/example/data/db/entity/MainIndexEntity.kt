package com.example.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MainIndexEntity(
    @PrimaryKey val id: Int,
    val itemTitle:String,
    val text_start_Inner_Index: String
)
