package com.example.weatherforcast.pojo.alerts

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_table")
data class Alerts(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val alertType: String, val time:String)