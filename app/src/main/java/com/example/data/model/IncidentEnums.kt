package com.example.data.model

enum class IncidentCategory(val displayName: String, val iconName: String) {
    FLOODING("Flooding", "water_drop"),
    ROAD_DAMAGE("Road Damage", "construction"),
    ELECTRICAL_HAZARD("Electrical Hazard", "bolt"),
    DRAINAGE_FAILURE("Drainage Failure", "water_damage"),
    ACCIDENT("Traffic Accident", "car_crash"),
    PUBLIC_SAFETY("Public Safety", "shield");

    companion object {
        fun fromString(value: String): IncidentCategory {
            return entries.find { it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) }
                ?: FLOODING
        }
    }
}

enum class IncidentStatus(val displayName: String) {
    REPORTED("Reported"),
    ANALYZING("AI Analyzing"),
    TRIAGED("Triaged"),
    ASSIGNED("Assigned"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved");

    companion object {
        fun fromString(value: String): IncidentStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) }
                ?: REPORTED
        }
    }
}

enum class SeverityLevel(val displayName: String) {
    LOW("Low"),
    MODERATE("Moderate"),
    HIGH("High"),
    CRITICAL("Critical");

    companion object {
        fun fromScore(score: Int): SeverityLevel {
            return when {
                score >= 80 -> CRITICAL
                score >= 60 -> HIGH
                score >= 35 -> MODERATE
                else -> LOW
            }
        }

        fun fromString(value: String): SeverityLevel {
            return entries.find { it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) }
                ?: MODERATE
        }
    }
}

enum class UserRole(val displayName: String) {
    ADMIN("Admin"),
    OPERATOR("Operator"),
    DEPARTMENT("Department Head"),
    REPORTER("Reporter");

    companion object {
        fun fromString(value: String): UserRole {
            return entries.find { it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) }
                ?: OPERATOR
        }
    }
}

enum class SensorType(val displayName: String, val defaultUnit: String) {
    WATER_LEVEL("Water Depth / Surge", "cm"),
    RAIN_GAUGE("Precipitation Rate", "mm/h"),
    ROAD_SURFACE_VIBRATION("Road Seismic / Vibration", "m/s²"),
    GRID_POWER_LOAD("Electrical Grid Load", "kW"),
    TRAFFIC_CONGESTION("Traffic Artery Density", "%"),
    AIR_QUALITY("Hazardous Particle Sensor", "AQI");

    companion object {
        fun fromString(value: String): SensorType {
            return entries.find { it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) }
                ?: WATER_LEVEL
        }
    }
}

enum class SensorStatus(val displayName: String) {
    ACTIVE("Active"),
    WARNING("Warning"),
    ALERT("Alert"),
    OFFLINE("Offline")
}

enum class ActivityType(val displayName: String) {
    INCIDENT_CREATED("Incident Created"),
    AI_ANALYZED("AI Analysis Completed"),
    SEVERITY_SCORED("Severity Scored"),
    DUPLICATE_FLAGGED("Duplicate Check"),
    IMPACT_PREDICTED("Impact Calculated"),
    DEPARTMENT_ROUTED("Department Routed"),
    DEPARTMENT_ASSIGNED("Assigned to Department"),
    STATUS_CHANGED("Status Changed"),
    SENSOR_TRIGGERED("Sensor Alert Triggered"),
    OPERATOR_NOTE("Operator Note Added"),
    RESOLVED("Incident Resolved")
}
