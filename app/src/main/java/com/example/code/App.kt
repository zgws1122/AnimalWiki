package com.example.code

import android.app.Application
import com.example.code.data.local.AppDatabase
import com.example.code.data.remote.ApiService
import com.example.code.data.repository.AnimalRepository
import com.example.code.data.repository.UserRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var apiService: ApiService
        private set

    lateinit var repository: AnimalRepository
        private set

    lateinit var userRepository: UserRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // Room 数据库
        database = AppDatabase.getInstance(this)

        // Retrofit 网络层
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://zoology.especies.cn/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Repository：协调 Room + Retrofit
        repository = AnimalRepository(apiService, database.animalDao(), database.favoriteDao(), database.historyDao())

        // 用户仓库
        userRepository = UserRepository(database.userDao())
    }
}
