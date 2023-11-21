package com.example.mvvmnews.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mvvmnews.repositories.NewsRepository

class NewsViewModelProviderFactory(val repository: NewsRepository, val app: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(repository, app) as T
    }
}