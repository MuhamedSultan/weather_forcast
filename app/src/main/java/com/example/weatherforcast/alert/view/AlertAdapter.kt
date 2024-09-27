package com.example.weatherforcast.alert.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcast.databinding.AlertItemBinding
import com.example.weatherforcast.pojo.alerts.Alerts

class AlertAdapter(private val alerts: List<Alerts>,private val onItemAlertClick: OnItemAlertClick) : RecyclerView.Adapter<AlertViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = AlertItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = alerts[position]
        holder.from.text = alert.alertType
        holder.to.text = alert.time.toString()
        holder.deleteAlert.setOnClickListener {
            onItemAlertClick.deleteAlertItem(alert)
        }
    }

    override fun getItemCount(): Int {
        return alerts.size
    }
}

class AlertViewHolder(binding: AlertItemBinding) : RecyclerView.ViewHolder(binding.root) {
    val from = binding.fromTime
    val to = binding.toTime
    val deleteAlert=binding.deleteItem

}
