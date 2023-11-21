package com.example.mvvmnews.models

import com.example.mvvmnews.models.Article

data class ArticleList(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)