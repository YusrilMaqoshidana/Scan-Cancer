package com.dicoding.asclepius.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.asclepius.data.Results
import com.dicoding.asclepius.data.local.model.HistoryEntity
import com.dicoding.asclepius.data.local.room.HistoryDao

class HistoryRepository private constructor(private val historyDao: HistoryDao){

    fun getHistories() : LiveData<Results<List<HistoryEntity>>> = liveData {
        emit(Results.Loading)
        try {
            val histories = historyDao.getHistories()
            emit(Results.Success(histories))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun addHistories(history: HistoryEntity) : LiveData<Results<Boolean>> = liveData {
        emit(Results.Loading)
        try {
            historyDao.insertHistory(history)
            emit(Results.Success(true))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: HistoryRepository? = null
        fun getInstance(
            historyDao: HistoryDao,
        ): HistoryRepository = instance ?: synchronized(this) {
            instance ?: HistoryRepository(historyDao)
        }.also { instance = it }
    }


}