package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sensor_readings")
data class SensorReadingEntity(
    @PrimaryKey val id: String,
    val sensorId: String,
    val value: Double,
    val unit: String,
    val isAlertTrigger: Boolean = false,
    val recordedAt: Long = System.currentTimeMillis()
)
