package com.example.code.data.remote

import com.example.code.data.remote.dto.PostDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("posts")
    suspend fun getPosts(): List<PostDto>

    @GET("posts/{id}")
    suspend fun getPost(@Path("id") id: Int): PostDto

    @POST("posts")
    suspend fun createPost(@Body post: PostDto): PostDto

    @GET("posts")
    suspend fun getPostsByUser(@Query("userId") userId: Int): List<PostDto>
}
