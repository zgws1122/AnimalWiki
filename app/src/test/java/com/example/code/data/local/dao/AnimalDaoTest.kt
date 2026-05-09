package com.example.code.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.code.data.local.AppDatabase
import com.example.code.data.local.entity.AnimalEntity
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
        val now = System.currentTimeMillis()
        val tiger = AnimalEntity(
            id = 1,
            name = "老虎",
            category = "哺乳动物",
            description = "大型猫科动物",
            imageUrl = null,
            timestamp = now
        )
        val eagle = AnimalEntity(
            id = 2,
            name = "金雕",
            category = "鸟类",
            description = "大型猛禽",
            imageUrl = null,
            timestamp = now + 100
        )

        dao.insert(tiger)
        dao.insert(eagle)

        val result = dao.getAll()
        assertEquals(2, result.size)
        assertEquals("金雕", result[0].name)
        assertEquals("老虎", result[1].name)
    }

    @Test
    fun insertAndDelete() = runTest {
        val animal = AnimalEntity(
            id = 1,
            name = "大熊猫",
            category = "哺乳动物",
            description = "中国国宝",
            imageUrl = null
        )

        dao.insert(animal)
        assertEquals(1, dao.getAll().size)

        dao.delete(animal)
        assertEquals(0, dao.getAll().size)
    }

    @Test
    fun getById() = runTest {
        val animal = AnimalEntity(
            id = 1,
            name = "扬子鳄",
            category = "爬行动物",
            description = "中国特有鳄鱼",
            imageUrl = null
        )

        dao.insert(animal)

        val found = dao.getById(1)
        assertNotNull(found)
        assertEquals("扬子鳄", found!!.name)
        assertEquals("爬行动物", found.category)
    }

    @Test
    fun updateAnimal() = runTest {
        val original = AnimalEntity(
            id = 1,
            name = "丹顶鹤",
            category = "鸟类",
            description = "仙鹤",
            imageUrl = null
        )
        dao.insert(original)

        val updated = original.copy(name = "丹顶鹤（更新）", description = "濒危保护动物")
        dao.update(updated)

        val result = dao.getById(1)
        assertNotNull(result)
        assertEquals("丹顶鹤（更新）", result!!.name)
        assertEquals("濒危保护动物", result.description)
    }

    @Test
    fun insertAllAndDeleteAll() = runTest {
        val animals = listOf(
            AnimalEntity(1, "企鹅", "鸟类", "不会飞的鸟", null),
            AnimalEntity(2, "海豚", "哺乳动物", "海洋哺乳动物", null),
            AnimalEntity(3, "蝴蝶", "昆虫", "鳞翅目昆虫", null)
        )

        dao.insertAll(animals)
        assertEquals(3, dao.getAll().size)

        dao.deleteAll()
        assertEquals(0, dao.getAll().size)
    }

    @Test
    fun getByIdReturnsNullForMissingId() = runTest {
        val result = dao.getById(999)
        assertNull(result)
    }
}
