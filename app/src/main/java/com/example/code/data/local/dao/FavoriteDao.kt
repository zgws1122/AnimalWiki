package com.example.code.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.code.data.local.entity.AnimalEntity
import com.example.code.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites WHERE userId = :userId AND animalId = :animalId LIMIT 1")
    suspend fun getFavorite(userId: Int, animalId: Int): FavoriteEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND animalId = :animalId)")
    suspend fun isFavorite(userId: Int, animalId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userId = :userId AND animalId = :animalId")
    suspend fun delete(userId: Int, animalId: Int)

    @Query("SELECT COUNT(*) FROM favorites WHERE userId = :userId")
    suspend fun countByUser(userId: Int): Int

    @Query("SELECT a.* FROM animals a INNER JOIN favorites f ON a.id = f.animalId WHERE f.userId = :userId ORDER BY f.id DESC")
    fun getFavoriteAnimalsByUser(userId: Int): Flow<List<AnimalEntity>>
}
