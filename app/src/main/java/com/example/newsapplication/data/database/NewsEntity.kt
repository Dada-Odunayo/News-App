package com.example.newsapplication.data.database


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID


@Entity(tableName = "article")
data class NewsEntity(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val title: String,
    val author: String?,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?,
)