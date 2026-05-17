package com.example.code.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.code.data.local.AppDatabase
import com.example.code.data.local.entity.AnimalEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AnimalDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: AnimalDao

    private fun animal(
        id: Int, name: String, category: String, description: String = "测试描述"
    ) = AnimalEntity(
        id = id, name = name, latinName = "Test $name", category = category,
        description = description, habitat = "测试栖息地", diet = "测试食性",
        conservationStatus = "无危 (LC)", imageUrl = null
    )

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.animalDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetAll() = runTest {
        dao.insertAll(listOf(animal(1, "老虎", "哺乳动物"), animal(2, "金雕", "鸟类")))

        val result = dao.getAll().first()
        assertEquals(2, result.size)
    }

    @Test
    fun getByCategory() = runTest {
        dao.insertAll(listOf(
            animal(1, "老虎", "哺乳动物"),
            animal(2, "金雕", "鸟类"),
            animal(3, "大熊猫", "哺乳动物")
        ))

        val mammals = dao.getByCategory("哺乳动物").first()
        assertEquals(2, mammals.size)
        assert(mammals.all { it.category == "哺乳动物" })
    }

    @Test
    fun getById() = runTest {
        dao.insertAll(listOf(animal(1, "扬子鳄", "爬行动物", "中国特有鳄鱼")))

        val found = dao.getById(1)
        assertNotNull(found)
        assertEquals("扬子鳄", found!!.name)
    }

    @Test
    fun getByIdReturnsNullForMissingId() = runTest {
        assertNull(dao.getById(999))
    }

    @Test
    fun searchByName() = runTest {
        dao.insertAll(listOf(
            animal(1, "大熊猫", "哺乳动物"),
            animal(2, "东北虎", "哺乳动物"),
            animal(3, "金雕", "鸟类")
        ))

        val results = dao.search("大熊猫").first()
        assertEquals(1, results.size)
        assertEquals("大熊猫", results[0].name)
    }

    @Test
    fun searchByLatinName() = runTest {
        dao.insertAll(listOf(
            animal(1, "大熊猫", "哺乳动物"),
            animal(2, "东北虎", "哺乳动物")
        ))

        val results = dao.search("Test 大熊猫").first()
        assertEquals(1, results.size)
    }

    @Test
    fun getAllCategories() = runTest {
        dao.insertAll(listOf(
            animal(1, "老虎", "哺乳动物"),
            animal(2, "金雕", "鸟类"),
            animal(3, "扬子鳄", "爬行动物")
        ))

        val categories = dao.getAllCategories().first()
        assertEquals(3, categories.size)
        assert(categories.contains("哺乳动物"))
        assert(categories.contains("鸟类"))
        assert(categories.contains("爬行动物"))
    }

    @Test
    fun count() = runTest {
        assertEquals(0, dao.count())
        dao.insertAll(listOf(animal(1, "老虎", "哺乳动物"), animal(2, "金雕", "鸟类")))
        assertEquals(2, dao.count())
    }
}
