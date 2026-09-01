package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.AIAnalysisEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AIAnalysisDao {
    @Query("SELECT * FROM ai_analyses WHERE incidentId = :incidentId LIMIT 1")
    fun getAnalysisForIncidentFlow(incidentId: String): Flow<AIAnalysisEntity?>

    @Query("SELECT * FROM ai_analyses WHERE incidentId = :incidentId LIMIT 1")
    suspend fun getAnalysisForIncident(incidentId: String): AIAnalysisEntity?

    @Query("SELECT * FROM ai_analyses ORDER BY createdAt DESC")
    fun getAllAnalyses(): Flow<List<AIAnalysisEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysis(analysis: AIAnalysisEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalyses(analyses: List<AIAnalysisEntity>)

    @Query("SELECT COUNT(*) FROM ai_analyses")
    suspend fun getAnalysisCount(): Int
}
