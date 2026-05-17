package com.example.code.data.local

import android.content.Context
import com.example.code.data.local.entity.AnimalEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object LocalDataSource {

    fun loadAnimals(context: Context): List<AnimalEntity> {
        val json = context.assets.open("animals.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<AnimalEntity>>() {}.type
        return Gson().fromJson(json, type)
    }
}
