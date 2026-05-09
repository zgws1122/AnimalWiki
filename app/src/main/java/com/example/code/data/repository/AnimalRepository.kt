package com.example.code.data.repository

import com.example.code.data.local.dao.AnimalDao
import com.example.code.data.local.entity.AnimalEntity
import com.example.code.data.remote.ApiService
import com.example.code.data.remote.dto.PostDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AnimalRepository(
    private val api: ApiService,
    private val dao: AnimalDao
) {
    private val _animals = MutableStateFlow<List<AnimalEntity>>(emptyList())
    val animals: Flow<List<AnimalEntity>> = _animals.asStateFlow()

    suspend fun refreshAnimals() {
        val posts = api.getPosts()
        val entities = posts.map { toEntity(it) }
        dao.deleteAll()
        dao.insertAll(entities)
        _animals.value = entities
    }

    suspend fun getAnimalById(id: Int): AnimalEntity? {
        val cached = dao.getById(id)
        if (cached != null) return cached
        return try {
            val post = api.getPost(id)
            val entity = toEntity(post)
            dao.insert(entity)
            entity
        } catch (e: Exception) {
            null
        }
    }

    suspend fun loadLocalAnimals() {
        _animals.value = dao.getAll()
    }

    companion object {
        private val CATEGORY_MAP = mapOf(
            1 to "哺乳动物",
            2 to "鸟类",
            3 to "爬行动物",
            4 to "两栖动物",
            5 to "鱼类",
            6 to "昆虫",
            7 to "哺乳动物",
            8 to "鸟类",
            9 to "爬行动物",
            10 to "鱼类"
        )

        fun toEntity(dto: PostDto): AnimalEntity = AnimalEntity(
            id = dto.id,
            name = dto.title,
            category = CATEGORY_MAP[dto.userId] ?: "其他",
            description = dto.body,
            imageUrl = null
        )
    }
}
