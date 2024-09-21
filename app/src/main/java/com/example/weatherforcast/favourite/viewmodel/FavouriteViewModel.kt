package com.example.weatherforcast.favourite.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.favourite.repo.FavouriteRepository
import com.example.weatherforcast.pojo.current_weather.Main
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavouriteViewModel(private val favouriteRepository: FavouriteRepository) : ViewModel() {
    private val _weatherResult: MutableLiveData<Result<WeatherResponse>> = MutableLiveData()
    val weatherResult: LiveData<Result<WeatherResponse>> = _weatherResult

    private val _favouritePlaces: MutableLiveData<List<WeatherResponse>>  = MutableLiveData()
    val favouritePlaces:  LiveData<List<WeatherResponse>>  = _favouritePlaces


    fun getCurrentWeather(lat: Double, lon: Double) = viewModelScope.launch(Dispatchers.IO) {
        val result = favouriteRepository.getCurrentWeather(lat, lon)
        _weatherResult.postValue(result)
    }

    fun addLocationToFavourite(weatherResponse: WeatherResponse) = viewModelScope.launch(Dispatchers.IO) {
        favouriteRepository.addLocationToFavourite(weatherResponse)
        getFavouritePlaces()
    }

    fun getFavouritePlaces()=viewModelScope.launch {
        val result =favouriteRepository.getFavouritePlaces()
        _favouritePlaces.value=result
    }

}