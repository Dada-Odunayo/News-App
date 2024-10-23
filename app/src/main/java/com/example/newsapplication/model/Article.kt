package com.example.newsapplication.model

import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.Date

data class Article(
    val author: String,
    val content: String,
    val description: String,
    val publishedAt: String,
    val title: String,
    val url: String,
    val urlToImage: String
)