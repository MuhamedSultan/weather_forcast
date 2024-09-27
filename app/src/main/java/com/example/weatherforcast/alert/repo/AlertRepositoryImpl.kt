package com.example.weatherforcast.alert.repo

import com.example.weatherforcast.db.LocalDataSource
import com.example.weatherforcast.pojo.alerts.Alerts
import kotlinx.coroutines.flow.Flow

class AlertRepositoryImpl(private val localDataSource: LocalDataSource):AlertRepository {
    override suspend fun addAlert(alert: Alerts) {
        localDataSource.addAlert(alert)
    }

    override suspend fun getAlert(): Flow<List<Alerts>> {
      return localDataSource.getAlert()
    }

    override suspend fun deleteAlert(alert: Alerts) {
       localDataSource.deleteAlert(alert)
    }
}