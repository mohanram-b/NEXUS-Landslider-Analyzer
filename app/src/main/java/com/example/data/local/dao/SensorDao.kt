package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.SensorEntity
import com.example.data.local.entities.SensorReadingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SensorDao {
    @Query("SELECT * FROM sensors ORDER BY name ASC")
    fun getAllSensors(): Flow<List<SensorEntity>>

    @Query("SELECT * FROM sensors WHERE id = :id")
    fun getSensorByIdFlow(id: String): Flow<SensorEntity?>

    @Query("SELECT * FROM sensors WHERE id = :id")
    suspend fun getSensorById(id: String): SensorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSensor(sensor: SensorEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSensors(sensors: List<SensorEntity>)

    @Update
    suspend fun updateSensor(sensor: SensorEntity)

    @Query("UPDATE sensors SET currentValue = :value, status = :status, lastUpdated = :timestamp WHERE id = :id")
    suspend fun updateSensorReading(id: String, value: Double, status: com.example.data.model.SensorStatus, timestamp: Long = System.currentTimeMillis())

    // Sensor Readings
    @Query("SELECT * FROM sensor_readings WHERE sensorId = :sensorId ORDER BY recordedAt DESC LIMIT 20")
    fun getReadingsForSensor(sensorId: String): Flow<List<SensorReadingEntity>>

    @Query("SELECT * FROM sensor_readings ORDER BY recordedAt DESC LIMIT 50")
    fun getRecentReadings(): Flow<List<SensorReadingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: SensorReadingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadings(readings: List<SensorReadingEntity>)

    @Query("SELECT COUNT(*) FROM sensors")
    suspend fun getSensorCount(): Int
}
