package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.IncidentEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun IncidentCard(
    incident: IncidentEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
    val timeStr = dateFormat.format(Date(incident.createdAt))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("incident_item_${incident.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NexusGlassCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(NexusGlassBorderTop, NexusGlassBorderBottom)
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryBadge(category = incident.category)
                    SeverityBadge(level = incident.severityLevel)
                }

                StatusBadge(status = incident.status)
            }

            // Title and Description
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = incident.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        letterSpacing = (-0.2).sp
                    ),
                    color = NexusTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = incident.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = NexusTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            HorizontalDivider(
                color = NexusGlassBorderBottom,
                thickness = 1.dp
            )

            // Bottom metadata row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = NexusTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = incident.address,
                        style = MaterialTheme.typography.labelSmall,
                        color = NexusTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (incident.isSensorTriggered) {
                        Surface(
                            color = NexusAccent.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.verticalGradient(
                                    listOf(NexusAccent.copy(alpha = 0.4f), NexusAccent.copy(alpha = 0.1f))
                                )
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sensors,
                                    contentDescription = null,
                                    tint = NexusAccent,
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    text = "SENSOR",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                    color = NexusAccent
                                )
                            }
                        }
                    }

                    Text(
                        text = timeStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = NexusTextMuted
                    )

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "View Details",
                        tint = NexusTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
