package com.example.mvvmnews.repositories

import com.example.mvvmnews.api.RetrofitInstance
import com.example.mvvmnews.db.NewsDB
import com.example.mvvmnews.db.NewsDao
import com.example.mvvmnews.models.Article

class NewsRepository(val db: NewsDB) {

    suspend fun getBreakingNews(countryCode: String, pageNo: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNo)

    suspend fun searchNews(searchQuery: String, pageNo: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNo)

    suspend fun upsert(article: Article) =
        db.newsDAO().upsert(article)

    suspend fun delete(article: Article) =
        db.newsDAO().delete(article)

    fun getAllSavedArticles() =
        db.newsDAO().getAllArticles()
}