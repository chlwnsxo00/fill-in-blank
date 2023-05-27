package com.example.data.db.dao

import MainIndexItems
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.data.db.entity.MainIndexEntity

@Dao
interface MainIndexDao {
    @Insert
    suspend fun insert(user: MainIndexEntity)

    @Update
    suspend fun update(user: MainIndexEntity)

    @Query("DELETE FROM MainIndexEntity WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM MainIndexEntity")
    suspend fun getAllIndexes(): List<MainIndexItems>
}