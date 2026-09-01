package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.NexusDatabase
import com.example.data.local.entities.DepartmentEntity
import com.example.data.local.entities.IncidentEntity
import com.example.data.model.*
import com.example.domain.ai.AIOrchestratorService
import com.example.domain.ai.AgentContext
import com.example.domain.ai.agents.DuplicateDetectionAgent
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var database: NexusDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, NexusDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("NEXUS", appName)
    }

    @Test
    fun `verify AI Orchestrator executes 6-agent pipeline`() = runBlocking {
        val orchestrator = AIOrchestratorService()

        val incident = IncidentEntity(
            id = "test_inc_1",
            title = "Submerged Expressway Underpass with Trapped Vehicles",
            description = "Flooding over 70cm with high electrical hazard risk from downed lighting pole.",
            category = IncidentCategory.FLOODING,
            status = IncidentStatus.ANALYZING,
            severityScore = 50,
            severityLevel = SeverityLevel.MODERATE,
            impactScore = 50,
            latitude = 37.7749,
            longitude = -122.4194,
            address = "Underpass Sector 4",
            reporterId = "usr_1"
        )

        val departments = listOf(
            DepartmentEntity("dept_water", "Water Management & Drainage Dept", "WATER", "dispatch@water.gov", "555-0100")
        )

        val context = AgentContext(
            incident = incident,
            existingIncidents = emptyList(),
            activeSensors = emptyList(),
            departments = departments
        )

        val result = orchestrator.executeIncidentAnalysisPipeline(context)

        assertNotNull(result)
        assertEquals(IncidentCategory.FLOODING, result.analysis.category)
        assertTrue("Severity should be scored high for submerged trapped vehicles", result.severity.severityScore >= 60)
        assertTrue(result.analysis.hazards.isNotEmpty())
        assertNotNull(result.routing.recommendedDepartment)
        assertTrue(result.strategy.priorityChecklist.isNotEmpty())
    }

    @Test
    fun `verify duplicate detection identifies spatio-temporal cluster`() {
        val agent = DuplicateDetectionAgent()

        val existingIncident = IncidentEntity(
            id = "inc_master_1",
            title = "Water Rising at 12th St Underpass",
            description = "Heavy flooding across roadway.",
            category = IncidentCategory.FLOODING,
            status = IncidentStatus.TRIAGED,
            severityScore = 75,
            severityLevel = SeverityLevel.HIGH,
            impactScore = 60,
            latitude = 37.7749,
            longitude = -122.4194,
            address = "12th St Underpass",
            reporterId = "usr_1"
        )

        val newDuplicateIncident = IncidentEntity(
            id = "inc_dupe_2",
            title = "Water Rising at 12th St Underpass",
            description = "Severe water accumulation at underpass.",
            category = IncidentCategory.FLOODING,
            status = IncidentStatus.ANALYZING,
            severityScore = 50,
            severityLevel = SeverityLevel.MODERATE,
            impactScore = 50,
            latitude = 37.7750, // 10 meters away
            longitude = -122.4195,
            address = "12th St & Market",
            reporterId = "usr_2"
        )

        val context = AgentContext(
            incident = newDuplicateIncident,
            existingIncidents = listOf(existingIncident),
            activeSensors = emptyList(),
            departments = emptyList()
        )

        val analysisResult = IncidentAnalysisResult(
            category = IncidentCategory.FLOODING,
            categoryConfidence = 0.95,
            summary = "Flooding detected",
            hazards = listOf("Flood Hazard"),
            extractedKeywords = listOf("water", "underpass")
        )

        val duplicateResult = agent.detect(context, analysisResult)
        assertTrue("Should detect high similarity duplicate", duplicateResult.similarityScore >= 0.70)
        assertTrue(duplicateResult.isDuplicate)
        assertEquals("inc_master_1", duplicateResult.duplicateIncidentId)
    }
}
