package com.example.weatherforcast.alert.view

import com.example.weatherforcast.pojo.alerts.Alerts

interface OnItemAlertClick {
    fun deleteAlertItem(alert: Alerts)
}