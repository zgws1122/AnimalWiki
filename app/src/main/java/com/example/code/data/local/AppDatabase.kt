package com.example.code.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.code.data.local.dao.AnimalDao
import com.example.code.data.local.entity.AnimalEntity

@Database(entities = [AnimalEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun animalDao(): AnimalDao
}
