package com.example.weatherforcast.alert.repo

import com.example.weatherforcast.pojo.alerts.Alerts
import kotlinx.coroutines.flow.Flow

interface AlertRepository {
    suspend fun addAlert(alert: Alerts)
    suspend fun getAlert(): Flow<List<Alerts>>
    suspend fun deleteAlert(alert: Alerts)
}