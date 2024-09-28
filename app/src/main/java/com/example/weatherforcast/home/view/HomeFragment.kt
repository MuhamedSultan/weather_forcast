package com.example.weatherforcast.home.view

import RemoteDataSourceImpl
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import com.example.weatherforcast.utils.Result
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherforcast.R
import com.example.weatherforcast.databinding.FragmentHomeBinding
import com.example.weatherforcast.db.LocalDataSourceImpl
import com.example.weatherforcast.db.PreferencesManager
import com.example.weatherforcast.db.WeatherDatabase
import com.example.weatherforcast.home.repo.HomeRepository
import com.example.weatherforcast.home.repo.HomeRepositoryImpl
import com.example.weatherforcast.home.viewmodel.HomeViewModel
import com.example.weatherforcast.home.viewmodel.HomeViewModelFactory
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.pojo.days_weather.State
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val MY_LOCATION_PERMISSION_ID = 5005
    private val TAG = "homeTag"
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dao = WeatherDatabase.getInstance(requireContext())?.weatherDao()
        val localDataSource = LocalDataSourceImpl(dao!!)
        val remoteDataSource = RemoteDataSourceImpl()
        val repository = HomeRepositoryImpl(remoteDataSource, localDataSource,requireContext())
        val factory = HomeViewModelFactory(repository)
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)?.supportActionBar?.show()

        preferencesManager = PreferencesManager(requireContext())


        binding.dataTv.text = getCurrentDate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    Log.i(TAG, "Location: ${location.latitude}, ${location.longitude}")
                    if (preferencesManager.getSavedLocation() == "Gps") {
                        homeViewModel.getCurrentWeather(location.latitude, location.longitude)
                        homeViewModel.getDaysWeather(location.latitude, location.longitude)
                        getAddressFromLocation(location.latitude, location.longitude)
                    } else {
                        val lat =
                            preferencesManager.getSavedLatitude()?.toDouble() ?: location.latitude
                        val lon =
                            preferencesManager.getSavedLongitude()?.toDouble() ?: location.longitude
                        homeViewModel.getCurrentWeather(lat, lon)
                        homeViewModel.getDaysWeather(lat, lat)
                        getAddressFromLocation(lat, lon)
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.weatherResult.collect { weatherResult ->
                    when (weatherResult) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.layoutGroup.visibility = View.VISIBLE
                            weatherResult.data?.let { result ->
                                showData(result)
                                homeViewModel.addCurrentWeather(result)
                            }
                        }

                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            showError(weatherResult.message)
                        }

                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.daysWeatherResult.collect { weatherResult ->
                    when (weatherResult) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.layoutGroup.visibility = View.VISIBLE
                            weatherResult.data?.let { result ->
                                showTodayWeather(result.list)
                                showNextDaysWeather(result.list)
                                homeViewModel.addDaysCurrentWeather(result)

                            }
                        }

                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            showError(weatherResult.message)

                        }
                    }
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (checkPermissions()) {
            getLocation()
        } else {
            requestLocationPermissions()
        }
    }


    private fun showError(message: String?) {
        Snackbar.make(requireView(), message.toString(), Snackbar.LENGTH_SHORT).show()

    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            MY_LOCATION_PERMISSION_ID
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val locationRequest = LocationRequest.Builder(60000).apply {
            setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        }.build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        if (!isAdded) {
            return
        }
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses: MutableList<Address>? =
                geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses!!.isNotEmpty()) {
                val address: Address = addresses[0]
                val addressString: String = address.adminArea ?: "Unknown Area Name"
                binding.cityTv.text = addressString
                Log.i(TAG, "Address: $addressString")
            } else {
                Log.e(TAG, "No address found for location")
            }
        } catch (e: IOException) {
            Log.e(TAG, "Geocoder failed", e)
        }
    }

    private fun setupHoursRecyclerview(state: List<State>) {
        val hoursAdapter = HoursAdapter(requireContext(), state)
        val hoursLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.hoursRv.apply {
            adapter = hoursAdapter
            layoutManager = hoursLayoutManager
        }
    }

    private fun setupDaysRecyclerview(state: List<State>) {
        val daysAdapter = DaysAdapter(requireContext(), state)
        val hoursLayoutManager =
            LinearLayoutManager(requireContext())
        binding.daysRv.apply {
            adapter = daysAdapter
            layoutManager = hoursLayoutManager
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showData(result: WeatherResponse) {
        val tempUnit = preferencesManager.getSelectedOption(PreferencesManager.KEY_TEMP_UNIT, "K")
        val windSpeedUnit =
            preferencesManager.getSelectedOption(PreferencesManager.KEY_WIND_SPEED_UNIT, "Km/h")

        val tempValue = convertTemperature(result.main.temp, tempUnit ?: "Celsius")

        val windSpeedValue = convertWindSpeed(result.wind.speed, windSpeedUnit ?: "Km/h")


        val tempUnitSymbol = when (tempUnit) {
            "Celsius" -> "°C"
            "Fahrenheit" -> "°F"
            "K" -> "°K"
            else -> "°K"
        }

        binding.tempTv.text = String.format("%.2f %s", tempValue, tempUnitSymbol)
        binding.tempDesc.text = result.weather[0].description
        binding.windTv.text = String.format("%.2f %s", windSpeedValue, windSpeedUnit)
        binding.humidityTv.text = "${result.main.humidity}%"
        binding.rainTv.text = "${String.format("%.1f", result.rain?.`1h` ?: 0.0)} mm"
        binding.cloudTv.text = "${result.clouds.all} %"
        binding.pressureTv.text = "${result.main.pressure} hpa"
        binding.visibilityTv.text = "${result.visibility} m"
        Glide.with(requireContext())
            .load("https://openweathermap.org/img/wn/${result.weather[0].icon}@2x.png")
            .into(binding.tempImage)
    }


    private fun showTodayWeather(list: List<State>) {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val temperatureUnit =
            preferencesManager.getSelectedOption(PreferencesManager.KEY_TEMP_UNIT, "K") ?: "K"

        val limitedWeatherList = list.take(8).map { weather ->
            val time = timeFormat.format(inputFormat.parse(weather.dt_txt)!!)
            val tempKelvin = weather.main.temp
            val convertedTemperature = convertTemperature(tempKelvin, temperatureUnit)
            weather.copy(dt_txt = time, main = weather.main.copy(temp = convertedTemperature))
        }

        setupHoursRecyclerview(limitedWeatherList)
    }


    private fun showNextDaysWeather(list: List<State>) {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val temperatureUnit =
            preferencesManager.getSelectedOption(PreferencesManager.KEY_TEMP_UNIT, "K") ?: "K"

        val weatherPerDay = mutableMapOf<String, State>()

        list.forEach { weather ->
            val date = inputFormat.parse(weather.dt_txt)!!
            val dayName = dayFormat.format(date)

            if (!weatherPerDay.containsKey(dayName)) {
                val tempKelvin = weather.main.temp
                val convertedTemperature = convertTemperature(tempKelvin, temperatureUnit)

                weatherPerDay[dayName] = weather.copy(
                    dt_txt = dayName,
                    main = weather.main.copy(temp = convertedTemperature)
                )
            }
        }

        val upcomingWeatherList = weatherPerDay.map { it.value }.toList()

        setupDaysRecyclerview(upcomingWeatherList)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMMM EEEE", Locale.ENGLISH)
        return currentDate.format(formatter)
    }

    private fun convertTemperature(tempKelvin: Double, unit: String): Double {
        return when (unit) {
            "Celsius" -> tempKelvin - 273.15
            "Fahrenheit" -> (tempKelvin - 273.15) * 9 / 5 + 32
            else -> tempKelvin
        }
    }

    private fun convertWindSpeed(windSpeed: Double, unit: String): Double {
        return when (unit) {
            "km/h" -> windSpeed
            "m/s" -> windSpeed * 3.6
            else -> windSpeed
        }
    }


}

