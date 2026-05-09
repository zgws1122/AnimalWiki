package com.example.code.data.repository

import com.example.code.data.remote.dto.PostDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class AnimalRepositoryTest {

    @Test
    fun postDtoToEntity_mapsAllFieldsCorrectly() = runTest {
        val dto = PostDto(
            userId = 1,
            id = 42,
            title = "东北虎",
            body = "东北虎是现存最大的猫科动物。"
        )

        val entity = AnimalRepository.toEntity(dto)

        assertEquals(42, entity.id)
        assertEquals("东北虎", entity.name)
        assertEquals("哺乳动物", entity.category)
        assertEquals("东北虎是现存最大的猫科动物。", entity.description)
    }

    @Test
    fun postDtoToEntity_mapsBirdCategory() = runTest {
        val dto = PostDto(userId = 2, id = 10, title = "朱鹮", body = "国家一级保护动物")
        val entity = AnimalRepository.toEntity(dto)
        assertEquals("鸟类", entity.category)
    }

    @Test
    fun postDtoToEntity_mapsReptileCategory() = runTest {
        val dto = PostDto(userId = 3, id = 15, title = "中华鳖", body = "淡水水域分布")
        val entity = AnimalRepository.toEntity(dto)
        assertEquals("爬行动物", entity.category)
    }

    @Test
    fun postDtoToEntity_unknownUserIdMapsToOther() = runTest {
        val dto = PostDto(userId = 99, id = 50, title = "未知", body = "未分类")
        val entity = AnimalRepository.toEntity(dto)
        assertEquals("其他", entity.category)
    }
}
