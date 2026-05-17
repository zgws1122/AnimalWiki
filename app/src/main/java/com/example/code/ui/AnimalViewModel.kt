package com.example.code.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.code.App
import com.example.code.data.local.LocalDataSource
import com.example.code.data.local.entity.AnimalEntity
import com.example.code.data.remote.dto.DescriptionInfo
import com.example.code.data.repository.AnimalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnimalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AnimalRepository = (application as App).repository

    val allAnimals: Flow<List<AnimalEntity>> = repository.getAllAnimals()
    val categories: Flow<List<String>> = repository.getAllCategories()

    /** 详情页的 API 描述数据 */
    private val _apiDescriptions = MutableStateFlow<List<DescriptionInfo>>(emptyList())
    val apiDescriptions: StateFlow<List<DescriptionInfo>> = _apiDescriptions

    private val _isLoadingDescription = MutableStateFlow(false)
    val isLoadingDescription: StateFlow<Boolean> = _isLoadingDescription

    init {
        // 每次启动都从 JSON 同步数据到 Room，确保数据实时更新
        viewModelScope.launch {
            val animals = LocalDataSource.loadAnimals(application)
            repository.syncAnimals(animals)
        }
    }

    fun animalsByCategory(category: String): Flow<List<AnimalEntity>> =
        repository.getAnimalsByCategory(category)

    fun searchAnimals(keyword: String): Flow<List<AnimalEntity>> =
        repository.searchAnimals(keyword)

    suspend fun getAnimalById(id: Int): AnimalEntity? =
        repository.getAnimalById(id)

    /** 从 API 获取物种详细描述 */
    fun loadSpeciesDescription(speciesName: String, category: String) {
        viewModelScope.launch {
            _isLoadingDescription.value = true
            _apiDescriptions.value = repository.fetchSpeciesDescription(speciesName, category)
            _isLoadingDescription.value = false
        }
    }

    fun clearDescription() {
        _apiDescriptions.value = emptyList()
    }

    /** 收藏状态 */
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    fun loadFavoriteStatus(userId: Int, animalId: Int) {
        viewModelScope.launch {
            _isFavorite.value = repository.isFavorite(userId, animalId)
        }
    }

    fun toggleFavorite(userId: Int, animalId: Int) {
        viewModelScope.launch {
            if (_isFavorite.value) {
                repository.removeFavorite(userId, animalId)
                _isFavorite.value = false
            } else {
                repository.addFavorite(userId, animalId)
                _isFavorite.value = true
            }
        }
    }

    suspend fun getFavoriteCount(userId: Int): Int =
        repository.getFavoriteCount(userId)

    fun getFavoriteAnimals(userId: Int): Flow<List<AnimalEntity>> =
        repository.getFavoriteAnimals(userId)

    /** 浏览历史 */
    fun addHistory(userId: Int, animalId: Int) {
        viewModelScope.launch { repository.addHistory(userId, animalId) }
    }

    fun getHistoryAnimals(userId: Int): Flow<List<AnimalEntity>> =
        repository.getHistoryAnimals(userId)

    fun clearHistory(userId: Int) {
        viewModelScope.launch { repository.clearHistory(userId) }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return AnimalViewModel(application) as T
        }
    }
}
