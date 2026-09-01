package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_analyses")
data class AIAnalysisEntity(
    @PrimaryKey val id: String,
    val incidentId: String,
    val summary: String,
    val category: String,
    val categoryConfidence: Double, // 0.0 - 1.0 (e.g. 0.94)
    val hazardsJson: String, // List of hazards JSON
    val severityScore: Int, // 0 - 100
    val severityExplanation: String,
    val duplicateSimilarityScore: Double = 0.0, // 0.0 - 1.0
    val duplicateIncidentId: String? = null,
    val duplicateRecommendation: String = "Unique Incident",
    val impactScore: Int, // 0 - 100
    val impactPrediction: String, // Traffic, population, infrastructure analysis
    val populationAtRisk: String = "Low (~50-200 people)",
    val trafficDisruptionLevel: String = "Moderate Arterial Disruption",
    val infrastructureRisk: String = "Medium Structural Risk",
    val routingDecision: String, // Recommended Department
    val routingConfidence: Double, // 0.0 - 1.0
    val routingReason: String,
    val responseStrategy: String, // SOP tactical actions
    val priorityChecklistJson: String = "[]",
    val overallConfidence: Double, // 0.0 - 1.0
    val aiModelUsed: String = "Gemini 3.5 Flash / Orchestrator v2",
    val createdAt: Long = System.currentTimeMillis()
)
