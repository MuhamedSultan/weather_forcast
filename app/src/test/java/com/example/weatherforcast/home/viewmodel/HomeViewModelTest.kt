package com.example.weatherforcast.home.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforcast.home.ApiWeatherResponse
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.pojo.days_weather.DaysWeatherResponse
import com.example.weatherforcast.utils.Result
import getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {


    private lateinit var homeViewModel: HomeViewModel
    private lateinit var fakeRepository: FakeHomeRepository

    @Before
    fun setup() {
        fakeRepository = FakeHomeRepository()
        homeViewModel = HomeViewModel(fakeRepository)
    }

    @Test
    fun getCurrentWeather_returnsSuccess() = runTest {
        // when get current weather temp
        homeViewModel.getCurrentWeather(30.0, 50.0)

        //Then assert that weather triggered
        val result = homeViewModel.weatherResult.value
        assertThat(result, not(nullValue()))
    }

    @Test
    fun getDaysWeather_returnsSuccess() = runTest {
        // when get next days weather temp

        homeViewModel.getDaysWeather(30.0, 50.0)

        //Then assert that days weather triggered

        val result = homeViewModel.daysWeatherResult.value
        assertThat(result, not(nullValue()))
    }

    @Test
    fun addCurrentWeatherToLocal() = runTest {
        // when add current weather temp to local database
        val weatherResponse = ApiWeatherResponse.testWeatherResponse
        homeViewModel.addCurrentWeather(weatherResponse)

        //Then assert that weather temp  triggered
        assertThat(fakeRepository.weatherAdded, `is`(weatherResponse))
    }

    @Test
    fun addDaysWeather_callsRepository() = runTest {
        // when add days weather temp to local database
        val daysWeatherResponse = ApiWeatherResponse.testDaysWeatherResponse
        homeViewModel.addDaysCurrentWeather(daysWeatherResponse)

        //Then assert that days weather temp  triggered
        assertThat(fakeRepository.daysWeatherAdded, `is`(daysWeatherResponse))
    }


    @Test
    fun addCurrentWeatherToLocal_whenNullResponse() = runTest {
        // when response it null
        val weatherResponse: WeatherResponse? = null
        weatherResponse?.let { homeViewModel.addCurrentWeather(it) }

        // Then assert the current WeatherAdded  is still null
        assertThat(fakeRepository.weatherAdded, `is`(nullValue()))
    }

    @Test
    fun addDaysWeather_whenNullResponse() = runTest {
        // when response it null
        val daysWeatherResponse: DaysWeatherResponse? = null
        daysWeatherResponse?.let { homeViewModel.addDaysCurrentWeather(it) }

        // Then assert the daysWeatherAdded  is still null
        assertThat(fakeRepository.daysWeatherAdded, `is`(nullValue()))
    }
}
