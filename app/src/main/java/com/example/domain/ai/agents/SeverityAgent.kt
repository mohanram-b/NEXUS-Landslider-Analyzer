package com.example.domain.ai.agents

import com.example.data.model.IncidentAnalysisResult
import com.example.data.model.IncidentCategory
import com.example.data.model.SeverityLevel
import com.example.data.model.SeverityResult
import com.example.domain.ai.AgentContext
import kotlin.math.min

class SeverityAgent {
    fun score(context: AgentContext, analysis: IncidentAnalysisResult): SeverityResult {
        var baseScore = 30
        val text = "${context.incident.title} ${context.incident.description}".lowercase()

        // Critical hazard indicators
        if (text.contains("immediate") || text.contains("danger") || text.contains("critical") || text.contains("spark") || text.contains("collapse") || text.contains("sinkhole") || text.contains("life-threatening")) {
            baseScore += 35
        }
        if (text.contains("blocked") || text.contains("highway") || text.contains("main road") || text.contains("submerged") || text.contains("major")) {
            baseScore += 20
        }
        if (text.contains("children") || text.contains("hospital") || text.contains("school") || text.contains("substation")) {
            baseScore += 18
        }

        // Sensor telemetry correlation
        val nearbySensor = context.activeSensors.find { sensor ->
            sensor.status == com.example.data.model.SensorStatus.ALERT || sensor.status == com.example.data.model.SensorStatus.WARNING
        }
        if (nearbySensor != null) {
            baseScore += 15
        }

        // Category specific baseline
        when (analysis.category) {
            IncidentCategory.ELECTRICAL_HAZARD -> baseScore += 12
            IncidentCategory.FLOODING -> baseScore += 10
            IncidentCategory.ACCIDENT -> baseScore += 10
            IncidentCategory.DRAINAGE_FAILURE -> baseScore += 5
            IncidentCategory.ROAD_DAMAGE -> baseScore += 4
            IncidentCategory.PUBLIC_SAFETY -> baseScore += 8
        }

        val finalScore = min(98, baseScore.coerceAtLeast(15))
        val severityLevel = SeverityLevel.fromScore(finalScore)

        val explanation = when (severityLevel) {
            SeverityLevel.CRITICAL -> "High severity (${finalScore}/100): Imminent risk to human life, critical arterial transit disruption, or energized electrical hazard requires immediate tier-1 emergency response."
            SeverityLevel.HIGH -> "High severity (${finalScore}/100): Severe infrastructure degradation with substantial traffic bottleneck and cascading regional impact."
            SeverityLevel.MODERATE -> "Moderate severity (${finalScore}/100): Localized operational disturbance with controlled safety hazards; scheduled dispatch within standard response SLA."
            SeverityLevel.LOW -> "Low severity (${finalScore}/100): Minor non-hazardous condition; routine field inspection and maintenance logging."
        }

        val urgency = when (severityLevel) {
            SeverityLevel.CRITICAL -> "IMMEDIATE DISPATCH (< 15 min)"
            SeverityLevel.HIGH -> "PRIORITY RESPONSE (< 45 min)"
            SeverityLevel.MODERATE -> "STANDARD RESPONSE (< 3 hrs)"
            SeverityLevel.LOW -> "ROUTINE SCHEDULE (< 24 hrs)"
        }

        return SeverityResult(
            severityScore = finalScore,
            severityLevel = severityLevel,
            severityExplanation = explanation,
            urgencyLevel = urgency
        )
    }
}
