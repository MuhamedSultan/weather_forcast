package com.example.weatherforcast.home.view

import RemoteDataSourceImpl
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    Log.i(TAG, "Location: ${location.latitude}, ${location.longitude}")
                    homeViewModel.getCurrentWeather(location.latitude, location.longitude)
                }
            }
        }
        homeViewModel.currentWeather.observe(viewLifecycleOwner) { currentWeather ->
            binding.tempTv.text = currentWeather.main.temp.toString()
            Glide.with(requireContext())
                .load("https://openweathermap.org/img/wn/${currentWeather.weather[0].icon}@2x.png")
                .into(binding.tempImage)
        }

        if (checkPermissions()) {
            getLocation()
        } else {
            requestLocationPermissions()
        }


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
}

