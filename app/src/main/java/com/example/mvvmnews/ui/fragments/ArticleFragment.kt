package com.example.mvvmnews.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.example.mvvmnews.R
import com.example.mvvmnews.databinding.FragmentArticleBinding
import com.example.mvvmnews.ui.MainActivity
import com.example.mvvmnews.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar


class ArticleFragment : Fragment() {
    private lateinit var binding: FragmentArticleBinding
    private lateinit var viewModel: NewsViewModel
    val args: ArticleFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticleBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel

        val article = args.Article

        binding.webview.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url.toString())
        }

        binding.fab.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view, "Article saved Successfully", Snackbar.ANIMATION_MODE_SLIDE).show()
        }

    }
}