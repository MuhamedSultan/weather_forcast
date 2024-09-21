package com.example.weatherforcast.favourite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforcast.favourite.repo.FavouriteRepository
import com.example.weatherforcast.home.repo.HomeRepository

class FavouriteViewModelFactory(private val favouriteRepository: FavouriteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavouriteViewModel::class.java)) {
            return FavouriteViewModel(favouriteRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}