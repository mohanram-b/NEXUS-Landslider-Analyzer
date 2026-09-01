package com.example.domain.realtime

import com.example.data.local.entities.IncidentEntity
import com.example.data.local.entities.SensorEntity
import com.example.data.model.FullAIIncidentAnalysis
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class NexusRealtimeEvent {
    data class IncidentCreated(val incident: IncidentEntity) : NexusRealtimeEvent()
    data class IncidentAnalyzing(val incidentId: String, val stage: String, val progress: Float) : NexusRealtimeEvent()
    data class IncidentAnalyzed(val incidentId: String, val analysis: FullAIIncidentAnalysis) : NexusRealtimeEvent()
    data class IncidentAssigned(val incidentId: String, val departmentName: String) : NexusRealtimeEvent()
    data class IncidentResolved(val incidentId: String) : NexusRealtimeEvent()
    data class SensorReading(val sensorId: String, val value: Double, val unit: String) : NexusRealtimeEvent()
    data class SensorAlert(val sensor: SensorEntity, val readingValue: Double, val message: String) : NexusRealtimeEvent()
}

object RealtimeEventBus {
    private val _events = MutableSharedFlow<NexusRealtimeEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<NexusRealtimeEvent> = _events.asSharedFlow()

    suspend fun emit(event: NexusRealtimeEvent) {
        _events.emit(event)
    }

    fun tryEmit(event: NexusRealtimeEvent) {
        _events.tryEmit(event)
    }
}
