package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
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
import com.example.data.model.IncidentStatus
import com.example.data.model.SeverityLevel
import com.example.ui.components.IncidentCard
import com.example.ui.components.NexusCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.NexusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentsScreen(
    viewModel: NexusViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToReport: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val incidents by viewModel.filteredIncidents.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val allIncidents by viewModel.allIncidents.collectAsState()

    var showFiltersSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NexusBackground)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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

                    Column {
                        Text(
                            text = "Incident Repository",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = NexusTextPrimary
                        )
                        Text(
                            text = "Managing ${allIncidents.size} verified municipal records",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTextSecondary
                        )
                    }
                }

                Button(
                    onClick = onNavigateToReport,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NexusAccent),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("btn_new_incident")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "New Report",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            // Search Bar
            OutlinedTextField(
                value = filterState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = {
                    Text("Search by incident title, sector, hazard...", color = NexusTextMuted, fontSize = 13.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = NexusTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (filterState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = NexusTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("incident_search_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = NexusGlassCard,
                    unfocusedContainerColor = NexusGlassCard,
                    focusedBorderColor = NexusAccent,
                    unfocusedBorderColor = NexusGlassBorderTop,
                    focusedTextColor = NexusTextPrimary,
                    unfocusedTextColor = NexusTextPrimary
                ),
                singleLine = true
            )

            // Category Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterState.categoryFilter == null,
                    onClick = { viewModel.updateCategoryFilter(null) },
                    label = { Text("All Categories") },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NexusAccent,
                        selectedLabelColor = Color.White,
                        containerColor = NexusGlassSurfaceSecondary,
                        labelColor = NexusTextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = NexusGlassBorderBottom,
                        selectedBorderColor = NexusAccent,
                        enabled = true,
                        selected = filterState.categoryFilter == null
                    )
                )

                IncidentCategory.entries.forEach { category ->
                    FilterChip(
                        selected = filterState.categoryFilter == category,
                        onClick = {
                            viewModel.updateCategoryFilter(
                                if (filterState.categoryFilter == category) null else category
                            )
                        },
                        label = { Text(category.displayName) },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NexusAccent,
                            selectedLabelColor = Color.White,
                            containerColor = NexusGlassSurfaceSecondary,
                            labelColor = NexusTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = NexusGlassBorderBottom,
                            selectedBorderColor = NexusAccent,
                            enabled = true,
                            selected = filterState.categoryFilter == category
                        )
                    )
                }
            }

            // Severity Filter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterState.severityFilter == null,
                    onClick = { viewModel.updateSeverityFilter(null) },
                    label = { Text("All Severities") },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NexusGlassSurface,
                        selectedLabelColor = NexusTextPrimary,
                        containerColor = NexusGlassSurfaceSecondary,
                        labelColor = NexusTextMuted
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = NexusGlassBorderBottom,
                        selectedBorderColor = NexusGlassBorderTop,
                        enabled = true,
                        selected = filterState.severityFilter == null
                    )
                )

                SeverityLevel.entries.forEach { level ->
                    FilterChip(
                        selected = filterState.severityFilter == level,
                        onClick = {
                            viewModel.updateSeverityFilter(
                                if (filterState.severityFilter == level) null else level
                            )
                        },
                        label = { Text(level.displayName) },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (level) {
                                SeverityLevel.CRITICAL -> StatusCriticalBg
                                SeverityLevel.HIGH -> StatusHighBg
                                SeverityLevel.MODERATE -> StatusModerateBg
                                SeverityLevel.LOW -> StatusLowBg
                            },
                            selectedLabelColor = when (level) {
                                SeverityLevel.CRITICAL -> StatusCritical
                                SeverityLevel.HIGH -> StatusHigh
                                SeverityLevel.MODERATE -> StatusModerate
                                SeverityLevel.LOW -> StatusLow
                            },
                            containerColor = NexusGlassSurfaceSecondary,
                            labelColor = NexusTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = NexusGlassBorderBottom,
                            selectedBorderColor = when (level) {
                                SeverityLevel.CRITICAL -> StatusCritical
                                SeverityLevel.HIGH -> StatusHigh
                                SeverityLevel.MODERATE -> StatusModerate
                                SeverityLevel.LOW -> StatusLow
                            },
                            enabled = true,
                            selected = filterState.severityFilter == level
                        )
                    )
                }
            }
        }

        HorizontalDivider(color = NexusGlassBorderBottom, thickness = 1.dp)

        // Incidents List
        if (incidents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = NexusTextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No matching incidents found",
                        style = MaterialTheme.typography.titleMedium,
                        color = NexusTextPrimary
                    )
                    Text(
                        text = "Try adjusting your search query or removing active filters",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTextSecondary
                    )
                    OutlinedButton(
                        onClick = { viewModel.clearFilters() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NexusAccent)
                    ) {
                        Text("Reset All Filters")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(incidents, key = { it.id }) { incident ->
                    IncidentCard(
                        incident = incident,
                        onClick = { onNavigateToDetail(incident.id) }
                    )
                }
            }
        }
    }
}
