package com.example.data.db.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.db.entity.MainIndexEntity

@Database(
    entities = [MainIndexEntity::class],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {

    // DAO
    abstract fun mainIndexDao(): MainIndexDao

    // 싱글톤 패턴
    companion object{
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context:Context): AppDatabase {
            if (instance ==null){
                instance = Room.databaseBuilder(context, AppDatabase::class.java,"AppDatabase")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return instance!!
        }
    }
}