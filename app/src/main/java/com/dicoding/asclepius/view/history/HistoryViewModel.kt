package com.dicoding.asclepius.view.history

import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.local.model.HistoryEntity
import com.dicoding.asclepius.data.repository.HistoryRepository

class HistoryViewModel(private val historyRepository: HistoryRepository) : ViewModel() {
    fun getHistories() = historyRepository.getHistories()
    fun addHistories(history: HistoryEntity) = historyRepository.addHistories(history)
}