package com.example.data.repository

import com.example.data.local.NexusDatabase
import com.example.data.local.entities.*
import com.example.data.model.*
import com.example.domain.ai.AIOrchestratorService
import com.example.domain.ai.AgentContext
import com.example.domain.realtime.NexusRealtimeEvent
import com.example.domain.realtime.RealtimeEventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class NexusRepository(
    private val database: NexusDatabase,
    private val orchestrator: AIOrchestratorService = AIOrchestratorService()
) {
    val allIncidents: Flow<List<IncidentEntity>> = database.incidentDao().getAllIncidents()
    val activeIncidents: Flow<List<IncidentEntity>> = database.incidentDao().getActiveIncidents()
    val allDepartments: Flow<List<DepartmentEntity>> = database.departmentDao().getAllDepartments()
    val allSensors: Flow<List<SensorEntity>> = database.sensorDao().getAllSensors()
    val liveActivityFeed: Flow<List<ActivityEntity>> = database.activityDao().getLiveActivityFeed()
    val allUsers: Flow<List<UserEntity>> = database.userDao().getAllUsers()

    fun getIncidentById(id: String): Flow<IncidentEntity?> = database.incidentDao().getIncidentByIdFlow(id)
    fun getAnalysisForIncident(incidentId: String): Flow<AIAnalysisEntity?> = database.aiAnalysisDao().getAnalysisForIncidentFlow(incidentId)
    fun getActivitiesForIncident(incidentId: String): Flow<List<ActivityEntity>> = database.activityDao().getActivitiesForIncident(incidentId)
    fun getEvidenceForIncident(incidentId: String): Flow<List<IncidentEvidenceEntity>> = database.incidentDao().getEvidenceForIncident(incidentId)
    fun getReadingsForSensor(sensorId: String): Flow<List<SensorReadingEntity>> = database.sensorDao().getReadingsForSensor(sensorId)

    suspend fun createIncident(
        title: String,
        description: String,
        category: IncidentCategory,
        latitude: Double,
        longitude: Double,
        address: String,
        reporterId: String = "usr_reporter",
        reporterName: String = "Civic Observer",
        isSensorTriggered: Boolean = false,
        triggeredSensorId: String? = null,
        evidencePhotos: List<String> = emptyList()
    ): IncidentEntity = withContext(Dispatchers.IO) {
        val incidentId = "inc_${System.currentTimeMillis() % 100000}"
        val initialIncident = IncidentEntity(
            id = incidentId,
            title = title,
            description = description,
            category = category,
            status = IncidentStatus.ANALYZING,
            severityScore = 50,
            severityLevel = SeverityLevel.MODERATE,
            impactScore = 40,
            latitude = latitude,
            longitude = longitude,
            address = address,
            reporterId = reporterId,
            reporterName = reporterName,
            isSensorTriggered = isSensorTriggered,
            triggeredSensorId = triggeredSensorId,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        database.incidentDao().insertIncident(initialIncident)

        // Insert evidence if provided
        evidencePhotos.forEach { photoUrl ->
            database.incidentDao().insertEvidence(
                IncidentEvidenceEntity(
                    id = UUID.randomUUID().toString(),
                    incidentId = incidentId,
                    type = "IMAGE",
                    url = photoUrl,
                    caption = "Citizen upload photo"
                )
            )
        }

        // Record Activity
        database.activityDao().insertActivity(
            ActivityEntity(
                id = UUID.randomUUID().toString(),
                incidentId = incidentId,
                userId = reporterId,
                userName = reporterName,
                type = ActivityType.INCIDENT_CREATED,
                message = "New incident reported: $title ($address)"
            )
        )

        // Real-time notification
        RealtimeEventBus.tryEmit(NexusRealtimeEvent.IncidentCreated(initialIncident))

        // Trigger AI Analysis pipeline in background
        CoroutineScope(Dispatchers.IO).launch {
            runAIAnalysisForIncident(incidentId)
        }

        initialIncident
    }

    suspend fun runAIAnalysisForIncident(incidentId: String): FullAIIncidentAnalysis? = withContext(Dispatchers.IO) {
        val incident = database.incidentDao().getIncidentById(incidentId) ?: return@withContext null
        val existingIncidents = database.incidentDao().getPotentialDuplicates(incidentId)
        val activeSensors = database.sensorDao().getAllSensors().first()
        val departments = database.departmentDao().getAllDepartments().first()

        val context = AgentContext(
            incident = incident,
            existingIncidents = existingIncidents,
            activeSensors = activeSensors,
            departments = departments
        )

        RealtimeEventBus.tryEmit(
            NexusRealtimeEvent.IncidentAnalyzing(incidentId, "Initializing AI Orchestrator Pipeline...", 0.05f)
        )

        val fullAnalysis = orchestrator.executeIncidentAnalysisPipeline(context) { stage, progress ->
            RealtimeEventBus.tryEmit(NexusRealtimeEvent.IncidentAnalyzing(incidentId, stage, progress))
        }

        // Convert to AIAnalysisEntity
        val hazardsJson = "[" + fullAnalysis.analysis.hazards.joinToString(",") { "\"$it\"" } + "]"
        val checklistJson = "[" + fullAnalysis.strategy.priorityChecklist.joinToString(",") { "\"$it\"" } + "]"

        val analysisEntity = AIAnalysisEntity(
            id = "ai_$incidentId",
            incidentId = incidentId,
            summary = fullAnalysis.analysis.summary,
            category = fullAnalysis.analysis.category.name,
            categoryConfidence = fullAnalysis.analysis.categoryConfidence,
            hazardsJson = hazardsJson,
            severityScore = fullAnalysis.severity.severityScore,
            severityExplanation = fullAnalysis.severity.severityExplanation,
            duplicateSimilarityScore = fullAnalysis.duplicate.similarityScore,
            duplicateIncidentId = fullAnalysis.duplicate.duplicateIncidentId,
            duplicateRecommendation = fullAnalysis.duplicate.duplicateRecommendation,
            impactScore = fullAnalysis.impact.impactScore,
            impactPrediction = fullAnalysis.impact.impactPredictionSummary,
            populationAtRisk = fullAnalysis.impact.populationAtRisk,
            trafficDisruptionLevel = fullAnalysis.impact.trafficDisruptionLevel,
            infrastructureRisk = fullAnalysis.impact.infrastructureRisk,
            routingDecision = fullAnalysis.routing.recommendedDepartment,
            routingConfidence = fullAnalysis.routing.routingConfidence,
            routingReason = fullAnalysis.routing.routingReason,
            responseStrategy = fullAnalysis.strategy.tacticalStrategy,
            priorityChecklistJson = checklistJson,
            overallConfidence = fullAnalysis.overallConfidence,
            aiModelUsed = fullAnalysis.modelSignature
        )

        database.aiAnalysisDao().insertAnalysis(analysisEntity)

        // Update Incident status & scores
        val updatedIncident = incident.copy(
            category = fullAnalysis.analysis.category,
            status = IncidentStatus.TRIAGED,
            severityScore = fullAnalysis.severity.severityScore,
            severityLevel = fullAnalysis.severity.severityLevel,
            impactScore = fullAnalysis.impact.impactScore,
            departmentId = fullAnalysis.routing.departmentId,
            departmentName = fullAnalysis.routing.recommendedDepartment,
            updatedAt = System.currentTimeMillis()
        )
        database.incidentDao().updateIncident(updatedIncident)

        // Record Activity
        database.activityDao().insertActivity(
            ActivityEntity(
                id = UUID.randomUUID().toString(),
                incidentId = incidentId,
                userId = "nexus_ai",
                userName = "NEXUS AI Orchestrator",
                type = ActivityType.AI_ANALYZED,
                message = "AI Orchestration: Severity ${fullAnalysis.severity.severityScore}/100 (${fullAnalysis.severity.severityLevel.displayName}) | Routed to ${fullAnalysis.routing.recommendedDepartment}"
            )
        )

        RealtimeEventBus.tryEmit(NexusRealtimeEvent.IncidentAnalyzed(incidentId, fullAnalysis))
        fullAnalysis
    }

    suspend fun assignDepartment(incidentId: String, departmentId: String, departmentName: String, assignedByUserName: String = "Marcus Hayes") = withContext(Dispatchers.IO) {
        database.incidentDao().assignDepartment(incidentId, departmentId, departmentName)
        database.activityDao().insertActivity(
            ActivityEntity(
                id = UUID.randomUUID().toString(),
                incidentId = incidentId,
                userId = "usr_operator",
                userName = assignedByUserName,
                type = ActivityType.DEPARTMENT_ASSIGNED,
                message = "Incident assigned to $departmentName for immediate dispatch"
            )
        )
        RealtimeEventBus.tryEmit(NexusRealtimeEvent.IncidentAssigned(incidentId, departmentName))
    }

    suspend fun updateIncidentStatus(incidentId: String, status: IncidentStatus, userName: String = "Operator") = withContext(Dispatchers.IO) {
        database.incidentDao().updateIncidentStatus(incidentId, status)
        val activityType = if (status == IncidentStatus.RESOLVED) ActivityType.RESOLVED else ActivityType.STATUS_CHANGED
        database.activityDao().insertActivity(
            ActivityEntity(
                id = UUID.randomUUID().toString(),
                incidentId = incidentId,
                userId = "usr_operator",
                userName = userName,
                type = activityType,
                message = "Incident status updated to ${status.displayName}"
            )
        )
        if (status == IncidentStatus.RESOLVED) {
            RealtimeEventBus.tryEmit(NexusRealtimeEvent.IncidentResolved(incidentId))
        }
    }

    suspend fun addOperatorNote(incidentId: String, note: String, userName: String) = withContext(Dispatchers.IO) {
        database.activityDao().insertActivity(
            ActivityEntity(
                id = UUID.randomUUID().toString(),
                incidentId = incidentId,
                userId = "usr_operator",
                userName = userName,
                type = ActivityType.OPERATOR_NOTE,
                message = note
            )
        )
    }

    suspend fun simulateSensorReading(sensorId: String, valueOverride: Double? = null): SensorEntity? = withContext(Dispatchers.IO) {
        val sensor = database.sensorDao().getSensorById(sensorId) ?: return@withContext null
        val newValue = valueOverride ?: (sensor.threshold * 1.35) // Cross threshold
        val isAlert = newValue >= sensor.threshold
        val isWarning = newValue >= sensor.warningThreshold
        val newStatus = when {
            isAlert -> SensorStatus.ALERT
            isWarning -> SensorStatus.WARNING
            else -> SensorStatus.ACTIVE
        }

        database.sensorDao().updateSensorReading(sensorId, newValue, newStatus)
        database.sensorDao().insertReading(
            SensorReadingEntity(
                id = UUID.randomUUID().toString(),
                sensorId = sensorId,
                value = newValue,
                unit = sensor.unit,
                isAlertTrigger = isAlert
            )
        )

        val updatedSensor = sensor.copy(currentValue = newValue, status = newStatus)

        if (isAlert) {
            val alertMsg = "Critical Threshold Exceeded: ${sensor.name} reading reached $newValue ${sensor.unit} (Threshold: ${sensor.threshold} ${sensor.unit})"
            database.activityDao().insertActivity(
                ActivityEntity(
                    id = UUID.randomUUID().toString(),
                    incidentId = null,
                    userId = "sensor_net",
                    userName = "Telemetry Network",
                    type = ActivityType.SENSOR_TRIGGERED,
                    message = alertMsg
                )
            )
            RealtimeEventBus.tryEmit(NexusRealtimeEvent.SensorAlert(updatedSensor, newValue, alertMsg))
        } else {
            RealtimeEventBus.tryEmit(NexusRealtimeEvent.SensorReading(sensorId, newValue, sensor.unit))
        }

        updatedSensor
    }

    // Complete Automated Demo Flow: "SIMULATE FLOOD EVENT"
    suspend fun simulateFloodEventDemo(): IncidentEntity = withContext(Dispatchers.IO) {
        // 1. Water sensor reading increases and exceeds threshold
        val sensor = simulateSensorReading("snr_flood_01", 76.5)

        // 2. Incident automatically created from sensor alert
        val title = "Flash Flood Inundation & Pump Failure at 12th Ave Underpass"
        val desc = "Automated sensor alert: Underpass water depth exceeded critical threshold (76.5 cm / 50 cm). Water accumulation is accelerating with 2 stalled vehicles reported."
        val incident = createIncident(
            title = title,
            description = desc,
            category = IncidentCategory.FLOODING,
            latitude = 37.7749,
            longitude = -122.4194,
            address = "Market St & 12th Ave Underpass",
            reporterId = "snr_flood_01",
            reporterName = "Sensor #snr_flood_01",
            isSensorTriggered = true,
            triggeredSensorId = "snr_flood_01"
        )

        incident
    }

    suspend fun resetAndReseedDatabase() = withContext(Dispatchers.IO) {
        database.clearAllTables()
        NexusDatabaseSeeder.seedIfEmpty(database)
    }
}
