package com.example.code.data.repository

import com.example.code.data.local.entity.AnimalEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AnimalRepositoryTest {

    private val sampleJson = """
    [
      {
        "id": 1,
        "name": "大熊猫",
        "latinName": "Ailuropoda melanoleuca",
        "category": "哺乳动物",
        "description": "中国国宝",
        "habitat": "高山竹林",
        "diet": "竹子",
        "conservationStatus": "易危 (VU)",
        "imageUrl": null
      },
      {
        "id": 2,
        "name": "丹顶鹤",
        "latinName": "Grus japonensis",
        "category": "鸟类",
        "description": "东亚大型涉禽",
        "habitat": "湿地沼泽",
        "diet": "鱼虾蛙",
        "conservationStatus": "濒危 (EN)",
        "imageUrl": null
      },
      {
        "id": 3,
        "name": "扬子鳄",
        "latinName": "Alligator sinensis",
        "category": "爬行动物",
        "description": "中国特有鳄鱼",
        "habitat": "长江中下游",
        "diet": "螺蚌虾鱼",
        "conservationStatus": "极危 (CR)",
        "imageUrl": null
      }
    ]
    """.trimIndent()

    private fun parseAnimals(): List<AnimalEntity> {
        val type = object : TypeToken<List<AnimalEntity>>() {}.type
        return Gson().fromJson(sampleJson, type)
    }

    @Test
    fun jsonParsesCorrectly() {
        val animals = parseAnimals()
        assertEquals(3, animals.size)
    }

    @Test
    fun jsonMapsAllFields() {
        val animals = parseAnimals()
        val panda = animals[0]
        assertEquals(1, panda.id)
        assertEquals("大熊猫", panda.name)
        assertEquals("Ailuropoda melanoleuca", panda.latinName)
        assertEquals("哺乳动物", panda.category)
        assertEquals("中国国宝", panda.description)
        assertEquals("高山竹林", panda.habitat)
        assertEquals("竹子", panda.diet)
        assertEquals("易危 (VU)", panda.conservationStatus)
    }

    @Test
    fun jsonContainsAllCategories() {
        val animals = parseAnimals()
        val categories = animals.map { it.category }.distinct()
        assertTrue(categories.contains("哺乳动物"))
        assertTrue(categories.contains("鸟类"))
        assertTrue(categories.contains("爬行动物"))
    }

    @Test
    fun allFieldsNonBlank() {
        val animals = parseAnimals()
        animals.forEach { animal ->
            assertTrue(animal.name.isNotBlank())
            assertTrue(animal.latinName.isNotBlank())
            assertTrue(animal.description.isNotBlank())
            assertTrue(animal.habitat.isNotBlank())
            assertTrue(animal.diet.isNotBlank())
            assertTrue(animal.conservationStatus.isNotBlank())
        }
    }
}
