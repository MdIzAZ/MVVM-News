package com.example.mvvmnews.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.mvvmnews.R
import com.example.mvvmnews.databinding.ActivityMainBinding
import com.example.mvvmnews.db.NewsDB
import com.example.mvvmnews.repositories.NewsRepository

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //
        val repository = NewsRepository(NewsDB.getInstance(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(repository, application)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[NewsViewModel::class.java]
        //
        val btmNav = binding.bottomNavigationView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        btmNav.setupWithNavController(navHostFragment.navController)

    }

}