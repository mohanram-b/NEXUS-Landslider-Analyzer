package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.DepartmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DepartmentDao {
    @Query("SELECT * FROM departments ORDER BY name ASC")
    fun getAllDepartments(): Flow<List<DepartmentEntity>>

    @Query("SELECT * FROM departments WHERE id = :id")
    suspend fun getDepartmentById(id: String): DepartmentEntity?

    @Query("SELECT * FROM departments WHERE name LIKE '%' || :name || '%' LIMIT 1")
    suspend fun findDepartmentByName(name: String): DepartmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepartment(department: DepartmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepartments(departments: List<DepartmentEntity>)

    @Query("SELECT COUNT(*) FROM departments")
    suspend fun getDepartmentCount(): Int
}
