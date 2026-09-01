package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "departments")
data class DepartmentEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val email: String,
    val phone: String,
    val activeUnits: Int = 12,
    val responseTeamLead: String = "Dispatch Unit A",
    val createdAt: Long = System.currentTimeMillis()
)
