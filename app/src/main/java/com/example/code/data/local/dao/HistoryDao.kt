package com.example.code.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.code.data.local.entity.AnimalEntity
import com.example.code.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: HistoryEntity)

    @Query("DELETE FROM history WHERE userId = :userId AND animalId = :animalId")
    suspend fun deleteByAnimal(userId: Int, animalId: Int)

    @Query("DELETE FROM history WHERE userId = :userId")
    suspend fun clearAll(userId: Int)

    @Query("SELECT COUNT(*) FROM history WHERE userId = :userId")
    suspend fun countByUser(userId: Int): Int

    @Query("""
        SELECT DISTINCT a.* FROM animals a
        INNER JOIN history h ON a.id = h.animalId
        WHERE h.userId = :userId
        ORDER BY h.timestamp DESC
    """)
    fun getHistoryAnimalsByUser(userId: Int): Flow<List<AnimalEntity>>
}
