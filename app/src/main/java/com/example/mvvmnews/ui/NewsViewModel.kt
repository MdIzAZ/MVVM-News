package com.example.mvvmnews.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmnews.NewsApplication
import com.example.mvvmnews.models.Article
import com.example.mvvmnews.models.ArticleList
import com.example.mvvmnews.repositories.NewsRepository
import com.example.mvvmnews.util.Resource
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class NewsViewModel(private val newsRepository: NewsRepository, app: Application): AndroidViewModel(app) {
    //breaking news
    val breakingNewsLiveData: MutableLiveData<Resource<ArticleList>> = MutableLiveData()
    var pageNum = 1
    private var breakingNewsResponse: ArticleList? = null
    //search
    val searchNewsLiveData: MutableLiveData<Resource<ArticleList>> = MutableLiveData()
    var searchPageNum = 1
    private var searchNewsResponse: ArticleList? = null


    init {
        getBreakingNews("in")
    }
    fun getBreakingNews(country: String) = viewModelScope.launch {
        safeBreakingNewsCall(country)
    }

    fun searchNews(sq: String) = viewModelScope.launch {
        safeSearchNewsCall(sq)
    }


    private fun handleResponse(response: Response<ArticleList>) : Resource<ArticleList>{
        if(response.isSuccessful){
            response.body()?.let {
                pageNum++
                if(breakingNewsResponse == null){
                    breakingNewsResponse = it
                } else {
                    val oldList = breakingNewsResponse?.articles
                    val newList = it.articles
                    oldList?.addAll(newList)
                }
                return Resource.Success(breakingNewsResponse?: it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<ArticleList>) : Resource<ArticleList>{
        if(response.isSuccessful){
            response.body()?.let {
                searchPageNum++
                if(searchNewsResponse == null){
                    searchNewsResponse = it
                } else {
                    val old = searchNewsResponse?.articles
                    val new = it.articles
                    old?.addAll(new)
                }
                return Resource.Success(searchNewsResponse?: it)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews() = newsRepository.getAllSavedArticles()

    fun deleteArticles(article: Article) = viewModelScope.launch {
        newsRepository.delete(article)
    }

    private suspend fun safeBreakingNewsCall(country: String){
        breakingNewsLiveData.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = newsRepository.getBreakingNews(country, pageNum)
                breakingNewsLiveData.postValue(handleResponse(response))
            } else {
                breakingNewsLiveData.postValue(Resource.Error("No Internet Connection"))
            }

        } catch (t: Throwable){
            when(t){
                is IOException -> breakingNewsLiveData.postValue(Resource.Error("Network Failure"))
                else -> breakingNewsLiveData.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeSearchNewsCall(sq: String){
        searchNewsLiveData.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = newsRepository.searchNews(sq, searchPageNum)
                searchNewsLiveData.postValue(handleSearchNewsResponse(response))
            } else {
                searchNewsLiveData.postValue(Resource.Error("No Internet Connection"))
            }

        } catch (t: Throwable){
            when(t){
                is IOException -> searchNewsLiveData.postValue(Resource.Error("Network Failure"))
                else -> searchNewsLiveData.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }




}