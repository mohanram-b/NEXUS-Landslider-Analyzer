package com.example.data.model

data class IncidentAnalysisResult(
    val category: IncidentCategory,
    val categoryConfidence: Double,
    val summary: String,
    val hazards: List<String>,
    val extractedKeywords: List<String>
)

data class SeverityResult(
    val severityScore: Int,
    val severityLevel: SeverityLevel,
    val severityExplanation: String,
    val urgencyLevel: String
)

data class DuplicateResult(
    val similarityScore: Double,
    val isDuplicate: Boolean,
    val duplicateIncidentId: String?,
    val duplicateIncidentTitle: String?,
    val duplicateRecommendation: String
)

data class ImpactResult(
    val impactScore: Int,
    val populationAtRisk: String,
    val trafficDisruptionLevel: String,
    val infrastructureRisk: String,
    val impactPredictionSummary: String
)

data class RoutingResult(
    val departmentId: String,
    val recommendedDepartment: String,
    val routingConfidence: Double,
    val routingReason: String
)

data class ResponseStrategyResult(
    val tacticalStrategy: String,
    val priorityChecklist: List<String>,
    val resourceRequirements: String,
    val safetyProtocol: String
)

data class FullAIIncidentAnalysis(
    val incidentId: String,
    val analysis: IncidentAnalysisResult,
    val severity: SeverityResult,
    val duplicate: DuplicateResult,
    val impact: ImpactResult,
    val routing: RoutingResult,
    val strategy: ResponseStrategyResult,
    val overallConfidence: Double,
    val modelSignature: String = "Gemini 3.5 Flash / Orchestrator v2"
)
