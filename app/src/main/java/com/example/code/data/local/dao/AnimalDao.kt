package com.example.code.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.code.data.local.entity.AnimalEntity

@Dao
interface AnimalDao {

    @Query("SELECT * FROM animals ORDER BY timestamp DESC")
    suspend fun getAll(): List<AnimalEntity>

    @Query("SELECT * FROM animals WHERE id = :id")
    suspend fun getById(id: Int): AnimalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(animals: List<AnimalEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(animal: AnimalEntity)

    @Update
    suspend fun update(animal: AnimalEntity)

    @Delete
    suspend fun delete(animal: AnimalEntity)

    @Query("DELETE FROM animals")
    suspend fun deleteAll()
}
