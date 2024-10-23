package com.example.newsapplication.di

import android.app.Application
import androidx.room.Room
import com.example.newsapplication.api.NewsAPIService
import com.example.newsapplication.data.database.NewsDao
import com.example.newsapplication.data.database.NewsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideNewsApiService(): NewsAPIService {
        return Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsAPIService::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(app: Application): NewsDatabase {
        return Room.databaseBuilder(app, NewsDatabase::class.java, "news_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsDao(db: NewsDatabase): NewsDao {
        return db.articleDao()
    }
}