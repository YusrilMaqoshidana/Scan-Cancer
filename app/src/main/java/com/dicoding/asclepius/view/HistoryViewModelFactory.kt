package com.dicoding.asclepius.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.data.repository.HistoryRepository
import com.dicoding.asclepius.di.ClassInjection
import com.dicoding.asclepius.view.history.HistoryViewModel

class HistoryViewModelFactory(private val historyRepository: HistoryRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(historyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: HistoryViewModelFactory? = null

        fun getInstance(context: Context): HistoryViewModelFactory =
            instance ?: synchronized(this) {
                instance
                    ?: HistoryViewModelFactory(ClassInjection.provideHistoryRepository(context)).also {
                        instance = it
                    }
            }
    }
}