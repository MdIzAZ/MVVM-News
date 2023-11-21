package com.example.mvvmnews.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmnews.R
import com.example.mvvmnews.adapters.NewsAdapter
import com.example.mvvmnews.databinding.FragmentSearchNewsBinding
import com.example.mvvmnews.ui.MainActivity
import com.example.mvvmnews.ui.NewsViewModel
import com.example.mvvmnews.util.Constants
import com.example.mvvmnews.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.example.mvvmnews.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchNewsFragment : Fragment() {
    private lateinit var binding: FragmentSearchNewsBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter


    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchNewsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        setupRecyclerview()

        newsAdapter.set_onItemClick {
            val acttion = SearchNewsFragmentDirections.actionSearchNewsFragmentToArticleFragment2(it)
            findNavController().navigate(acttion)
        }

        var job: Job? = null
        binding.searchBox.addTextChangedListener {
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                it?.let {
                    if (it.toString().isNotEmpty()){
                        viewModel.searchNews(it.toString())
                    }
                }
            }
        }
        viewModel.searchNewsLiveData.observe(viewLifecycleOwner){
            when(it){
                is Resource.Success -> {
                    hideProgressbar()
                    it.data?.let {
                        newsAdapter.differ.submitList(it.articles.toList())
                        val totalPages = it.totalResults/ Constants.QUERY_PAGE_SIZE +2
                        isLastPage = viewModel.searchPageNum == totalPages
                    }
                }
                is Resource.Error ->{
                    hideProgressbar()
                    it.message?.let {
                        Toast.makeText(context, "No Iternet Connection !", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading ->{
                    showProgressbar()
                    it.message?.let {
                        Log.d("n",it)
                    }
                }
            }
        }


    }

    private fun hideProgressbar() {
        binding.progressBar.visibility = View.INVISIBLE
        isLoading = false
    }
    private fun showProgressbar() {
        binding.progressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun setupRecyclerview(){
        newsAdapter = NewsAdapter()
        binding.rcv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = newsAdapter
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }

    }


    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener(){

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount  = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoading_NotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition+ visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition>=0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoading_NotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible
                    && isScrolling

            if(shouldPaginate){
                viewModel.searchNews(binding.searchBox.text.toString())
                isScrolling = false
            }
        }
    }
}