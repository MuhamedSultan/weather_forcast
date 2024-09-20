package com.example.weatherforcast.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.home.repo.HomeRepository
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.pojo.days_weather.DaysWeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.weatherforcast.utils.Result

class HomeViewModel(private val homeRepository: HomeRepository) : ViewModel() {

    private val _weatherResult: MutableLiveData<Result<WeatherResponse>> = MutableLiveData()
    val weatherResult: LiveData<Result<WeatherResponse>> = _weatherResult

    private val _daysWeatherResult: MutableLiveData<Result<DaysWeatherResponse>> = MutableLiveData()
    val daysWeatherResult: LiveData<Result<DaysWeatherResponse>> = _daysWeatherResult

    fun getCurrentWeather(lat: Double, lon: Double) = viewModelScope.launch(Dispatchers.IO) {
        val result = homeRepository.getCurrentWeather(lat, lon)
        _weatherResult.postValue(result)
    }

    fun getDaysWeather(lat: Double, lon: Double) = viewModelScope.launch(Dispatchers.IO) {
        val result = homeRepository.getDaysWeather(lat, lon)
        _daysWeatherResult.postValue(result)
    }
}
