package com.example.data.db.dao

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
    suspend fun delete(id: Long)

    @Query("SELECT * FROM MainIndexEntity")
    suspend fun getAllIndexes(): List<MainIndexEntity>

    @Query("SELECT MAX(id) FROM MainIndexEntity")
    suspend fun getMaxId(): Int?
}