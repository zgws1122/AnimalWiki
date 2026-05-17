package com.example.code.data.remote

import com.example.code.data.remote.dto.DescriptionResponse
import com.example.code.data.remote.dto.DescriptionTypeResponse
import com.example.code.data.remote.dto.DbaseNameResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    /** 查询所有可用数据库名称 */
    @GET("api/v1/dbaseName")
    suspend fun getDbaseNames(
        @Query("apiKey") apiKey: String
    ): DbaseNameResponse

    /** 根据物种名查询描述类型 */
    @GET("api/v1/descriptionType")
    suspend fun getDescriptionType(
        @Query("scientificName") scientificName: String,
        @Query("dbaseName") dbaseName: String,
        @Query("apiKey") apiKey: String
    ): DescriptionTypeResponse

    /** 根据物种名和描述类型查询描述详情 */
    @GET("api/v1/description")
    suspend fun getDescription(
        @Query("scientificName") scientificName: String,
        @Query("dbaseName") dbaseName: String,
        @Query("descriptionType") descriptionType: String,
        @Query("apiKey") apiKey: String
    ): DescriptionResponse
}
