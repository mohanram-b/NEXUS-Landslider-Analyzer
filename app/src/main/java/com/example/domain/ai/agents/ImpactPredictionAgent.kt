package com.example.domain.ai.agents

import com.example.data.model.ImpactResult
import com.example.data.model.IncidentAnalysisResult
import com.example.data.model.SeverityResult
import com.example.domain.ai.AgentContext
import kotlin.math.min

class ImpactPredictionAgent {
    fun predict(context: AgentContext, analysis: IncidentAnalysisResult, severity: SeverityResult): ImpactResult {
        val text = "${context.incident.title} ${context.incident.description}".lowercase()

        // 1. Traffic disruption evaluation
        val trafficDisruption = when {
            text.contains("expressway") || text.contains("highway") || text.contains("arterial") || text.contains("underpass") -> "Major Highway Gridlock (Est. 4.5h delay)"
            text.contains("intersection") || text.contains("main st") || text.contains("avenue") || text.contains("boulevard") -> "Moderate Arterial Disruption (Est. 1.5h delay)"
            else -> "Localized Street Level Disruption"
        }

        // 2. Population at risk
        val populationAtRisk = when {
            severity.severityScore >= 80 -> "High Exposure (~1,500 - 5,000 residents/commuters)"
            severity.severityScore >= 55 -> "Medium Exposure (~300 - 1,200 residents)"
            else -> "Low Exposure (< 150 individuals)"
        }

        // 3. Infrastructure risk
        val infrastructureRisk = when {
            text.contains("power") || text.contains("substation") || text.contains("grid") -> "Critical Utility & Electrical Distribution Grid Hazard"
            text.contains("bridge") || text.contains("culvert") || text.contains("structural") -> "Municipal Structural & Drainage Integrity Compromise"
            text.contains("flood") || text.contains("water") -> "Drainage Basin Saturation & Road Foundation Erosion"
            else -> "Surface Level Pavement & Transit Wear"
        }

        // Compound impact score (0 - 100)
        var impactBase = (severity.severityScore * 0.55).toInt()
        if (trafficDisruption.contains("Major")) impactBase += 24
        else if (trafficDisruption.contains("Moderate")) impactBase += 12

        if (populationAtRisk.contains("High")) impactBase += 18
        else if (populationAtRisk.contains("Medium")) impactBase += 9

        val finalImpactScore = min(99, impactBase.coerceAtLeast(20))

        val summary = "Impact Prediction (${finalImpactScore}/100): $trafficDisruption. $populationAtRisk affected. Primary infrastructure risk: $infrastructureRisk."

        return ImpactResult(
            impactScore = finalImpactScore,
            populationAtRisk = populationAtRisk,
            trafficDisruptionLevel = trafficDisruption,
            infrastructureRisk = infrastructureRisk,
            impactPredictionSummary = summary
        )
    }
}
