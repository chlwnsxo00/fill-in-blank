package com.example.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.data.Test

@Dao
interface TestDao {
    @Insert
    suspend fun insert(user: Test)

    @Update
    suspend fun update(user: Test)

    @Query("DELETE FROM Test WHERE id = :testId")
    suspend fun delete(testId: Int)

    @Query("SELECT * FROM Test")
    suspend fun getAllUsers(): List<Test>
}