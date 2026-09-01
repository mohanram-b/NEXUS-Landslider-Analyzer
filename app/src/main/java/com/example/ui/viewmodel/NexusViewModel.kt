package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.NexusDatabase
import com.example.data.local.entities.*
import com.example.data.model.*
import com.example.data.repository.NexusDatabaseSeeder
import com.example.data.repository.NexusRepository
import com.example.domain.realtime.NexusRealtimeEvent
import com.example.domain.realtime.RealtimeEventBus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardMetrics(
    val activeCount: Int = 0,
    val criticalCount: Int = 0,
    val avgResponseTimeMinutes: String = "18.4 min",
    val resolutionRatePercentage: String = "84.2%"
)

data class IncidentFilterState(
    val searchQuery: String = "",
    val categoryFilter: IncidentCategory? = null,
    val severityFilter: SeverityLevel? = null,
    val statusFilter: IncidentStatus? = null,
    val departmentFilter: String? = null
)

class NexusViewModel(application: Application) : AndroidViewModel(application) {

    private val database = NexusDatabase.getInstance(application)
    private val repository = NexusRepository(database)

    val allIncidents = repository.allIncidents.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val activeIncidents = repository.activeIncidents.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allDepartments = repository.allDepartments.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allSensors = repository.allSensors.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val liveActivityFeed = repository.liveActivityFeed.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allUsers = repository.allUsers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current User Session
    private val _currentUser = MutableStateFlow(
        UserEntity("usr_operator", "Marcus Hayes", "marcus.h@dispatch.nexus.gov", UserRole.OPERATOR)
    )
    val currentUser = _currentUser.asStateFlow()

    // Filters
    private val _filterState = MutableStateFlow(IncidentFilterState())
    val filterState = _filterState.asStateFlow()

    // Filtered Incidents
    val filteredIncidents = combine(allIncidents, _filterState) { list, filter ->
        list.filter { item ->
            val matchesQuery = filter.searchQuery.isEmpty() ||
                    item.title.contains(filter.searchQuery, ignoreCase = true) ||
                    item.description.contains(filter.searchQuery, ignoreCase = true) ||
                    item.address.contains(filter.searchQuery, ignoreCase = true)
            val matchesCategory = filter.categoryFilter == null || item.category == filter.categoryFilter
            val matchesSeverity = filter.severityFilter == null || item.severityLevel == filter.severityFilter
            val matchesStatus = filter.statusFilter == null || item.status == filter.statusFilter
            val matchesDept = filter.departmentFilter == null || item.departmentId == filter.departmentFilter
            matchesQuery && matchesCategory && matchesSeverity && matchesStatus && matchesDept
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Metrics
    val dashboardMetrics = allIncidents.map { list ->
        val active = list.count { it.status != IncidentStatus.RESOLVED }
        val critical = list.count { it.severityLevel == SeverityLevel.CRITICAL && it.status != IncidentStatus.RESOLVED }
        val resolved = list.count { it.status == IncidentStatus.RESOLVED }
        val total = list.size
        val resRate = if (total > 0) ((resolved.toDouble() / total) * 100).toInt() else 0

        DashboardMetrics(
            activeCount = active,
            criticalCount = critical,
            avgResponseTimeMinutes = if (active > 0) "14.8 min" else "12.0 min",
            resolutionRatePercentage = "$resRate%"
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardMetrics())

    // Selected Incident Detail
    private val _selectedIncidentId = MutableStateFlow<String?>(null)
    val selectedIncidentId = _selectedIncidentId.asStateFlow()

    val selectedIncident = _selectedIncidentId.flatMapLatest { id ->
        if (id != null) repository.getIncidentById(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedAIAnalysis = _selectedIncidentId.flatMapLatest { id ->
        if (id != null) repository.getAnalysisForIncident(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedIncidentActivities = _selectedIncidentId.flatMapLatest { id ->
        if (id != null) repository.getActivitiesForIncident(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedIncidentEvidence = _selectedIncidentId.flatMapLatest { id ->
        if (id != null) repository.getEvidenceForIncident(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Live AI Analysis Execution Progress
    private val _analyzingIncidentId = MutableStateFlow<String?>(null)
    val analyzingIncidentId = _analyzingIncidentId.asStateFlow()

    private val _analysisStage = MutableStateFlow<String>("")
    val analysisStage = _analysisStage.asStateFlow()

    private val _analysisProgress = MutableStateFlow(0f)
    val analysisProgress = _analysisProgress.asStateFlow()

    // Live Broadcast Alert notification Banner
    private val _activeRealtimeAlert = MutableStateFlow<String?>(null)
    val activeRealtimeAlert = _activeRealtimeAlert.asStateFlow()

    init {
        viewModelScope.launch {
            NexusDatabaseSeeder.seedIfEmpty(database)
        }

        // Listen to Realtime Event Bus
        viewModelScope.launch {
            RealtimeEventBus.events.collect { event ->
                when (event) {
                    is NexusRealtimeEvent.IncidentAnalyzing -> {
                        _analyzingIncidentId.value = event.incidentId
                        _analysisStage.value = event.stage
                        _analysisProgress.value = event.progress
                    }
                    is NexusRealtimeEvent.IncidentAnalyzed -> {
                        _analyzingIncidentId.value = null
                        _analysisStage.value = "Complete"
                        _analysisProgress.value = 1.0f
                        _activeRealtimeAlert.value = "AI Orchestration Complete: Incident analyzed and severity scored."
                    }
                    is NexusRealtimeEvent.SensorAlert -> {
                        _activeRealtimeAlert.value = "CRITICAL SENSOR ALERT: ${event.message}"
                    }
                    is NexusRealtimeEvent.IncidentCreated -> {
                        _activeRealtimeAlert.value = "New Incident Registered: ${event.incident.title}"
                    }
                    else -> Unit
                }
            }
        }
    }

    fun selectIncident(id: String?) {
        _selectedIncidentId.value = id
    }

    fun updateSearchQuery(query: String) {
        _filterState.value = _filterState.value.copy(searchQuery = query)
    }

    fun updateCategoryFilter(cat: IncidentCategory?) {
        _filterState.value = _filterState.value.copy(categoryFilter = cat)
    }

    fun updateSeverityFilter(sev: SeverityLevel?) {
        _filterState.value = _filterState.value.copy(severityFilter = sev)
    }

    fun updateStatusFilter(status: IncidentStatus?) {
        _filterState.value = _filterState.value.copy(statusFilter = status)
    }

    fun clearFilters() {
        _filterState.value = IncidentFilterState()
    }

    fun switchUser(user: UserEntity) {
        _currentUser.value = user
    }

    fun createIncidentReport(
        title: String,
        description: String,
        category: IncidentCategory,
        latitude: Double,
        longitude: Double,
        address: String,
        photos: List<String>,
        onCreated: (String) -> Unit
    ) {
        viewModelScope.launch {
            val user = _currentUser.value
            val created = repository.createIncident(
                title = title,
                description = description,
                category = category,
                latitude = latitude,
                longitude = longitude,
                address = address,
                reporterId = user.id,
                reporterName = user.name,
                evidencePhotos = photos
            )
            selectIncident(created.id)
            onCreated(created.id)
        }
    }

    fun rerunAIAnalysis(incidentId: String) {
        viewModelScope.launch {
            repository.runAIAnalysisForIncident(incidentId)
        }
    }

    fun assignDepartment(incidentId: String, deptId: String, deptName: String) {
        viewModelScope.launch {
            val user = _currentUser.value
            repository.assignDepartment(incidentId, deptId, deptName, user.name)
        }
    }

    fun updateIncidentStatus(incidentId: String, status: IncidentStatus) {
        viewModelScope.launch {
            val user = _currentUser.value
            repository.updateIncidentStatus(incidentId, status, user.name)
        }
    }

    fun addOperatorNote(incidentId: String, note: String) {
        viewModelScope.launch {
            val user = _currentUser.value
            repository.addOperatorNote(incidentId, note, user.name)
        }
    }

    fun simulateSensorAlert(sensorId: String) {
        viewModelScope.launch {
            repository.simulateSensorReading(sensorId)
        }
    }

    fun triggerSimulateFloodDemo(onIncidentCreated: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            val incident = repository.simulateFloodEventDemo()
            selectIncident(incident.id)
            onIncidentCreated?.invoke(incident.id)
        }
    }

    fun resetAndReseed() {
        viewModelScope.launch {
            repository.resetAndReseedDatabase()
            _activeRealtimeAlert.value = "Database reseeded with 40 incidents & 10 telemetry nodes"
        }
    }

    fun dismissAlertBanner() {
        _activeRealtimeAlert.value = null
    }
}
