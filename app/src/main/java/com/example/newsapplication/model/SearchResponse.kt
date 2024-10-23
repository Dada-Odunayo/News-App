package com.example.newsapplication.model

data class SearchResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)