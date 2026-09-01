package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.IncidentEntity
import com.example.data.local.entities.IncidentEvidenceEntity
import com.example.data.model.IncidentCategory
import com.example.data.model.IncidentStatus
import com.example.data.model.SeverityLevel
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentDao {
    @Query("SELECT * FROM incidents ORDER BY createdAt DESC")
    fun getAllIncidents(): Flow<List<IncidentEntity>>

    @Query("SELECT * FROM incidents WHERE status != 'RESOLVED' ORDER BY severityScore DESC, createdAt DESC")
    fun getActiveIncidents(): Flow<List<IncidentEntity>>

    @Query("SELECT * FROM incidents WHERE id = :id")
    fun getIncidentByIdFlow(id: String): Flow<IncidentEntity?>

    @Query("SELECT * FROM incidents WHERE id = :id")
    suspend fun getIncidentById(id: String): IncidentEntity?

    @Query("SELECT * FROM incidents WHERE id != :currentId ORDER BY createdAt DESC LIMIT 30")
    suspend fun getPotentialDuplicates(currentId: String): List<IncidentEntity>

    @Query("SELECT * FROM incidents WHERE category = :category ORDER BY createdAt DESC")
    fun getIncidentsByCategory(category: IncidentCategory): Flow<List<IncidentEntity>>

    @Query("SELECT * FROM incidents WHERE severityLevel = :level ORDER BY createdAt DESC")
    fun getIncidentsBySeverity(level: SeverityLevel): Flow<List<IncidentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: IncidentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncidents(incidents: List<IncidentEntity>)

    @Update
    suspend fun updateIncident(incident: IncidentEntity)

    @Query("UPDATE incidents SET status = :status, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateIncidentStatus(id: String, status: IncidentStatus, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE incidents SET departmentId = :deptId, departmentName = :deptName, status = 'ASSIGNED', updatedAt = :timestamp WHERE id = :id")
    suspend fun assignDepartment(id: String, deptId: String, deptName: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM incidents WHERE id = :id")
    suspend fun deleteIncidentById(id: String)

    @Query("SELECT COUNT(*) FROM incidents")
    suspend fun getIncidentCount(): Int

    // Evidence
    @Query("SELECT * FROM incident_evidence WHERE incidentId = :incidentId ORDER BY createdAt ASC")
    fun getEvidenceForIncident(incidentId: String): Flow<List<IncidentEvidenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidence(evidence: IncidentEvidenceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidenceList(evidenceList: List<IncidentEvidenceEntity>)
}
