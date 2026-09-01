package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.DepartmentEntity
import com.example.data.model.IncidentStatus
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.NexusViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentDetailScreen(
    incidentId: String,
    viewModel: NexusViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(incidentId) {
        viewModel.selectIncident(incidentId)
    }

    val incident by viewModel.selectedIncident.collectAsState()
    val analysis by viewModel.selectedAIAnalysis.collectAsState()
    val activities by viewModel.selectedIncidentActivities.collectAsState()
    val departments by viewModel.allDepartments.collectAsState()
    val analyzingIncidentId by viewModel.analyzingIncidentId.collectAsState()
    val analysisStage by viewModel.analysisStage.collectAsState()
    val analysisProgress by viewModel.analysisProgress.collectAsState()

    var showDepartmentPicker by remember { mutableStateOf(false) }
    var showStatusPicker by remember { mutableStateOf(false) }
    var operatorNoteText by remember { mutableStateOf("") }

    val isCurrentAnalyzing = analyzingIncidentId == incidentId

    if (incident == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(NexusBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = NexusAccent)
        }
        return
    }

    val currentIncident = incident!!

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "INCIDENT #${currentIncident.id.takeLast(6).uppercase()}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = NexusTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = NexusTextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.rerunAIAnalysis(currentIncident.id) },
                        modifier = Modifier.testTag("btn_rerun_ai")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Rerun AI Analysis",
                            tint = StatusAnalyzing
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NexusGlassCard,
                    titleContentColor = NexusTextPrimary
                )
            )
        },
        containerColor = NexusBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Incident Overview Header Card
            item {
                NexusCard {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CategoryBadge(category = currentIncident.category)
                                SeverityBadge(level = currentIncident.severityLevel)
                            }

                            StatusBadge(status = currentIncident.status)
                        }

                        Text(
                            text = currentIncident.title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.4).sp
                            ),
                            color = NexusTextPrimary
                        )

                        Text(
                            text = currentIncident.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NexusTextSecondary,
                            lineHeight = 20.sp
                        )

                        Divider(color = NexusGlassBorderTop)

                        // Location & Reporter Details
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = NexusAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = currentIncident.address,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = NexusTextPrimary
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = NexusTextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Reported by ${currentIncident.reporterName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexusTextMuted
                                )
                            }
                        }
                    }
                }
            }

            // 2. Incident Triage Action Bar
            item {
                NexusCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Command Actions",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = NexusTextPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Assign Department Button
                            OutlinedButton(
                                onClick = { showDepartmentPicker = true },
                                modifier = Modifier.weight(1f).height(42.dp).testTag("btn_assign_dept"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NexusAccent),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                        listOf(NexusAccent.copy(alpha = 0.8f), NexusAccent.copy(alpha = 0.4f))
                                    )
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AssignmentInd,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (currentIncident.departmentName != null) "Reassign Unit" else "Assign Unit",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                            }

                            // Update Status Button
                            Button(
                                onClick = { showStatusPicker = true },
                                modifier = Modifier.weight(1f).height(42.dp).testTag("btn_change_status"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NexusGlassSurfaceSecondary)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Update Status",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                            }
                        }

                        if (currentIncident.departmentName != null) {
                            Surface(
                                color = NexusGlassSurfaceSecondary,
                                shape = RoundedCornerShape(10.dp),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                        listOf(NexusGlassBorderTop, NexusGlassBorderBottom)
                                    )
                                )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = StatusLow,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Assigned to ${currentIncident.departmentName}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        color = NexusTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Embedded AI Operational Intelligence Panel
            item {
                AIAnalysisPanel(
                    analysis = analysis,
                    isAnalyzing = isCurrentAnalyzing,
                    analysisStage = analysisStage,
                    analysisProgress = analysisProgress,
                    onRerunAnalysis = { viewModel.rerunAIAnalysis(currentIncident.id) }
                )
            }

            // 4. Operator Notes & Timeline Activity Log
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Incident Activity & Timeline",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = NexusTextPrimary
                    )

                    // Add Note Input Field
                    NexusCard {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = operatorNoteText,
                                onValueChange = { operatorNoteText = it },
                                placeholder = {
                                    Text("Add tactical log entry or dispatcher note...", color = NexusTextMuted, fontSize = 13.sp)
                                },
                                modifier = Modifier.fillMaxWidth().height(70.dp).testTag("input_operator_note"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = NexusGlassSurfaceSecondary,
                                    unfocusedContainerColor = NexusGlassSurfaceSecondary,
                                    focusedBorderColor = NexusAccent,
                                    unfocusedBorderColor = NexusGlassBorderTop,
                                    focusedTextColor = NexusTextPrimary,
                                    unfocusedTextColor = NexusTextPrimary
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = {
                                        if (operatorNoteText.isNotBlank()) {
                                            viewModel.addOperatorNote(currentIncident.id, operatorNoteText.trim())
                                            operatorNoteText = ""
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = NexusAccent),
                                    enabled = operatorNoteText.isNotBlank(),
                                    modifier = Modifier.testTag("btn_submit_note")
                                ) {
                                    Text("Post Note")
                                }
                            }
                        }
                    }

                    // Activity Items
                    NexusCard {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            activities.forEachIndexed { index, activity ->
                                ActivityFeedItem(activity = activity)
                                if (index < activities.size - 1) {
                                    Divider(
                                        color = NexusGlassBorderTop,
                                        thickness = 0.8.dp,
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

    // Department Picker Modal
    if (showDepartmentPicker) {
        AlertDialog(
            onDismissRequest = { showDepartmentPicker = false },
            title = {
                Text("Select Responding Department", style = MaterialTheme.typography.titleMedium, color = NexusTextPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    departments.forEach { dept ->
                        Surface(
                            onClick = {
                                viewModel.assignDepartment(currentIncident.id, dept.id, dept.name)
                                showDepartmentPicker = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = NexusGlassSurfaceSecondary,
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    listOf(NexusGlassBorderTop, NexusGlassBorderBottom)
                                )
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Business,
                                    contentDescription = null,
                                    tint = NexusAccent
                                )
                                Column {
                                    Text(
                                        text = dept.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = NexusTextPrimary
                                    )
                                    Text(
                                        text = "${dept.activeUnits} units on duty • ${dept.phone}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NexusTextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDepartmentPicker = false }) {
                    Text("Cancel", color = NexusTextSecondary)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = NexusGlassCard
        )
    }

    // Status Picker Modal
    if (showStatusPicker) {
        AlertDialog(
            onDismissRequest = { showStatusPicker = false },
            title = {
                Text("Update Incident Status", style = MaterialTheme.typography.titleMedium, color = NexusTextPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    IncidentStatus.entries.forEach { status ->
                        Surface(
                            onClick = {
                                viewModel.updateIncidentStatus(currentIncident.id, status)
                                showStatusPicker = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (currentIncident.status == status) NexusAccent.copy(alpha = 0.2f) else NexusGlassSurfaceSecondary,
                            border = if (currentIncident.status == status) CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                    listOf(NexusAccent.copy(alpha = 0.8f), NexusAccent.copy(alpha = 0.4f))
                                )
                            ) else CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    listOf(NexusGlassBorderTop, NexusGlassBorderBottom)
                                )
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                StatusBadge(status = status)
                                Text(
                                    text = status.displayName,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = NexusTextPrimary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showStatusPicker = false }) {
                    Text("Cancel", color = NexusTextSecondary)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = NexusGlassCard
        )
    }
}
