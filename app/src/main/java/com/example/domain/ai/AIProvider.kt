package com.example.domain.ai

import com.example.data.local.entities.DepartmentEntity
import com.example.data.local.entities.IncidentEntity
import com.example.data.local.entities.SensorEntity
import com.example.data.model.*

data class AgentContext(
    val incident: IncidentEntity,
    val existingIncidents: List<IncidentEntity>,
    val activeSensors: List<SensorEntity>,
    val departments: List<DepartmentEntity>
)

interface AIProvider {
    suspend fun analyzeIncident(context: AgentContext): IncidentAnalysisResult
    suspend fun scoreSeverity(context: AgentContext, analysis: IncidentAnalysisResult): SeverityResult
    suspend fun detectDuplicates(context: AgentContext, analysis: IncidentAnalysisResult): DuplicateResult
    suspend fun predictImpact(context: AgentContext, analysis: IncidentAnalysisResult, severity: SeverityResult): ImpactResult
    suspend fun routeDepartment(context: AgentContext, analysis: IncidentAnalysisResult, severity: SeverityResult): RoutingResult
    suspend fun planResponseStrategy(context: AgentContext, analysis: IncidentAnalysisResult, severity: SeverityResult, routing: RoutingResult): ResponseStrategyResult
}
