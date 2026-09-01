package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.IncidentCategory
import com.example.data.model.IncidentStatus
import com.example.data.model.SeverityLevel
import com.example.ui.components.NexusCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.NexusViewModel

@Composable
fun AnalyticsScreen(
    viewModel: NexusViewModel,
    onOpenDrawer: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val allIncidents by viewModel.allIncidents.collectAsState()
    val departments by viewModel.allDepartments.collectAsState()
    val metrics by viewModel.dashboardMetrics.collectAsState()

    val total = allIncidents.size.coerceAtLeast(1)

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
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    listOf(NexusGlassBorderTop, NexusGlassBorderBottom)
                                ),
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
                        text = "Operational Analytics",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = NexusTextPrimary
                    )
                    Text(
                        text = "Aggregated intelligence across $total municipal incidents",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTextSecondary
                    )
                }
            }
        }

        // Summary Performance Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NexusCard(modifier = Modifier.weight(1f)) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("TOTAL INCIDENTS", style = MaterialTheme.typography.labelSmall, color = NexusTextMuted)
                        Text("$total", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = NexusTextPrimary)
                        Text("100% AI triaged", style = MaterialTheme.typography.bodySmall, color = StatusLow)
                    }
                }
                NexusCard(modifier = Modifier.weight(1f)) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("RESOLUTION RATE", style = MaterialTheme.typography.labelSmall, color = NexusTextMuted)
                        Text(metrics.resolutionRatePercentage, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF818CF8))
                        Text("SLA on target", style = MaterialTheme.typography.bodySmall, color = NexusTextSecondary)
                    }
                }
            }
        }

        // 1. Severity Distribution
        item {
            NexusCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Severity Classification Distribution",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = NexusTextPrimary
                    )

                    SeverityLevel.entries.forEach { level ->
                        val count = allIncidents.count { it.severityLevel == level }
                        val ratio = count.toFloat() / total
                        val barColor = when (level) {
                            SeverityLevel.CRITICAL -> StatusCritical
                            SeverityLevel.HIGH -> StatusHigh
                            SeverityLevel.MODERATE -> StatusModerate
                            SeverityLevel.LOW -> StatusLow
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = level.displayName,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = NexusTextPrimary
                                )
                                Text(
                                    text = "$count incidents (${(ratio * 100).toInt()}%)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NexusTextMuted
                                )
                            }
                            LinearProgressIndicator(
                                progress = { ratio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = barColor,
                                trackColor = NexusGlassSurfaceSecondary
                            )
                        }
                    }
                }
            }
        }

        // 2. Incident Breakdown by Category
        item {
            NexusCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Incidents by Hazard Category",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = NexusTextPrimary
                    )

                    IncidentCategory.entries.forEach { category ->
                        val count = allIncidents.count { it.category == category }
                        val ratio = count.toFloat() / total

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = category.displayName,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = NexusTextPrimary
                                )
                                Text(
                                    text = "$count (${(ratio * 100).toInt()}%)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NexusTextMuted
                                )
                            }
                            LinearProgressIndicator(
                                progress = { ratio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = NexusAccent,
                                trackColor = NexusGlassSurfaceSecondary
                            )
                        }
                    }
                }
            }
        }

        // 3. Departmental Dispatch Load
        item {
            NexusCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Department Workload Allocation",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = NexusTextPrimary
                    )

                    departments.forEach { dept ->
                        val assignedCount = allIncidents.count { it.departmentId == dept.id }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = dept.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = NexusTextPrimary
                                )
                                Text(
                                    text = "${dept.activeUnits} response units available",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NexusTextMuted
                                )
                            }
                            Surface(
                                color = NexusGlassSurfaceSecondary,
                                shape = RoundedCornerShape(8.dp),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                        listOf(NexusGlassBorderTop, NexusGlassBorderBottom)
                                    )
                                )
                            ) {
                                Text(
                                    text = "$assignedCount Assigned",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = NexusAccent
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
