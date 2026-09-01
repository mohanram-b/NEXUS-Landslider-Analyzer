package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun NexusCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    borderColor: Color? = null,
    backgroundColor: Color = NexusGlassCard,
    content: @Composable ColumnScope.() -> Unit
) {
    val borderBrush = if (borderColor != null) {
        Brush.verticalGradient(listOf(borderColor, borderColor.copy(alpha = 0.4f)))
    } else {
        Brush.verticalGradient(listOf(NexusGlassBorderTop, NexusGlassBorderBottom))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = CardDefaults.outlinedCardBorder().copy(brush = borderBrush)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color = NexusAccent,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("metric_card_${title.lowercase().replace(" ", "_")}"),
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = NexusTextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.18f))
                        .border(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(accentColor.copy(alpha = 0.4f), accentColor.copy(alpha = 0.1f))
                            ),
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = NexusTextPrimary
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = NexusTextMuted
            )
        }
    }
}

@Composable
fun SeverityBadge(
    level: SeverityLevel,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, dotColor) = when (level) {
        SeverityLevel.CRITICAL -> Triple(StatusCriticalBg, StatusCritical, StatusCritical)
        SeverityLevel.HIGH -> Triple(StatusHighBg, StatusHigh, StatusHigh)
        SeverityLevel.MODERATE -> Triple(StatusModerateBg, StatusModerate, StatusModerate)
        SeverityLevel.LOW -> Triple(StatusLowBg, StatusLow, StatusLow)
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(textColor.copy(alpha = 0.4f), textColor.copy(alpha = 0.12f))
                ),
                RoundedCornerShape(8.dp)
            ),
        color = bgColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Text(
                text = level.displayName.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp
                ),
                color = textColor
            )
        }
    }
}

@Composable
fun StatusBadge(
    status: IncidentStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        IncidentStatus.REPORTED -> Color(0x3327272A) to NexusTextSecondary
        IncidentStatus.ANALYZING -> StatusAnalyzingBg to StatusAnalyzing
        IncidentStatus.TRIAGED -> StatusModerateBg to StatusModerate
        IncidentStatus.ASSIGNED -> Color(0x266366F1) to Color(0xFF818CF8)
        IncidentStatus.IN_PROGRESS -> StatusHighBg to StatusHigh
        IncidentStatus.RESOLVED -> StatusResolvedBg to StatusResolved
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(textColor.copy(alpha = 0.35f), textColor.copy(alpha = 0.1f))
                ),
                RoundedCornerShape(8.dp)
            ),
        color = bgColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status.displayName,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp
            ),
            color = textColor
        )
    }
}

@Composable
fun CategoryBadge(
    category: IncidentCategory,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(NexusGlassBorderTop, NexusGlassBorderBottom)
                ),
                RoundedCornerShape(8.dp)
            ),
        color = NexusGlassSurfaceSecondary,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = category.displayName,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = NexusTextSecondary
        )
    }
}

@Composable
fun ConfidenceBar(
    confidence: Double,
    label: String = "AI Confidence",
    modifier: Modifier = Modifier
) {
    val percent = (confidence * 100).toInt()
    val barColor = when {
        percent >= 90 -> StatusLow
        percent >= 75 -> NexusAccent
        else -> StatusHigh
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = NexusTextMuted
            )
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = barColor
            )
        }
        LinearProgressIndicator(
            progress = { confidence.toFloat().coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = barColor,
            trackColor = NexusGlassSurfaceSecondary
        )
    }
}

@Composable
fun LiveAlertBanner(
    message: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = message != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        if (message != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(NexusAccent.copy(alpha = 0.6f), NexusGlassBorderTop)),
                        RoundedCornerShape(14.dp)
                    ),
                color = NexusGlassCard,
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(NexusAccent)
                    )
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = NexusTextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = NexusTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
