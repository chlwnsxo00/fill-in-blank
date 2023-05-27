package com.example.data.db.entity

import androidx.room.PrimaryKey

data class InnerIndexEntity(
    @PrimaryKey val id: Int,
    val itemTitle:String,
    val text_start_Inner_Index: String
)
