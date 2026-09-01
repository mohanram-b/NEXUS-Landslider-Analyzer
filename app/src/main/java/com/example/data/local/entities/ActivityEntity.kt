package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ActivityType

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val id: String,
    val incidentId: String?,
    val userId: String?,
    val userName: String = "NEXUS AI System",
    val type: ActivityType,
    val message: String,
    val metadata: String = "{}",
    val createdAt: Long = System.currentTimeMillis()
)
