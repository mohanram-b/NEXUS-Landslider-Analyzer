package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.SensorEntity
import com.example.data.model.SensorStatus
import com.example.data.model.SensorType
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SensorCard(
    sensor: SensorEntity,
    onSimulateAlert: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (statusColor, statusBg) = when (sensor.status) {
        SensorStatus.ACTIVE -> StatusLow to StatusLowBg
        SensorStatus.WARNING -> StatusHigh to StatusHighBg
        SensorStatus.ALERT -> StatusCritical to StatusCriticalBg
        SensorStatus.OFFLINE -> NexusTextMuted to NexusSurfaceSecondary
    }

    val typeIcon = when (sensor.type) {
        SensorType.WATER_LEVEL, SensorType.RAIN_GAUGE -> Icons.Default.WaterDrop
        SensorType.GRID_POWER_LOAD -> Icons.Default.Bolt
        else -> Icons.Default.Sensors
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sensor_card_${sensor.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NexusGlassCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(
                    if (sensor.status == SensorStatus.ALERT) StatusCritical.copy(alpha = 0.7f) else NexusGlassBorderTop,
                    if (sensor.status == SensorStatus.ALERT) StatusCritical.copy(alpha = 0.3f) else NexusGlassBorderBottom
                )
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(NexusGlassSurface)
                            .border(
                                1.dp,
                                Brush.verticalGradient(listOf(NexusGlassBorderTop, NexusGlassBorderBottom)),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = typeIcon,
                            contentDescription = null,
                            tint = NexusAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = sensor.name,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = NexusTextPrimary
                        )
                        Text(
                            text = sensor.locationName,
                            style = MaterialTheme.typography.labelSmall,
                            color = NexusTextMuted
                        )
                    }
                }

                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(8.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.verticalGradient(
                            listOf(statusColor.copy(alpha = 0.4f), statusColor.copy(alpha = 0.1f))
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Text(
                            text = sensor.status.name,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            color = statusColor
                        )
                    }
                }
            }

            // Metric Value & Threshold
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "LIVE TELEMETRY",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = NexusTextMuted
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${sensor.currentValue}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            ),
                            color = if (sensor.status == SensorStatus.ALERT) StatusCritical else NexusTextPrimary
                        )
                        Text(
                            text = sensor.unit,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NexusTextSecondary
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "ALERT THRESHOLD",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = NexusTextMuted
                    )
                    Text(
                        text = "> ${sensor.threshold} ${sensor.unit}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = NexusTextSecondary
                    )
                }
            }

            // Threshold Progress Gauge
            val ratio = (sensor.currentValue / sensor.threshold).toFloat().coerceIn(0f, 1.5f)
            LinearProgressIndicator(
                progress = { ratio.coerceAtMost(1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (ratio >= 1.0f) StatusCritical else if (ratio >= 0.75f) StatusHigh else NexusAccent,
                trackColor = NexusGlassSurfaceSecondary
            )

            // Simulate Alert Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                Text(
                    text = "Updated: ${timeFormat.format(Date(sensor.lastUpdated))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = NexusTextMuted
                )

                OutlinedButton(
                    onClick = onSimulateAlert,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusCritical),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(listOf(StatusCritical.copy(alpha = 0.6f), StatusCritical.copy(alpha = 0.2f)))
                    ),
                    modifier = Modifier.height(32.dp).testTag("btn_simulate_alert_${sensor.id}")
                ) {
                    Text(
                        text = "Simulate Surge",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium, fontSize = 11.sp)
                    )
                }
            }
        }
    }
}
