package com.example.data.local

import android.content.Context
import androidx.room.*
import com.example.data.local.dao.*
import com.example.data.local.entities.*
import com.example.data.model.*

class RoomConverters {
    @TypeConverter
    fun fromIncidentCategory(value: IncidentCategory): String = value.name

    @TypeConverter
    fun toIncidentCategory(value: String): IncidentCategory = IncidentCategory.fromString(value)

    @TypeConverter
    fun fromIncidentStatus(value: IncidentStatus): String = value.name

    @TypeConverter
    fun toIncidentStatus(value: String): IncidentStatus = IncidentStatus.fromString(value)

    @TypeConverter
    fun fromSeverityLevel(value: SeverityLevel): String = value.name

    @TypeConverter
    fun toSeverityLevel(value: String): SeverityLevel = SeverityLevel.fromString(value)

    @TypeConverter
    fun fromUserRole(value: UserRole): String = value.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = UserRole.fromString(value)

    @TypeConverter
    fun fromSensorType(value: SensorType): String = value.name

    @TypeConverter
    fun toSensorType(value: String): SensorType = SensorType.fromString(value)

    @TypeConverter
    fun fromSensorStatus(value: SensorStatus): String = value.name

    @TypeConverter
    fun toSensorStatus(value: String): SensorStatus = SensorStatus.valueOf(value)

    @TypeConverter
    fun fromActivityType(value: ActivityType): String = value.name

    @TypeConverter
    fun toActivityType(value: String): ActivityType = ActivityType.valueOf(value)
}

@Database(
    entities = [
        UserEntity::class,
        DepartmentEntity::class,
        IncidentEntity::class,
        AIAnalysisEntity::class,
        IncidentEvidenceEntity::class,
        SensorEntity::class,
        SensorReadingEntity::class,
        ActivityEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class NexusDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun departmentDao(): DepartmentDao
    abstract fun incidentDao(): IncidentDao
    abstract fun aiAnalysisDao(): AIAnalysisDao
    abstract fun sensorDao(): SensorDao
    abstract fun activityDao(): ActivityDao

    companion object {
        @Volatile
        private var INSTANCE: NexusDatabase? = null

        fun getInstance(context: Context): NexusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NexusDatabase::class.java,
                    "nexus_intelligence.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
