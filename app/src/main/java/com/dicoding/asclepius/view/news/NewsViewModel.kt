package com.dicoding.asclepius.view.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.data.remote.model.ArticlesItem
import com.dicoding.asclepius.data.remote.retrofit.ApiConfig

class NewsViewModel : ViewModel() {
    private val apiKey = BuildConfig.API_KEY
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    val newsArticles: LiveData<List<ArticlesItem>> = liveData {
        _loading.value = true
        emit(fetchNewsArticles())
        _loading.value = false
    }

    private suspend fun fetchNewsArticles(): List<ArticlesItem> {
        return try {
            val response = ApiConfig.getApiService().getArticle(apiKey = apiKey)
            if (response.articles.isNotEmpty()) {
                val filteredArticles = response.articles.filterNotNull().filter {
                    !it.title!!.contains("[Removed]", ignoreCase = true)
                }
                filteredArticles
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
