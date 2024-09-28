package com.example.weatherforcast.home.repo

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforcast.db.LocalDataSource
import com.example.weatherforcast.home.ApiWeatherResponse
import com.example.weatherforcast.pojo.current_weather.Clouds
import com.example.weatherforcast.pojo.current_weather.Coord
import com.example.weatherforcast.pojo.current_weather.Main
import com.example.weatherforcast.pojo.current_weather.Sys
import com.example.weatherforcast.pojo.current_weather.Weather
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.pojo.current_weather.Wind
import com.example.weatherforcast.pojo.days_weather.City
import com.example.weatherforcast.pojo.days_weather.DaysWeatherResponse
import com.example.weatherforcast.pojo.days_weather.Rain
import com.example.weatherforcast.pojo.days_weather.State
import com.example.weatherforcast.remote.RemoteDataSource
import com.example.weatherforcast.utils.ConnectivityHelper
import com.example.weatherforcast.utils.Result
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeRepositoryImplTest {

    private lateinit var homeRepository: HomeRepositoryImpl
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var localDataSource: LocalDataSource
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        remoteDataSource = FakeRemoteDataSource()
        localDataSource = FakeLocalDataSource()
        homeRepository = HomeRepositoryImpl(remoteDataSource, localDataSource, context)
    }

    @Test
    fun `test getCurrentWeather when internet is available and remote fetch is successful`() = runTest {
        ConnectivityHelper.setInternetAvailability(true)
        val lat = 30.0
        val lon = 50.0
        (remoteDataSource as FakeRemoteDataSource).setCurrentWeather(ApiWeatherResponse.testWeatherResponse)

        val resultFlow = homeRepository.getCurrentWeather(lat, lon)

        resultFlow.collect { result ->
            assertThat(result, `is`(instanceOf(Result.Success::class.java)))
            val weatherResponse = (result as Result.Success).data
            assertThat(weatherResponse, notNullValue())
            assertThat(weatherResponse?.name, `is`("Test City"))
        }
    }

    @Test
    fun `test getCurrentWeather when internet is available but remote fetch fails`() = runTest {
        ConnectivityHelper.setInternetAvailability(true)

        (remoteDataSource as FakeRemoteDataSource).setShouldFail(true)

        val lat = 30.0
        val lon = 50.0
        val resultFlow = homeRepository.getCurrentWeather(lat, lon)

        resultFlow.collect { result ->
            assertThat(result, `is`(instanceOf(Result.Error::class.java)))
        }
    }
    fun `test getCurrentWeather when no internet and local fetch is successful`() = runTest {
        ConnectivityHelper.setInternetAvailability(false)

        val lat = 30.0
        val lon = 50.0


        (localDataSource as FakeLocalDataSource).addCurrentWeather(ApiWeatherResponse.testWeatherResponse)

        val resultFlow = homeRepository.getCurrentWeather(lat, lon)

        resultFlow.collect { result ->
            assertThat(result, `is`(instanceOf(Result.Success::class.java)))
            val weatherResponse = (result as Result.Success).data
            assertThat(weatherResponse, notNullValue())
            assertThat(weatherResponse?.name, `is`("Test City"))
        }
    }

    @Test
    fun `test getDaysWeather when internet is available and remote fetch is successful`() = runTest {
        ConnectivityHelper.setInternetAvailability(true)
        val lat = 30.0
        val lon = 50.0
        (remoteDataSource as FakeRemoteDataSource).setDaysWeather(ApiWeatherResponse.testDaysWeatherResponse)

        val resultFlow = homeRepository.getDaysWeather(lat, lon)

        resultFlow.collect { result ->
            assertThat(result, `is`(instanceOf(Result.Success::class.java)))
            val daysWeatherResponse = (result as Result.Success).data
            assertThat(daysWeatherResponse, notNullValue())
            assertThat(daysWeatherResponse?.city?.name, `is`("Test City"))
        }
    }
    @Test
    fun `test getDaysWeather when internet is available but remote fetch fails`() = runTest {
        ConnectivityHelper.setInternetAvailability(true)

        (remoteDataSource as FakeRemoteDataSource).setShouldFail(true)

        val lat = 30.0
        val lon = 50.0
        val resultFlow = homeRepository.getDaysWeather(lat, lon)

        resultFlow.collect { result ->
            assertThat(result, `is`(instanceOf(Result.Error::class.java)))
        }
    }

    @Test
    fun `test addCurrentWeather stores weather data locally`() = runTest {


        homeRepository.addCurrentWeather(ApiWeatherResponse.testWeatherResponse)

        val savedWeather = (localDataSource as FakeLocalDataSource).getStoredWeather()
        assertThat(savedWeather, `is`(ApiWeatherResponse.testWeatherResponse))
    }

    @Test
    fun `test addDaysWeather stores days weather data locally`() = runTest {

        homeRepository.addDaysWeather(ApiWeatherResponse.testDaysWeatherResponse)

        val savedDaysWeather = (localDataSource as FakeLocalDataSource).getStoredDaysWeather()
        assertThat(savedDaysWeather, `is`(ApiWeatherResponse.testDaysWeatherResponse))
    }
}
