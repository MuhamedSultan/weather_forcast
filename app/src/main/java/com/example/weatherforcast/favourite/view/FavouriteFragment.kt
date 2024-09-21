package com.example.weatherforcast.favourite.view

import RemoteDataSourceImpl
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforcast.R
import com.example.weatherforcast.databinding.FragmentFavouriteBinding
import com.example.weatherforcast.db.LocalDataSourceImpl
import com.example.weatherforcast.db.WeatherDatabase
import com.example.weatherforcast.favourite.repo.FavouriteRepositoryImpl
import com.example.weatherforcast.favourite.viewmodel.FavouriteViewModel
import com.example.weatherforcast.favourite.viewmodel.FavouriteViewModelFactory
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class FavouriteFragment : Fragment(), OnMapReadyCallback ,OnFavouriteClick{

    private lateinit var binding: FragmentFavouriteBinding
    private lateinit var mMap: GoogleMap
    private lateinit var favouriteViewModel: FavouriteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val weatherDao = WeatherDatabase.getInstance(requireContext())?.favouriteLocationDao()
        val localDataSource = LocalDataSourceImpl(weatherDao!!)
        val remoteDataSource = RemoteDataSourceImpl()
        val favouriteRepository = FavouriteRepositoryImpl(remoteDataSource, localDataSource)
        val factory = FavouriteViewModelFactory(favouriteRepository)
        favouriteViewModel = ViewModelProvider(this, factory)[FavouriteViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.favourite_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.floatingActionButton.setOnClickListener {
            binding.favouriteMap.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
            binding.floatingActionButton.visibility = View.GONE
        }
        favouriteViewModel.getFavouritePlaces()

        favouriteViewModel.favouritePlaces.observe(viewLifecycleOwner) { favourites ->
            setupRecyclerview(favourites)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val defaultLocation = LatLng(-34.0, 151.0)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 5f))

        mMap.setOnMapClickListener { latLng ->
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            val latitude = latLng.latitude
            val longitude = latLng.longitude

            favouriteViewModel.getCurrentWeather(latitude, longitude)
            favouriteViewModel.weatherResult.observe(viewLifecycleOwner) { result ->
                result.data?.let { weatherResponse ->
                    favouriteViewModel.addLocationToFavourite(weatherResponse)
                    favouriteViewModel.getFavouritePlaces()
                }
            }

            binding.favouriteMap.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            binding.floatingActionButton.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerview(weather: List<WeatherResponse>) {
        val favouriteAdapter = FavouriteAdapter(weather,this)
        binding.recyclerView.apply {
            adapter = favouriteAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        favouriteAdapter.updateData(weather)
    }

    override fun onItemFavouriteClick(weatherResponse: WeatherResponse) {

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

}
