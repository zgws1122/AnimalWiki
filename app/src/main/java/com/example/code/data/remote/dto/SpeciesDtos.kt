package com.example.code.data.remote.dto

import com.google.gson.annotations.SerializedName

/** 查询数据库名称列表的响应 */
data class DbaseNameResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: DbaseNameData?
)

data class DbaseNameData(
    @SerializedName("sum") val sum: String,
    @SerializedName("dbaseName") val dbaseName: List<String>
)

/** 查询描述类型的响应 */
data class DescriptionTypeResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: DescriptionTypeData?
)

data class DescriptionTypeData(
    @SerializedName("desType") val desType: List<Map<String, String>>
)

/** 查询描述内容的响应 */
data class DescriptionResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: DescriptionData?
)

data class DescriptionData(
    @SerializedName("scientificName") val scientificName: String,
    @SerializedName("DescriptionInfo") val descriptionInfo: List<DescriptionInfo>
)

data class DescriptionInfo(
    @SerializedName("destitle") val title: String,
    @SerializedName("descontent") val content: String,
    @SerializedName("refs") val refs: List<String>?
)
