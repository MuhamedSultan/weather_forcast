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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val remoteDataSource = RemoteDataSourceImpl()
        val repository = HomeRepositoryImpl(remoteDataSource)
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


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    Log.i(TAG, "Location: ${location.latitude}, ${location.longitude}")
                    homeViewModel.getCurrentWeather(location.latitude, location.longitude)
                    homeViewModel.getDaysWeather(location.latitude, location.longitude)
                    getAddressFromLocation(location)
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
                                showTodayWeather2(result.list)
                                showNextDaysWeather(result.list)

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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showData(result: WeatherResponse) {
        val tempKelvin = result.main.temp
        val tempCelsius = tempKelvin - 273.15
        binding.tempTv.text = "%.2f °C".format(tempCelsius)

        Glide.with(requireContext())
            .load("https://openweathermap.org/img/wn/${result.weather[0].icon}@2x.png")
            .into(binding.tempImage)
        binding.dataTv.text = getCurrentDate()

        val windSpeed = result.wind.speed
        val humidity = result.main.humidity
        val rain = result.rain?.`1h` ?: 0.0

        val windSpeedText = "%.2f m/s".format(windSpeed)
        val humidityText = "%d%%".format(humidity)
        val rainText = "%.2f mm".format(rain)

        binding.windTv.text = windSpeedText
        binding.humidityTv.text = humidityText
        binding.rainTv.text = rainText

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

    private fun getAddressFromLocation(location: Location) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses: MutableList<Address>? =
                geocoder.getFromLocation(location.latitude, location.longitude, 1)

            if (addresses!!.isNotEmpty()) {
                val address: Address = addresses[0]
                val addressString: String? = address.adminArea
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

    private fun showTodayWeather(list: List<State>) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val todayWeather = list.filter { it.dt_txt.startsWith(currentDate) }
        println("Today's Weather (every 3 hours):")
        todayWeather.forEach { weather ->
            val time = inputFormat.parse(weather.dt_txt)?.let { timeFormat.format(it) }
            println("Time: $time, Temp: ${weather.main.temp}°K, ${weather.weather[0].description}")
            setupHoursRecyclerview(todayWeather)
        }
    }


    private fun showTodayWeather2(list: List<State>) {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val limitedWeatherList = list.take(8).map { weather ->
            val time = timeFormat.format(inputFormat.parse(weather.dt_txt)!!)
            weather.copy(dt_txt = time)
        }
        setupHoursRecyclerview(limitedWeatherList)
    }


    private fun showNextDaysWeather(list: List<State>) {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())

        val weatherPerDay = mutableMapOf<String, State>()

        list.forEach { weather ->
            val date = inputFormat.parse(weather.dt_txt)!!
            val dayName = dayFormat.format(date)

            if (!weatherPerDay.containsKey(dayName)) {
                weatherPerDay[dayName] = weather.copy(dt_txt = dayName)
            }
        }
        val upcomingWeatherList = weatherPerDay.map { (dayName, state) ->
            state.copy(dt_txt = dayName)
        }.toList()
        setupDaysRecyclerview(upcomingWeatherList)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMMM EEEE", Locale.ENGLISH)
        return currentDate.format(formatter)
    }

}

