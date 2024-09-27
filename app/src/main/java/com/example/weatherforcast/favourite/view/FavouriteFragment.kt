package com.example.weatherforcast.favourite.view

import RemoteDataSourceImpl
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherforcast.R
import com.example.weatherforcast.databinding.FragmentFavouriteBinding
import com.example.weatherforcast.db.LocalDataSourceImpl
import com.example.weatherforcast.db.PreferencesManager
import com.example.weatherforcast.db.WeatherDatabase
import com.example.weatherforcast.favourite.repo.FavouriteRepositoryImpl
import com.example.weatherforcast.favourite.viewmodel.FavouriteViewModel
import com.example.weatherforcast.favourite.viewmodel.FavouriteViewModelFactory
import com.example.weatherforcast.home.view.DaysAdapter
import com.example.weatherforcast.home.view.HoursAdapter
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.pojo.days_weather.State
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class FavouriteFragment : Fragment(), OnMapReadyCallback, OnFavouriteClick {

    private lateinit var binding: FragmentFavouriteBinding
    private lateinit var mMap: GoogleMap
    private lateinit var favouriteViewModel: FavouriteViewModel
    private lateinit var preferencesManager: PreferencesManager
    private val TAG = "favouriteTag"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val weatherDao = WeatherDatabase.getInstance(requireContext())?.weatherDao()
        val localDataSource = LocalDataSourceImpl(weatherDao!!)
        val remoteDataSource = RemoteDataSourceImpl()
        val favouriteRepository = FavouriteRepositoryImpl(remoteDataSource, localDataSource)
        val factory = FavouriteViewModelFactory(favouriteRepository)
        favouriteViewModel = ViewModelProvider(this, factory)[FavouriteViewModel::class.java]
        preferencesManager = PreferencesManager(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)?.supportActionBar?.show()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.itemDetails.visibility == View.VISIBLE) {
                        binding.itemDetails.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.floatingActionButton.visibility = View.VISIBLE
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            })
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                favouriteViewModel.weatherResult.collect { result ->
                    result.data?.let { weatherResponse ->
                        favouriteViewModel.addLocationToFavourite(weatherResponse)
                        showData(weatherResponse)

                    }
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                favouriteViewModel.daysWeatherResult.collect { daysResult ->
                    daysResult.data?.let { daysWeatherResponse ->
                        showTodayWeather(daysWeatherResponse.list)
                        showNextDaysWeather(daysWeatherResponse.list)
                    }
                }

            }
        }


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                favouriteViewModel.favouritePlaces.collect { favourites ->
                    setupFavouriteRecyclerview(favourites ?: emptyList())
                }
            }
        }
        favouriteViewModel.getFavouritePlaces()

        binding.floatingActionButton.setOnClickListener {
            binding.favouriteMap.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
            binding.floatingActionButton.visibility = View.GONE
            val mapFragment =
                childFragmentManager.findFragmentById(R.id.favourite_map) as SupportMapFragment
            mapFragment.getMapAsync(this)

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
        binding.windTv.text = String.format("%.2f %s", windSpeedValue, windSpeedUnit)
        binding.humidityTv.text = "${result.main.humidity}%"
        binding.rainTv.text = "${String.format("%.1f", result.rain?.`1h` ?: 0.0)} mm"
        binding.cityTv.text = result.name
        binding.dataTv.text = getCurrentDate()
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


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val defaultLocation = LatLng(30.0444, 31.2357)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 5f))

        mMap.setOnMapClickListener { latLng ->
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            val latitude = latLng.latitude
            val longitude = latLng.longitude

            favouriteViewModel.getCurrentWeather(latitude, longitude)
            favouriteViewModel.getDaysWeather(latitude, longitude)
            binding.favouriteMap.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            binding.floatingActionButton.visibility = View.VISIBLE
        }
    }

    private fun setupFavouriteRecyclerview(weather: List<WeatherResponse>) {
        val tempUnit = preferencesManager.getSelectedOption(PreferencesManager.KEY_TEMP_UNIT, "K")
        val favouriteAdapter = FavouriteAdapter(weather, tempUnit.toString(), this)
        binding.recyclerView.apply {
            adapter = favouriteAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemFavouriteClick(weatherResponse: WeatherResponse) {
        binding.itemDetails.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.floatingActionButton.visibility = View.GONE

        favouriteViewModel.getCurrentWeather(weatherResponse.coord.lat, weatherResponse.coord.lon)
        favouriteViewModel.getDaysWeather(weatherResponse.coord.lat, weatherResponse.coord.lon)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                favouriteViewModel.weatherResult.collect {
                    showData(weatherResponse)
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                favouriteViewModel.daysWeatherResult.collect {
                    it.data?.let { data ->
                        showTodayWeather(data.list)
                        showNextDaysWeather(data.list)
                    }
                }
            }
        }
    }

    override fun onDeleteItemFavouriteClick(weatherResponse: WeatherResponse) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Favourite")
        builder.setMessage("Are you sure you want to delete this place from your favourites?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            favouriteViewModel.deleteLocationFromFavourite(weatherResponse)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
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