package com.example.domain.ai.agents

import com.example.data.local.entities.IncidentEntity
import com.example.data.model.DuplicateResult
import com.example.data.model.IncidentAnalysisResult
import com.example.domain.ai.AgentContext
import kotlin.math.*

class DuplicateDetectionAgent {

    fun detect(context: AgentContext, analysis: IncidentAnalysisResult): DuplicateResult {
        var highestSimilarity = 0.0
        var matchedIncident: IncidentEntity? = null

        val currentTitleWords = context.incident.title.lowercase().split("\\s+".toRegex()).toSet()
        val currentLat = context.incident.latitude
        val currentLng = context.incident.longitude

        for (existing in context.existingIncidents) {
            if (existing.id == context.incident.id) continue

            // 1. Text Jaccard similarity on title & address
            val existingTitleWords = existing.title.lowercase().split("\\s+".toRegex()).toSet()
            val intersection = currentTitleWords.intersect(existingTitleWords).size
            val union = currentTitleWords.union(existingTitleWords).size
            val textSimilarity = if (union > 0) intersection.toDouble() / union else 0.0

            // 2. Spatial distance (Haversine formula in km)
            val distanceKm = calculateHaversineDistanceKm(currentLat, currentLng, existing.latitude, existing.longitude)
            val spatialScore = when {
                distanceKm < 0.1 -> 1.0 // Within 100 meters
                distanceKm < 0.5 -> 0.8 // Within 500 meters
                distanceKm < 1.5 -> 0.5 // Within 1.5 km
                else -> 0.0
            }

            // 3. Category match
            val categoryScore = if (existing.category == analysis.category) 1.0 else 0.0

            // 4. Temporal proximity (within 6 hours)
            val timeDiffHours = abs(context.incident.createdAt - existing.createdAt) / (1000.0 * 60 * 60)
            val temporalScore = when {
                timeDiffHours < 2.0 -> 1.0
                timeDiffHours < 6.0 -> 0.7
                timeDiffHours < 24.0 -> 0.4
                else -> 0.1
            }

            // Weighted composite similarity
            val compositeScore = (spatialScore * 0.40) + (textSimilarity * 0.30) + (categoryScore * 0.20) + (temporalScore * 0.10)

            if (compositeScore > highestSimilarity) {
                highestSimilarity = compositeScore
                matchedIncident = existing
            }
        }

        val isDuplicate = highestSimilarity >= 0.72

        val recommendation = when {
            isDuplicate && matchedIncident != null -> {
                "Correlated Duplicate: High spatio-temporal match with #${matchedIncident.id.takeLast(6)} (${matchedIncident.title}). Merge reports into active master ticket."
            }
            highestSimilarity > 0.45 && matchedIncident != null -> {
                "Potential Cluster: Related incident detected nearby (#${matchedIncident.id.takeLast(6)}). Link as secondary consequence."
            }
            else -> {
                "Unique Incident: No conflicting duplicate reports identified within geographical proximity radius."
            }
        }

        return DuplicateResult(
            similarityScore = (highestSimilarity * 100).roundToInt() / 100.0,
            isDuplicate = isDuplicate,
            duplicateIncidentId = if (isDuplicate) matchedIncident?.id else null,
            duplicateIncidentTitle = if (isDuplicate) matchedIncident?.title else null,
            duplicateRecommendation = recommendation
        )
    }

    private fun calculateHaversineDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
