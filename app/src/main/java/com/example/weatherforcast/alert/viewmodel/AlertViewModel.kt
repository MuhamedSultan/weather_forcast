package com.example.weatherforcast.alert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.alert.repo.AlertRepository
import com.example.weatherforcast.pojo.alerts.Alerts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlertViewModel(private val alertRepository: AlertRepository) : ViewModel() {
    private val _getAllAlerts: MutableStateFlow<List<Alerts>?> = MutableStateFlow(null)
    val getAllAlerts: StateFlow<List<Alerts>?> get() = _getAllAlerts

    fun addAlert(alert: Alerts) =
        viewModelScope.launch {
            alertRepository.addAlert(alert)
        }

    fun getAllAlerts() =
        viewModelScope.launch {
            alertRepository.getAlert().collect{
                _getAllAlerts.value=it
            }
        }

    fun deleteAlert(alert: Alerts) = viewModelScope.launch {
        alertRepository.deleteAlert(alert)
        getAllAlerts()
    }
}