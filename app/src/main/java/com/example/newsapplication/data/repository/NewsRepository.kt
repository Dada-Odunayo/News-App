package com.example.newsapplication.data.repository

import android.util.Log
import com.example.newsapplication.BuildConfig
import com.example.newsapplication.api.NewsAPIService
import com.example.newsapplication.data.database.NewsDao
import com.example.newsapplication.data.database.NewsEntity
import com.example.newsapplication.model.Article

import javax.inject.Inject
import kotlin.collections.map

class NewsRepository @Inject constructor(
    private val newsAPIService: NewsAPIService,
    private val articleDao: NewsDao
) {

    suspend fun fetchNews(query: String,offset: Int , limit: Int ): DataSource<List<Article>> {
        return try {
            val apiKey = BuildConfig.NEWS_API_KEY
            val response = newsAPIService.getAllNews(query, apiKey)

            if (response.isSuccessful) {
                response.body()?.articles?.let { articles ->
                    DataSource.Success(articles, fromNetwork = true)
                } ?: run {
                    // Return cached articles if the response body is null
                    val cachedArticles = articleDao.getAllArticles(offset, limit).map { it.toArticle() }
                    DataSource.Success(cachedArticles, fromNetwork = false)
                }
            } else {
                val cachedArticles = articleDao.getAllArticles(offset, limit).map { it.toArticle()  }
                DataSource.Success(cachedArticles, fromNetwork = false)
            }
        } catch (e: Exception) {

            // Return cached articles in case of an exception
            val cachedArticles = articleDao.getAllArticles(offset, limit).map { it.toArticle() }
            if (cachedArticles.isNotEmpty()){
                DataSource.Success(cachedArticles, fromNetwork = false)
            }
          else{
                DataSource.Error(e.localizedMessage ?: "An error occurred")
            }
        }
    }

    suspend fun fetchNewsLocally(offset: Int, limit: Int ): DataSource<List<Article>> {
        return try {
            val cachedArticles = articleDao.getAllArticles(offset,limit).map {
                it.toArticle()
            }
            if (cachedArticles.isNotEmpty()){
                DataSource.Success(cachedArticles, fromNetwork = false)
            }
            DataSource.Success(cachedArticles, fromNetwork = false)
        } catch (e: Exception) {
            DataSource.Error(e.localizedMessage ?: "An error occurred")
        }

    }

    suspend fun cacheArticles(article: Article?){
        val newsEntities = listOf<NewsEntity>(
            NewsEntity(
                title = article?.title.toString(),
                description = article?.description,
                url = article?.url.toString(),
                urlToImage = article?.urlToImage,
                publishedAt = article?.publishedAt.toString(),
                content = article?.content,
                author = article?.author,
            ))

        // Cache the articles in Room database
        articleDao.insertArticles(newsEntities)
    }

    private fun NewsEntity.toArticle(): Article {
        return Article(
            title = this.title,
            description = this.description.toString(),
            url = this.url,
            urlToImage = this.urlToImage.toString(),
            publishedAt = this.publishedAt,
            content = this.content.toString(),
            author = this.author.toString(),
        )
    }

}
