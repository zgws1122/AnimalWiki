package com.example.code.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "animals")
data class AnimalEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val category: String,
    val description: String,
    val imageUrl: String?,
    val timestamp: Long = System.currentTimeMillis()
)
