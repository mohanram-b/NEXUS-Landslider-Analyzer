package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incident_evidence")
data class IncidentEvidenceEntity(
    @PrimaryKey val id: String,
    val incidentId: String,
    val type: String = "IMAGE", // IMAGE, SENSOR_TELEMETRY, DOCUMENT
    val url: String,
    val caption: String = "",
    val metadata: String = "{}",
    val createdAt: Long = System.currentTimeMillis()
)
