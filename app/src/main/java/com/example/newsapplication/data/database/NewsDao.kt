package com.example.newsapplication.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<NewsEntity>)

    @Query("SELECT * FROM article LIMIT :limit OFFSET :offset")
    suspend fun getAllArticles(offset: Int, limit: Int): List<NewsEntity>
}