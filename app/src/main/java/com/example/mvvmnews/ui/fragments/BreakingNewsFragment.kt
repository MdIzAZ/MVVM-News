package com.example.mvvmnews.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmnews.adapters.NewsAdapter
import com.example.mvvmnews.databinding.FragmentBreakingNewsBinding
import com.example.mvvmnews.ui.MainActivity
import com.example.mvvmnews.ui.NewsViewModel
import com.example.mvvmnews.util.Resource
import com.example.mvvmnews.util.Constants.Companion.QUERY_PAGE_SIZE


class BreakingNewsFragment : Fragment() {
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var binding: FragmentBreakingNewsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBreakingNewsBinding.inflate(inflater)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        setupRecyclerview()

        //on rcv item click
        newsAdapter.set_onItemClick {
            val acttion = BreakingNewsFragmentDirections.actionBreakingNewsFragmentToArticleFragment(it)
            findNavController().navigate(acttion)
        }

        viewModel.breakingNewsLiveData.observe(viewLifecycleOwner){
            when(it){
                is Resource.Success -> {
                    hideProgressbar()
                    it.data?.let {
                        newsAdapter.differ.submitList(it.articles.toList())
                        val totalPages = it.totalResults/ QUERY_PAGE_SIZE+2
                        isLastPage = viewModel.pageNum == totalPages
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
        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = newsAdapter
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object :RecyclerView.OnScrollListener(){

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
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoading_NotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible
                    && isScrolling

            if(shouldPaginate){
                viewModel.getBreakingNews("in")
                isScrolling = false
            }
        }
    }
}





















