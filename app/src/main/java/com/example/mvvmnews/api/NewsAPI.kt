package com.example.mvvmnews.api

import com.example.mvvmnews.models.ArticleList
import com.example.mvvmnews.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        code:String = "in",
        @Query("page")
        no:Int = 1,
        @Query("apiKey")
        key:String = API_KEY

    ): Response<ArticleList>

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        sq:String,
        @Query("page")
        no:Int = 1,
        @Query("apiKey")
        key:String = API_KEY

    ): Response<ArticleList>
}