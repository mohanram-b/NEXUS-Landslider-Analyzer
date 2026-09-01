package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SeverityLevel
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.NexusViewModel

@Composable
fun DashboardScreen(
    viewModel: NexusViewModel,
    onNavigateToIncidents: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToSensors: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val metrics by viewModel.dashboardMetrics.collectAsState()
    val activeIncidents by viewModel.activeIncidents.collectAsState()
    val sensors by viewModel.allSensors.collectAsState()
    val activities by viewModel.liveActivityFeed.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val realtimeAlert by viewModel.activeRealtimeAlert.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NexusBackground),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Top Real-time alert banner (if active)
        item {
            LiveAlertBanner(
                message = realtimeAlert,
                onDismiss = { viewModel.dismissAlertBanner() }
            )
        }

        // 2. Main Executive Header & Demo Banner
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
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

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(StatusLow)
                                )
                                Text(
                                    text = "NEXUS COMMAND CENTER",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = NexusAccent
                                )
                            }
                            Text(
                                text = "Incident Intelligence",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = NexusTextPrimary
                            )
                        }
                    }

                    Surface(
                        color = NexusGlassSurface,
                        shape = RoundedCornerShape(10.dp),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.verticalGradient(listOf(NexusGlassBorderTop, NexusGlassBorderBottom))
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(NexusAccent)
                            )
                            Text(
                                text = currentUser.role.displayName.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.sp
                                ),
                                color = NexusTextSecondary
                            )
                        }
                    }
                }

                // Interactive Demo Card: "SIMULATE FLOOD EVENT"
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("simulate_flood_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NexusGlassCard),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            listOf(NexusAccent.copy(alpha = 0.8f), StatusCritical.copy(alpha = 0.5f))
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = null,
                                    tint = NexusAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Automated AI Incident Flow Demo",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = NexusTextPrimary
                                )
                            }
                            Surface(
                                color = NexusAccent.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.verticalGradient(
                                        listOf(NexusAccent.copy(alpha = 0.4f), NexusAccent.copy(alpha = 0.1f))
                                    )
                                )
                            ) {
                                Text(
                                    text = "1-CLICK DEMO",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = NexusAccent
                                )
                            }
                        }

                        Text(
                            text = "Simulate an IoT sensor surge (76.5cm water depth) that triggers an automated incident, activates the 6-agent AI intelligence pipeline, and dispatches to Water Management.",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTextSecondary,
                            lineHeight = 18.sp
                        )

                        Button(
                            onClick = {
                                viewModel.triggerSimulateFloodDemo { newIncidentId ->
                                    onNavigateToDetail(newIncidentId)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(44.dp).testTag("btn_simulate_flood_event"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NexusAccent,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Simulate Flood Event & Run AI Pipeline",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }
            }
        }

        // 3. Operational Metrics Grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Active Incidents",
                        value = "${metrics.activeCount}",
                        subtitle = "Requiring response",
                        icon = Icons.Outlined.Emergency,
                        accentColor = NexusAccent,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Critical Hazards",
                        value = "${metrics.criticalCount}",
                        subtitle = "Immediate priority",
                        icon = Icons.Outlined.Warning,
                        accentColor = StatusCritical,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Avg Triage Time",
                        value = metrics.avgResponseTimeMinutes,
                        subtitle = "AI automated scoring",
                        icon = Icons.Outlined.Speed,
                        accentColor = StatusLow,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Resolution Rate",
                        value = metrics.resolutionRatePercentage,
                        subtitle = "Closed & stabilized",
                        icon = Icons.Outlined.TaskAlt,
                        accentColor = Color(0xFF818CF8),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. Critical & Priority Active Incidents Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Priority Incidents",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = NexusTextPrimary
                        )
                        Surface(
                            color = NexusGlassSurfaceSecondary,
                            shape = RoundedCornerShape(10.dp),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.verticalGradient(listOf(NexusGlassBorderTop, NexusGlassBorderBottom))
                            )
                        ) {
                            Text(
                                text = "${activeIncidents.size}",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = NexusTextSecondary
                            )
                        }
                    }

                    TextButton(onClick = onNavigateToIncidents) {
                        Text(
                            text = "View All",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = NexusAccent
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = NexusAccent,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                val topActive = activeIncidents.take(5)
                topActive.forEach { incident ->
                    IncidentCard(
                        incident = incident,
                        onClick = { onNavigateToDetail(incident.id) }
                    )
                }
            }
        }

        // 5. Live Telemetry Sensors Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(StatusLow)
                        )
                        Text(
                            text = "Environmental & Grid Sensors",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = NexusTextPrimary
                        )
                    }

                    TextButton(onClick = onNavigateToSensors) {
                        Text(
                            text = "Manage Nodes",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = NexusAccent
                        )
                    }
                }

                val keySensors = sensors.take(3)
                keySensors.forEach { sensor ->
                    SensorCard(
                        sensor = sensor,
                        onSimulateAlert = { viewModel.simulateSensorAlert(sensor.id) }
                    )
                }
            }
        }

        // 6. Live Activity Feed Ticker
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Live Operations Stream",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = NexusTextPrimary
                )

                NexusCard {
                    val recentActivities = activities.take(6)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        recentActivities.forEachIndexed { index, activity ->
                            ActivityFeedItem(activity = activity)
                            if (index < recentActivities.size - 1) {
                                HorizontalDivider(
                                    color = NexusGlassBorderBottom,
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(start = 38.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
