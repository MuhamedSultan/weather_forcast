package com.example.weatherforcast.favourite.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcast.databinding.FavouriteItemBinding
import com.example.weatherforcast.pojo.current_weather.WeatherResponse

class FavouriteAdapter(
    private var weatherData: List<WeatherResponse>,
    private val tempUnit: String,
    private val favouriteClick: OnFavouriteClick
) : RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = FavouriteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavouriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val weather = weatherData[position]

        holder.placeName.text = weather.name

        val convertedTemp = convertTemperature(weather.main.temp, tempUnit)
        val unitSymbol = getTemperatureSymbol(tempUnit)
        holder.placeTemp.text = String.format("%.2f %s", convertedTemp, unitSymbol)

        holder.deleteItem.setOnClickListener {
            favouriteClick.onDeleteItemFavouriteClick(weather)
        }
        holder.favouriteItem.setOnClickListener {
            favouriteClick.onItemFavouriteClick(weather)
        }
    }

    override fun getItemCount(): Int {
        return weatherData.size
    }

    class FavouriteViewHolder(binding: FavouriteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val placeName = binding.placeName
        val placeTemp = binding.placeTemp
        val deleteItem = binding.deleteItem
        val favouriteItem = binding.favouriteItem
    }

    private fun convertTemperature(tempKelvin: Double, unit: String): Double {
        return when (unit) {
            "Celsius" -> tempKelvin - 273.15
            "Fahrenheit" -> (tempKelvin - 273.15) * 9 / 5 + 32
            else -> tempKelvin
        }
    }

    private fun getTemperatureSymbol(unit: String): String {
        return when (unit) {
            "Celsius" -> "°C"
            "Fahrenheit" -> "°F"
            else -> "K"
        }
    }
}
