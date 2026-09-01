package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SensorStatus
import com.example.ui.components.NexusCard
import com.example.ui.components.SensorCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.NexusViewModel

@Composable
fun SensorsScreen(
    viewModel: NexusViewModel,
    onNavigateToDetail: (String) -> Unit,
    onOpenDrawer: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val sensors by viewModel.allSensors.collectAsState()

    val activeCount = sensors.count { it.status == SensorStatus.ACTIVE }
    val warningCount = sensors.count { it.status == SensorStatus.WARNING }
    val alertCount = sensors.count { it.status == SensorStatus.ALERT }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NexusBackground),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (onOpenDrawer != null) {
                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(NexusGlassSurface)
                            .border(
                                1.dp,
                                Brush.verticalGradient(listOf(NexusGlassBorderTop, NexusGlassBorderBottom)),
                                RoundedCornerShape(10.dp)
                            )
                            .testTag("btn_open_drawer")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open Navigation Menu",
                            tint = NexusAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "IoT Sensor Network",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = NexusTextPrimary
                    )
                    Text(
                        text = "Real-time telemetry from 10 municipal sensor nodes",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTextSecondary
                    )
                }
            }
        }

        // Status Summary Pills
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    color = StatusLowBg,
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.verticalGradient(
                            listOf(StatusLow.copy(alpha = 0.4f), StatusLow.copy(alpha = 0.1f))
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("NORMAL", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold), color = StatusLow)
                        Text("$activeCount Nodes", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = StatusLow)
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    color = StatusHighBg,
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.verticalGradient(
                            listOf(StatusHigh.copy(alpha = 0.4f), StatusHigh.copy(alpha = 0.1f))
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("WARNING", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold), color = StatusHigh)
                        Text("$warningCount Nodes", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = StatusHigh)
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    color = StatusCriticalBg,
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.verticalGradient(
                            listOf(StatusCritical.copy(alpha = 0.4f), StatusCritical.copy(alpha = 0.1f))
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("ALERT", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold), color = StatusCritical)
                        Text("$alertCount Nodes", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = StatusCritical)
                    }
                }
            }
        }

        // Full Automated Simulation Flow Banner
        item {
            NexusCard(
                borderColor = NexusAccent.copy(alpha = 0.5f)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = null,
                            tint = NexusAccent
                        )
                        Text(
                            text = "Interactive Sensor Surge Demo",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = NexusTextPrimary
                        )
                    }
                    Text(
                        text = "Triggering a surge simulates an environmental spike above safe operational limits, automatically registering a verified incident and executing the AI triage pipeline.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTextSecondary
                    )
                    Button(
                        onClick = {
                            viewModel.triggerSimulateFloodDemo { newId ->
                                onNavigateToDetail(newId)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(40.dp).testTag("btn_trigger_flood_workflow"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NexusAccent)
                    ) {
                        Icon(imageVector = Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Trigger Flood Sensor & Auto-Incident Flow", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        // All Sensor Cards
        items(sensors, key = { it.id }) { sensor ->
            SensorCard(
                sensor = sensor,
                onSimulateAlert = { viewModel.simulateSensorAlert(sensor.id) }
            )
        }
    }
}
