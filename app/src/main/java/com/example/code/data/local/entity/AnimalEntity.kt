package com.example.code.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "animals")
data class AnimalEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val latinName: String,
    val category: String,
    val description: String,
    val habitat: String,
    val diet: String,
    val conservationStatus: String,
    val imageUrl: String?,
    val taxonomy: String = "",
    val bodySize: String = "",
    val distribution: String = ""
)
