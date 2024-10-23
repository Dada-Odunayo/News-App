package com.example.newsapplication.api

import com.example.newsapplication.model.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPIService {
    @GET("v2/everything")
    suspend fun getAllNews(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String
    ): Response<SearchResponse>
}