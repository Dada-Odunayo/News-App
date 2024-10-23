package com.example.newsapplication.ui.news

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.newsapplication.data.database.NewsEntity
import com.example.newsapplication.data.repository.DataSource
import com.example.newsapplication.data.repository.NewsRepository
import com.example.newsapplication.model.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
): ViewModel(

) {
    private val _newsArticles :MutableStateFlow<List<Article>> = MutableStateFlow(emptyList())
    val newsArticle:StateFlow<List<Article>> = _newsArticles

    private val _uiState:MutableStateFlow<NewsState> = MutableStateFlow(NewsState())
    val uiState:StateFlow<NewsState> = _uiState

    val loadingState = MutableStateFlow<LoadingState>(LoadingState.Empty)
    private var currentOffset = 0
    private val limit = 10
    init {
        fetchNewsLocally()
    }
    fun updateQuery(query:String){
        _uiState.value =_uiState.value.copy(
            query  =  query
        )
    }

    fun resetUI(){
        loadingState.value = LoadingState.Empty
    }

    fun fetchNews() {
        viewModelScope.launch {
            loadingState.value = LoadingState.Loading
            when(val articleDataSource =newsRepository.fetchNews(_uiState.value.query,currentOffset,limit)){
                is DataSource.Error -> {
                    loadingState.value = LoadingState.Error("A Network Error Occurred")
                }
                is DataSource.Success -> {
                    if(articleDataSource.fromNetwork){
                        _newsArticles.value = emptyList()
                        _newsArticles.value = articleDataSource.data
                    }
                    else{
                        _newsArticles.value += articleDataSource.data
                        currentOffset += articleDataSource.data.size
                    }

                    _uiState.value = _uiState.value.copy(
                        fromNetwork = articleDataSource.fromNetwork
                    )
                    loadingState.value = LoadingState.Success
                }
            }

        }
    }

    private fun fetchNewsLocally(){
        loadingState.value = LoadingState.Loading
        viewModelScope.launch {
            when(val articleDataSource = newsRepository.fetchNewsLocally(currentOffset, limit)){
                is DataSource.Success -> {
                    _newsArticles.value += articleDataSource.data
                    currentOffset += articleDataSource.data.size
                    _uiState.value = _uiState.value.copy(
                        fromNetwork = articleDataSource.fromNetwork
                    )
                    loadingState.value = LoadingState.Success
                }
                is DataSource.Error -> {
                    loadingState.value = LoadingState.Error(articleDataSource.message)
                }
            }
        }
    }

    fun updateSelectedArticle(url:String){
        Log.d("selected article","called: $url")
        _uiState.value = _uiState.value.copy(
            selectedArticle = _newsArticles.value.find {
                it.url == url
            }
        )
    }

    fun cacheArticles(){
        viewModelScope.launch {
            newsRepository.cacheArticles(_uiState.value.selectedArticle)
        }
    }


}

data class NewsState(
    val query: String="",
    val fromNetwork:Boolean = false,
    val selectedArticle: Article? = null
)

sealed interface LoadingState{
    object Empty: LoadingState
    object Loading: LoadingState
    data class Error(val error:String): LoadingState
    object Success: LoadingState
}