package com.dicoding.asclepius.di

import android.content.Context
import com.dicoding.asclepius.data.local.room.HistoryDatabase
import com.dicoding.asclepius.data.remote.retrofit.ApiConfig
import com.dicoding.asclepius.data.repository.HistoryRepository
import com.dicoding.asclepius.data.repository.NewsRepository

object ClassInjection {
    fun provideHistoryRepository(context: Context): HistoryRepository {
        val database = HistoryDatabase.getDatabase(context)
        val dao = database.historyDao()
        return HistoryRepository.getInstance(dao)
    }
}