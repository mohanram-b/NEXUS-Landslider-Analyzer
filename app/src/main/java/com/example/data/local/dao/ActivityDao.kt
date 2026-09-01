package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.ActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities ORDER BY createdAt DESC LIMIT 50")
    fun getLiveActivityFeed(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE incidentId = :incidentId ORDER BY createdAt DESC")
    fun getActivitiesForIncident(incidentId: String): Flow<List<ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<ActivityEntity>)

    @Query("SELECT COUNT(*) FROM activities")
    suspend fun getActivityCount(): Int
}
