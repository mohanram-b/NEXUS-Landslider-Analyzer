package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.UserEntity
import com.example.ui.components.NexusCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.NexusViewModel

@Composable
fun SettingsScreen(
    viewModel: NexusViewModel,
    onOpenDrawer: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    var showReseedDialog by remember { mutableStateOf(false) }

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
                        text = "System Settings & Diagnostics",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = NexusTextPrimary
                    )
                    Text(
                        text = "Configure platform roles, AI services, and database storage",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTextSecondary
                    )
                }
            }
        }

        // 1. Active User & Role Switcher
        item {
            NexusCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = NexusAccent)
                        Text(
                            text = "Active Operator Profile",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = NexusTextPrimary
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        allUsers.forEach { user ->
                            val isSelected = user.id == currentUser.id
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.switchUser(user) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) NexusAccent.copy(alpha = 0.15f) else NexusGlassSurfaceSecondary,
                                border = if (isSelected) CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                        listOf(NexusAccent.copy(alpha = 0.8f), NexusAccent.copy(alpha = 0.4f))
                                    )
                                ) else CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                        listOf(NexusGlassBorderTop, NexusGlassBorderBottom)
                                    )
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { viewModel.switchUser(user) },
                                        colors = RadioButtonDefaults.colors(selectedColor = NexusAccent)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = user.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = NexusTextPrimary
                                        )
                                        Text(
                                            text = "${user.role.displayName} • ${user.email}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = NexusTextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. AI Intelligence Engine Status
        item {
            NexusCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = StatusAnalyzing)
                            Text(
                                text = "AI Orchestration Architecture",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = NexusTextPrimary
                            )
                        }
                        Surface(
                            color = StatusLowBg,
                            shape = RoundedCornerShape(8.dp),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    listOf(StatusLow.copy(alpha = 0.4f), StatusLow.copy(alpha = 0.1f))
                                )
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(StatusLow))
                                Text("ONLINE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp), color = StatusLow)
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AgentStatusRow("1. Incident Analysis Agent", "Categorization, entity extraction, hazard recognition", true)
                        AgentStatusRow("2. Severity Agent", "Multi-factor explainable risk scoring (0-100)", true)
                        AgentStatusRow("3. Duplicate Detection Agent", "Spatio-temporal Haversine & Jaccard clustering", true)
                        AgentStatusRow("4. Impact Prediction Agent", "Traffic, population & infrastructure risk modeling", true)
                        AgentStatusRow("5. Routing Agent", "Jurisdiction matching & confidence scoring", true)
                        AgentStatusRow("6. Response Strategy Agent", "Automated tactical SOP checklist synthesis", true)
                    }
                }
            }
        }

        // 3. Database Maintenance & Reset
        item {
            NexusCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Storage, contentDescription = null, tint = NexusTextSecondary)
                        Text(
                            text = "Database Management",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = NexusTextPrimary
                        )
                    }

                    Text(
                        text = "Reset and re-populate the local Room database with the initial 40 realistic municipal incidents, 10 telemetry sensor nodes, departments, and event logs.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTextSecondary
                    )

                    OutlinedButton(
                        onClick = { showReseedDialog = true },
                        modifier = Modifier.fillMaxWidth().height(44.dp).testTag("btn_reseed_db"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NexusTextPrimary),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(NexusGlassBorderTop, NexusGlassBorderBottom)
                            )
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reseed Incident & Telemetry Database")
                    }
                }
            }
        }
    }

    if (showReseedDialog) {
        AlertDialog(
            onDismissRequest = { showReseedDialog = false },
            title = { Text("Reseed Database?", color = NexusTextPrimary) },
            text = {
                Text(
                    "This will restore all default 40 incidents, 10 sensor nodes, and dispatch activity feeds.",
                    color = NexusTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAndReseed()
                        showReseedDialog = false
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NexusAccent)
                ) {
                    Text("Confirm Reseed")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReseedDialog = false }) {
                    Text("Cancel", color = NexusTextSecondary)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = NexusGlassCard
        )
    }
}

@Composable
private fun AgentStatusRow(
    title: String,
    desc: String,
    isActive: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = NexusTextPrimary
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.labelSmall,
                color = NexusTextMuted
            )
        }
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Active",
            tint = StatusLow,
            modifier = Modifier.size(16.dp)
        )
    }
}
