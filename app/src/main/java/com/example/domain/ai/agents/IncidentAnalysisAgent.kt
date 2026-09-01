package com.example.domain.ai.agents

import com.example.data.model.IncidentAnalysisResult
import com.example.data.model.IncidentCategory
import com.example.domain.ai.AgentContext
import java.util.Locale

class IncidentAnalysisAgent {
    fun analyze(context: AgentContext): IncidentAnalysisResult {
        val title = context.incident.title.lowercase(Locale.ROOT)
        val desc = context.incident.description.lowercase(Locale.ROOT)
        val combined = "$title $desc"

        val detectedCategory = when {
            combined.contains("flood") || combined.contains("water") || combined.contains("submerged") || combined.contains("overflow") -> IncidentCategory.FLOODING
            combined.contains("drain") || combined.contains("sewer") || combined.contains("culvert") || combined.contains("manhole") || combined.contains("clog") -> IncidentCategory.DRAINAGE_FAILURE
            combined.contains("wire") || combined.contains("electric") || combined.contains("power") || combined.contains("transformer") || combined.contains("spark") || combined.contains("blackout") -> IncidentCategory.ELECTRICAL_HAZARD
            combined.contains("pothole") || combined.contains("road") || combined.contains("bridge") || combined.contains("collapse") || combined.contains("pavement") || combined.contains("sinkhole") -> IncidentCategory.ROAD_DAMAGE
            combined.contains("crash") || combined.contains("accident") || combined.contains("collision") || combined.contains("vehicle") || combined.contains("pedestrian") -> IncidentCategory.ACCIDENT
            else -> context.incident.category
        }

        val hazards = mutableListOf<String>()
        if (combined.contains("flood") || combined.contains("water")) {
            hazards.add("Vehicular Stranding Hazard")
            hazards.add("Hydroplaning & Road Inundation")
        }
        if (combined.contains("electric") || combined.contains("wire") || combined.contains("spark")) {
            hazards.add("Electrocution & Arc Flash Risk")
            hazards.add("Secondary Fire Potential")
        }
        if (combined.contains("collapse") || combined.contains("sinkhole") || combined.contains("pothole")) {
            hazards.add("Structural Integrity Compromise")
            hazards.add("High-Speed Vehicle Axle Damage")
        }
        if (combined.contains("traffic") || combined.contains("road") || combined.contains("blocked")) {
            hazards.add("Major Commuter Artery Gridlock")
            hazards.add("Emergency Vehicle Route Obstruction")
        }
        if (hazards.isEmpty()) {
            hazards.add("Public Safety Impact")
            hazards.add("Municipal Infrastructure Disruption")
        }

        val extractedKeywords = mutableListOf<String>()
        val words = combined.split("\\s+".toRegex()).filter { it.length > 4 }.distinct().take(6)
        extractedKeywords.addAll(words)

        val summary = "Automated AI synthesis: Detected ${detectedCategory.displayName} incident at ${context.incident.address}. Identified ${hazards.size} active operational hazards requiring prompt municipal intervention."

        val confidence = when {
            combined.length > 50 -> 0.95
            combined.length > 20 -> 0.89
            else -> 0.78
        }

        return IncidentAnalysisResult(
            category = detectedCategory,
            categoryConfidence = confidence,
            summary = summary,
            hazards = hazards,
            extractedKeywords = extractedKeywords
        )
    }
}
