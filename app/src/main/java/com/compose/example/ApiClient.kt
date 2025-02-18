package com.compose.example

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST

interface ApiClient {

    companion object {
        private const val BASE_URL = "https://d721011d-6316-42af-905f-48dfed01cfe4.mock.pstmn.io/"

        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiClient::class.java)
        }
        fun get(): ApiClient = retrofit
    }

    @POST("recent")
    suspend fun getRecentPeoples(): List<People>

    @POST("followed")
    suspend fun getFollowedPeoples(): List<People>
}