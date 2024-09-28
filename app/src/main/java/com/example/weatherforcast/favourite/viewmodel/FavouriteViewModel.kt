package com.example.weatherforcast.favourite.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.favourite.repo.FavouriteRepository
import com.example.weatherforcast.pojo.current_weather.Main
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.pojo.days_weather.DaysWeatherResponse
import com.example.weatherforcast.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavouriteViewModel(private val favouriteRepository: FavouriteRepository) : ViewModel() {
    private val _weatherResult: MutableStateFlow<Result<WeatherResponse?>> =
        MutableStateFlow(Result.Loading())
    val weatherResult: StateFlow<Result<WeatherResponse?>> = _weatherResult

    private val _daysWeatherResult: MutableStateFlow<Result<DaysWeatherResponse?>> =
        MutableStateFlow(Result.Loading())
    val daysWeatherResult: StateFlow<Result<DaysWeatherResponse?>> = _daysWeatherResult


    private val _favouritePlaces: MutableStateFlow<List<WeatherResponse>?> = MutableStateFlow(null)
    val favouritePlaces: StateFlow<List<WeatherResponse>?> = _favouritePlaces


    fun getCurrentWeather(lat: Double, lon: Double) = viewModelScope.launch(Dispatchers.IO) {
        favouriteRepository.getCurrentWeather(lat, lon)
            .catch { e ->
                _weatherResult.value = Result.Error(e.message.toString())
            }
            .collect { result ->
                _weatherResult.value = result
            }
    }

    fun getDaysWeather(lat: Double, lon: Double) = viewModelScope.launch(Dispatchers.IO) {
        favouriteRepository.getDaysWeather(lat, lon)
            .catch { e ->
                _daysWeatherResult.value = Result.Error(e.message.toString())
            }
            .collect { result ->
                _daysWeatherResult.value = result
            }
    }

    fun addLocationToFavourite(weatherResponse: WeatherResponse) =
        viewModelScope.launch(Dispatchers.IO) {
            favouriteRepository.addLocationToFavourite(weatherResponse)
            getFavouritePlaces()
        }

    fun getFavouritePlaces() = viewModelScope.launch {
        favouriteRepository.getFavouritePlaces().collect {result->
            _favouritePlaces.value = result
        }

    }

    fun deleteLocationFromFavourite(weatherResponse: WeatherResponse) = viewModelScope.launch {
        favouriteRepository.deleteLocationToFavourite(weatherResponse)
        getFavouritePlaces()
    }

}