package com.example.code.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.code.data.local.entity.AnimalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalDao {

    @Query("SELECT * FROM animals ORDER BY name")
    fun getAll(): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals WHERE category = :category ORDER BY name")
    fun getByCategory(category: String): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals WHERE id = :id")
    suspend fun getById(id: Int): AnimalEntity?

    @Query("SELECT * FROM animals WHERE name LIKE '%' || :keyword || '%' OR latinName LIKE '%' || :keyword || '%'")
    fun search(keyword: String): Flow<List<AnimalEntity>>

    @Query("SELECT DISTINCT category FROM animals ORDER BY category")
    fun getAllCategories(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(animals: List<AnimalEntity>)

    @Query("DELETE FROM animals")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM animals")
    suspend fun count(): Int
}
