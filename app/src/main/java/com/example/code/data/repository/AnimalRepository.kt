package com.example.code.data.repository

import com.example.code.data.local.dao.AnimalDao
import com.example.code.data.local.dao.FavoriteDao
import com.example.code.data.local.dao.HistoryDao
import com.example.code.data.local.entity.AnimalEntity
import com.example.code.data.local.entity.FavoriteEntity
import com.example.code.data.local.entity.HistoryEntity
import com.example.code.data.remote.ApiService
import com.example.code.data.remote.dto.DescriptionInfo
import kotlinx.coroutines.flow.Flow

class AnimalRepository(
    private val api: ApiService,
    private val dao: AnimalDao,
    private val favoriteDao: FavoriteDao,
    private val historyDao: HistoryDao
) {

    fun getAllAnimals(): Flow<List<AnimalEntity>> = dao.getAll()

    fun getAnimalsByCategory(category: String): Flow<List<AnimalEntity>> =
        dao.getByCategory(category)

    fun searchAnimals(keyword: String): Flow<List<AnimalEntity>> =
        dao.search(keyword)

    fun getAllCategories(): Flow<List<String>> = dao.getAllCategories()

    suspend fun getAnimalById(id: Int): AnimalEntity? = dao.getById(id)

    suspend fun hasLocalData(): Boolean = dao.count() > 0

    /** 清空并重新写入数据（每次启动调用，保证 JSON 修改后数据更新） */
    suspend fun syncAnimals(animals: List<AnimalEntity>) {
        dao.deleteAll()
        dao.insertAll(animals)
    }

    /** 收藏功能 */
    suspend fun isFavorite(userId: Int, animalId: Int): Boolean =
        favoriteDao.isFavorite(userId, animalId)

    suspend fun addFavorite(userId: Int, animalId: Int) {
        favoriteDao.insert(FavoriteEntity(userId = userId, animalId = animalId))
    }

    suspend fun removeFavorite(userId: Int, animalId: Int) {
        favoriteDao.delete(userId, animalId)
    }

    suspend fun getFavoriteCount(userId: Int): Int =
        favoriteDao.countByUser(userId)

    fun getFavoriteAnimals(userId: Int): Flow<List<AnimalEntity>> =
        favoriteDao.getFavoriteAnimalsByUser(userId)

    /** 浏览历史 */
    suspend fun addHistory(userId: Int, animalId: Int) {
        historyDao.insert(HistoryEntity(userId = userId, animalId = animalId))
    }

    fun getHistoryAnimals(userId: Int): Flow<List<AnimalEntity>> =
        historyDao.getHistoryAnimalsByUser(userId)

    suspend fun clearHistory(userId: Int) {
        historyDao.clearAll(userId)
    }

    /**
     * 从中国动物主题数据库 API 获取物种详细描述
     * @param speciesName 中文物种名（如"虎"、"丹顶鹤"）
     * @param category 动物分类（用于匹配对应数据库）
     * @return 描述信息列表，失败返回空列表
     */
    suspend fun fetchSpeciesDescription(
        speciesName: String,
        category: String
    ): List<DescriptionInfo> {
        return try {
            val dbaseName = CATEGORY_TO_DB[category] ?: return emptyList()

            // 1. 查询描述类型
            val typeResponse = api.getDescriptionType(speciesName, dbaseName, API_KEY)
            if (typeResponse.code != 200 || typeResponse.data == null) return emptyList()

            // 2. 取第一个描述类型的 ID
            val firstType = typeResponse.data.desType.firstOrNull()
            val typeId = firstType?.keys?.firstOrNull() ?: return emptyList()

            // 3. 查询描述内容
            val descResponse = api.getDescription(speciesName, dbaseName, typeId, API_KEY)
            if (descResponse.code != 200 || descResponse.data == null) return emptyList()

            descResponse.data.descriptionInfo
        } catch (e: Exception) {
            emptyList()
        }
    }

    companion object {
        const val API_KEY = "fd5ca3c1139046b68ee3cfb3c0cfe416"

        /** 动物分类 → API 数据库名称 */
        val CATEGORY_TO_DB = mapOf(
            "哺乳动物" to "中国哺乳动物数据库",
            "鸟类" to "中国鸟类数据库",
            "爬行动物" to "中国爬行动物数据库",
            "两栖动物" to "中国两栖动物",
            "鱼类" to "中国内陆水体鱼类数据库",
            "昆虫" to "中国直翅目与革翅目昆虫数据库"
        )
    }
}
