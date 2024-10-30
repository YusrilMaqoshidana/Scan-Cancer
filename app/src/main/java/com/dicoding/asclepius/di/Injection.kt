package com.dicoding.asclepius.di

import android.content.Context
import com.dicoding.asclepius.data.local.room.HistoryDatabase
import com.dicoding.asclepius.data.repository.HistoryRepository

object Injection {
    fun provideHistoryRepository(context: Context): HistoryRepository {
        val database = HistoryDatabase.getDatabase(context)
        val dao = database.historyDao()
        return HistoryRepository.getInstance(dao)
    }
}