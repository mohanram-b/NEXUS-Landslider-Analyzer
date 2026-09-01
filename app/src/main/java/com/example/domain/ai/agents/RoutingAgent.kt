package com.example.domain.ai.agents

import com.example.data.model.IncidentAnalysisResult
import com.example.data.model.IncidentCategory
import com.example.data.model.RoutingResult
import com.example.data.model.SeverityResult
import com.example.domain.ai.AgentContext

class RoutingAgent {
    fun route(context: AgentContext, analysis: IncidentAnalysisResult, severity: SeverityResult): RoutingResult {
        // Target departments in the municipal system
        val targetDeptType = when (analysis.category) {
            IncidentCategory.FLOODING, IncidentCategory.DRAINAGE_FAILURE -> "WATER"
            IncidentCategory.ROAD_DAMAGE -> "TRANSPORTATION"
            IncidentCategory.ELECTRICAL_HAZARD -> "POWER"
            IncidentCategory.ACCIDENT, IncidentCategory.PUBLIC_SAFETY -> {
                if (severity.severityScore >= 75) "HAZARD" else "SAFETY"
            }
        }

        val matchedDept = context.departments.find { dept ->
            dept.type.contains(targetDeptType, ignoreCase = true) ||
            dept.name.contains(targetDeptType, ignoreCase = true)
        } ?: context.departments.firstOrNull()

        val deptName = matchedDept?.name ?: "Municipal Emergency Services"
        val deptId = matchedDept?.id ?: "dept_general"

        val confidence = when {
            matchedDept != null -> 0.94
            else -> 0.82
        }

        val reason = when (analysis.category) {
            IncidentCategory.FLOODING, IncidentCategory.DRAINAGE_FAILURE ->
                "Routed to $deptName due to heavy hydrological and stormwater drainage management scope."
            IncidentCategory.ELECTRICAL_HAZARD ->
                "Routed to $deptName due to high-voltage grid isolation and transformer safety jurisdiction."
            IncidentCategory.ROAD_DAMAGE ->
                "Routed to $deptName for rapid structural asphalt repair and civil engineering inspection."
            IncidentCategory.ACCIDENT ->
                "Routed to $deptName for immediate traffic diversion, towing clearance, and scene stabilization."
            IncidentCategory.PUBLIC_SAFETY ->
                "Routed to $deptName for perimeter control, civilian safety buffer, and hazard mitigation."
        }

        return RoutingResult(
            departmentId = deptId,
            recommendedDepartment = deptName,
            routingConfidence = confidence,
            routingReason = reason
        )
    }
}
