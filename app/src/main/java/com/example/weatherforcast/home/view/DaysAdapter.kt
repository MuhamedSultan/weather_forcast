package com.example.weatherforcast.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.weatherforcast.databinding.DaysItemBinding
import com.example.weatherforcast.databinding.HoursItemBinding
import com.example.weatherforcast.pojo.days_weather.State

class DaysAdapter(private val context: Context, private val state: List<State>) :
    Adapter<DaysAdapter.HoursViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoursViewHolder {
        val view = DaysItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HoursViewHolder(view)
    }


    override fun onBindViewHolder(holder: HoursViewHolder, position: Int) {
        val state = state[position]
        holder.hoursTv.text = state.dt_txt
        holder.tempTv.text = String.format("%.2f", state.main.temp)
        Glide.with(context)
            .load("https://openweathermap.org/img/wn/${state.weather[0].icon}@2x.png")
            .into(holder.weatherImage)
    }

    override fun getItemCount(): Int {
        return state.size
    }

    class HoursViewHolder(binding: DaysItemBinding) : ViewHolder(binding.root) {
        val hoursTv = binding.hourTv
        val weatherImage = binding.weatherImage
        val tempTv = binding.hourTempTv

    }
}