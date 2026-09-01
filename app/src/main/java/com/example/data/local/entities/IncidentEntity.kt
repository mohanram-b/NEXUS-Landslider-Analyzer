package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.IncidentCategory
import com.example.data.model.IncidentStatus
import com.example.data.model.SeverityLevel

@Entity(tableName = "incidents")
data class IncidentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: IncidentCategory,
    val status: IncidentStatus,
    val severityScore: Int,
    val severityLevel: SeverityLevel,
    val impactScore: Int,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val reporterId: String,
    val reporterName: String = "Anonymous Reporter",
    val departmentId: String? = null,
    val departmentName: String? = null,
    val isSensorTriggered: Boolean = false,
    val triggeredSensorId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
