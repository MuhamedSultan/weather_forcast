package com.example.weatherforcast.alert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforcast.alert.repo.AlertRepository

class AlertViewModelFactory(private val alertRepository: AlertRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
            return AlertViewModel(alertRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}