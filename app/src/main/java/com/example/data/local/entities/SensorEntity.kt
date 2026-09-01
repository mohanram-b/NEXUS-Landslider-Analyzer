package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.SensorStatus
import com.example.data.model.SensorType

@Entity(tableName = "sensors")
data class SensorEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: SensorType,
    val status: SensorStatus,
    val latitude: Double,
    val longitude: Double,
    val locationName: String,
    val currentValue: Double,
    val unit: String,
    val threshold: Double,
    val warningThreshold: Double,
    val lastUpdated: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
