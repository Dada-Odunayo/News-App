package com.example.newsapplication.data.repository

sealed class DataSource<out T> {
    data class Success<T>(val data: T, val fromNetwork: Boolean) : DataSource<T>()
    data class Error(val message: String) : DataSource<Nothing>()
}
