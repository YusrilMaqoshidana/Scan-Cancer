package com.dicoding.asclepius.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.data.Results
import com.dicoding.asclepius.data.local.room.HistoryDao
import com.dicoding.asclepius.data.remote.model.ArticlesItem
import com.dicoding.asclepius.data.remote.retrofit.ApiConfig
import com.dicoding.asclepius.data.remote.retrofit.ApiService

class NewsRepository private constructor(private val apiService: ApiService) {
    fun getAllNews(): LiveData<Results<List<ArticlesItem>>> = liveData {
        emit(Results.Loading)
        val q = "cancer"
        val category = "health"
        val language = "en"
        val apiKey = BuildConfig.API_KEY
        try {
            val response = apiService.getArticle(q, category, language, apiKey)
            val news = response.articles
            val newsList = news.map {
                ArticlesItem.Builder()
                    .urlToImage(it?.urlToImage.toString())
                    .title(it?.title)
                    .author(it?.author.toString())
                    .url(it?.url)
                    .publishedAt(it?.publishedAt)
                    .description(it?.description).build()
            }.filter {
                it.title != "[Removed]" && it.urlToImage != null && it.url != null
            }
            emit(Results.Success(newsList))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }
    companion object {
        @Volatile
        private var instance: NewsRepository? = null

        fun getInstance(apiService: ApiService): NewsRepository =
            instance ?: synchronized(this) {
                instance ?: NewsRepository(apiService)
            }.also { instance = it }
    }
}