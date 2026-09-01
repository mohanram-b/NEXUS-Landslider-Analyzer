package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.data.model.IncidentCategory
import com.example.ui.components.CategoryBadge
import com.example.ui.components.NexusCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.NexusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIncidentScreen(
    viewModel: NexusViewModel,
    onBack: () -> Unit,
    onIncidentCreated: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(1) } // 1: Category & Title, 2: Description & Location, 3: Review & Submit

    var selectedCategory by remember { mutableStateOf(IncidentCategory.FLOODING) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("Market St & 8th Ave, Sector 3") }
    var latitude by remember { mutableDoubleStateOf(37.7780) }
    var longitude by remember { mutableDoubleStateOf(-122.4150) }
    var isSubmitting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "REPORT INCIDENT",
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
            // Step Progress Bar
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STEP $step OF 3",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = NexusAccent)
                        )
                        Text(
                            text = when (step) {
                                1 -> "Incident Classification"
                                2 -> "Incident Details & Location"
                                else -> "Review & AI Orchestration"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = NexusTextSecondary
                        )
                    }

                    LinearProgressIndicator(
                        progress = { step / 3f },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                        color = NexusAccent,
                        trackColor = NexusGlassSurfaceSecondary
                    )
                }
            }

            // Step 1: Category & Title
            if (step == 1) {
                item {
                    NexusCard {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text(
                                text = "Select Incident Category",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = NexusTextPrimary
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                IncidentCategory.entries.forEach { category ->
                                    val isSelected = selectedCategory == category
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedCategory = category },
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
                                                onClick = { selectedCategory = category },
                                                colors = RadioButtonDefaults.colors(selectedColor = NexusAccent)
                                            )
                                            Text(
                                                text = category.displayName,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                                color = if (isSelected) NexusTextPrimary else NexusTextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    NexusCard {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Incident Title",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = NexusTextPrimary
                            )

                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                placeholder = {
                                    Text("E.g., Rising Water Depth Blocking South Tunnel", color = NexusTextMuted, fontSize = 13.sp)
                                },
                                modifier = Modifier.fillMaxWidth().testTag("input_incident_title"),
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
                        }
                    }
                }

                item {
                    Button(
                        onClick = { if (title.isNotBlank()) step = 2 },
                        enabled = title.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_step1_next"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NexusAccent)
                    ) {
                        Text("Next: Details & Location", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Step 2: Description & Location
            if (step == 2) {
                item {
                    NexusCard {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Incident Description",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = NexusTextPrimary
                            )

                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                placeholder = {
                                    Text("Provide specific operational details: estimated depth, downed power lines, traffic blockage, immediate hazards...", color = NexusTextMuted, fontSize = 13.sp)
                                },
                                modifier = Modifier.fillMaxWidth().height(120.dp).testTag("input_incident_description"),
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
                        }
                    }
                }

                item {
                    NexusCard {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Incident Address & Location",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = NexusTextPrimary
                            )

                            OutlinedTextField(
                                value = address,
                                onValueChange = { address = it },
                                placeholder = {
                                    Text("Enter street address or intersection", color = NexusTextMuted, fontSize = 13.sp)
                                },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = NexusAccent)
                                },
                                modifier = Modifier.fillMaxWidth().testTag("input_incident_address"),
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
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = NexusGlassSurfaceSecondary,
                                    shape = RoundedCornerShape(8.dp),
                                    border = CardDefaults.outlinedCardBorder().copy(
                                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                            listOf(NexusGlassBorderTop, NexusGlassBorderBottom)
                                        )
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Lat: $latitude",
                                        modifier = Modifier.padding(8.dp),
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
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Lng: $longitude",
                                        modifier = Modifier.padding(8.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NexusTextMuted
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { step = 1 },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NexusTextSecondary),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    listOf(NexusGlassBorderTop, NexusGlassBorderBottom)
                                )
                            )
                        ) {
                            Text("Back")
                        }

                        Button(
                            onClick = { if (description.isNotBlank() && address.isNotBlank()) step = 3 },
                            enabled = description.isNotBlank() && address.isNotBlank(),
                            modifier = Modifier.weight(1f).height(48.dp).testTag("btn_step2_next"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NexusAccent)
                        ) {
                            Text("Review Report", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Step 3: Review & Submit
            if (step == 3) {
                item {
                    NexusCard {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text(
                                text = "Confirm Incident Summary",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = NexusTextPrimary
                            )

                            CategoryBadge(category = selectedCategory)

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "TITLE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NexusTextMuted
                                )
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = NexusTextPrimary
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "DESCRIPTION",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NexusTextMuted
                                )
                                Text(
                                    text = description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexusTextSecondary
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "LOCATION",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NexusTextMuted
                                )
                                Text(
                                    text = address,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexusTextPrimary
                                )
                            }
                        }
                    }
                }

                item {
                    NexusCard(
                        borderColor = StatusAnalyzing.copy(alpha = 0.5f),
                        backgroundColor = StatusAnalyzingBg.copy(alpha = 0.05f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = StatusAnalyzing,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = "AI Orchestration Trigger",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = NexusTextPrimary
                                )
                                Text(
                                    text = "Submitting will immediately execute the 6-agent AI intelligence pipeline (Analysis → Severity → Duplicate → Impact → Routing → SOP Response).",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexusTextSecondary
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { step = 2 },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NexusTextSecondary)
                        ) {
                            Text("Back")
                        }

                        Button(
                            onClick = {
                                if (!isSubmitting) {
                                    isSubmitting = true
                                    viewModel.createIncidentReport(
                                        title = title,
                                        description = description,
                                        category = selectedCategory,
                                        latitude = latitude,
                                        longitude = longitude,
                                        address = address,
                                        photos = emptyList()
                                    ) { newId ->
                                        onIncidentCreated(newId)
                                    }
                                }
                            },
                            enabled = !isSubmitting,
                            modifier = Modifier.weight(1f).height(48.dp).testTag("btn_submit_incident"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NexusAccent)
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Text("Submit & Run AI", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}
