package com.example.weatherforcast.favourite.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.weatherforcast.databinding.FavouriteItemBinding
import com.example.weatherforcast.pojo.current_weather.Main
import com.example.weatherforcast.pojo.current_weather.WeatherResponse

class FavouriteAdapter(private var weatherData:List<WeatherResponse>):Adapter<FavouriteAdapter.FavouriteViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = FavouriteItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FavouriteViewHolder(view)
    }


    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
       val weather=weatherData[position]
        holder.placeName.text=weather.name
        holder.placeTemp.text=weather.main.temp.toString()
    }


    override fun getItemCount(): Int {
      return weatherData.size
    }
    fun updateData(newWeatherList: List<WeatherResponse>) {
        weatherData = newWeatherList
        notifyDataSetChanged()
    }


    class FavouriteViewHolder(private val binding:FavouriteItemBinding):ViewHolder(binding.root) {
        val placeName=binding.placeName
        val placeTemp=binding.placeTemp
    }
}