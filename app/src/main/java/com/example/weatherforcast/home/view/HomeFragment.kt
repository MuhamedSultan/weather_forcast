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
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.weatherforcast.R
import com.example.weatherforcast.databinding.FragmentHomeBinding
import com.example.weatherforcast.home.repo.HomeRepository
import com.example.weatherforcast.home.repo.HomeRepositoryImpl
import com.example.weatherforcast.home.viewmodel.HomeViewModel
import com.example.weatherforcast.home.viewmodel.HomeViewModelFactory
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
                    getAddressFromLocation(location)
                }
            }
        }
        homeViewModel.weatherResult.observe(viewLifecycleOwner) { weatherResult ->
            when (weatherResult) {
                is Result.Success -> {
                    weatherResult.data?.let { result ->
                        showData(result)
                    }
                }

                is Result.Error -> {
                    showError(weatherResult.message)
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
            .load("https://openweathermap.org/img/wn/10d@2x.png")
            .into(binding.tempImage)
        binding.dataTv.text=getCurrentDate()

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
        val locationRequest = LocationRequest.Builder(0).apply {
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMMM EEEE", Locale.ENGLISH)
        return currentDate.format(formatter)
    }

}

