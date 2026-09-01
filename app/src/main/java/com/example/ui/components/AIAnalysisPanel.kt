package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.AIAnalysisEntity
import com.example.data.model.SeverityLevel
import com.example.ui.theme.*

@Composable
fun AIAnalysisPanel(
    analysis: AIAnalysisEntity?,
    isAnalyzing: Boolean = false,
    analysisStage: String = "",
    analysisProgress: Float = 0f,
    onRerunAnalysis: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
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
                        .background(if (isAnalyzing) StatusAnalyzing else NexusAccent)
                )
                Text(
                    text = "AI Operational Intelligence",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.2).sp
                    ),
                    color = NexusTextPrimary
                )
            }

            if (onRerunAnalysis != null && !isAnalyzing) {
                TextButton(
                    onClick = onRerunAnalysis,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Re-analyze",
                        tint = NexusAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Re-run AI",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexusAccent
                    )
                }
            }
        }

        if (isAnalyzing) {
            NexusCard(
                borderColor = StatusAnalyzing.copy(alpha = 0.4f),
                backgroundColor = StatusAnalyzingBg.copy(alpha = 0.05f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ORCHESTRATION IN PROGRESS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = StatusAnalyzing
                        )
                        Text(
                            text = "${(analysisProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = StatusAnalyzing
                        )
                    }
                    LinearProgressIndicator(
                        progress = { analysisProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = StatusAnalyzing,
                        trackColor = NexusSurfaceSecondary
                    )
                    Text(
                        text = analysisStage,
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTextSecondary
                    )
                }
            }
        }

        if (analysis != null) {
            // 1. Severity Assessment Card
            AIInsightCard(
                title = "Severity Assessment",
                icon = Icons.Outlined.Warning,
                badge = {
                    val sevLevel = SeverityLevel.fromScore(analysis.severityScore)
                    SeverityBadge(level = sevLevel)
                }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SEVERITY SCORE",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexusTextMuted
                        )
                        Text(
                            text = "${analysis.severityScore}/100",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (analysis.severityScore >= 80) StatusCritical else if (analysis.severityScore >= 60) StatusHigh else NexusAccent
                        )
                    }
                    LinearProgressIndicator(
                        progress = { analysis.severityScore / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (analysis.severityScore >= 80) StatusCritical else if (analysis.severityScore >= 60) StatusHigh else NexusAccent,
                        trackColor = NexusSurfaceSecondary
                    )
                    Text(
                        text = analysis.severityExplanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            // 2. Department Routing Card
            AIInsightCard(
                title = "Department Routing",
                icon = Icons.Outlined.Business,
                badge = {
                    Surface(
                        color = NexusAccent.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.verticalGradient(
                                listOf(NexusAccent.copy(alpha = 0.4f), NexusAccent.copy(alpha = 0.1f))
                            )
                        )
                    ) {
                        Text(
                            text = "${(analysis.routingConfidence * 100).toInt()}% CONFIDENCE",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 10.sp),
                            color = NexusAccent
                        )
                    }
                }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "RECOMMENDED ASSIGNMENT",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexusTextMuted
                    )
                    Text(
                        text = analysis.routingDecision,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = NexusTextPrimary
                    )
                    Text(
                        text = analysis.routingReason,
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            // 3. Duplicate & Cluster Detection Card
            AIInsightCard(
                title = "Duplicate & Correlation Check",
                icon = Icons.Outlined.Layers,
                badge = {
                    val isDupe = analysis.duplicateSimilarityScore >= 0.70
                    Surface(
                        color = if (isDupe) StatusHighBg else StatusLowBg,
                        shape = RoundedCornerShape(8.dp),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.verticalGradient(
                                listOf(
                                    (if (isDupe) StatusHigh else StatusLow).copy(alpha = 0.4f),
                                    (if (isDupe) StatusHigh else StatusLow).copy(alpha = 0.1f)
                                )
                            )
                        )
                    ) {
                        Text(
                            text = if (isDupe) "CORRELATED" else "UNIQUE",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 10.sp),
                            color = if (isDupe) StatusHigh else StatusLow
                        )
                    }
                }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ConfidenceBar(
                        confidence = analysis.duplicateSimilarityScore,
                        label = "Spatio-Temporal Similarity"
                    )
                    Text(
                        text = analysis.duplicateRecommendation,
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTextSecondary
                    )
                }
            }

            // 4. Impact Prediction Card
            AIInsightCard(
                title = "Impact Prediction",
                icon = Icons.Outlined.TrendingUp,
                badge = {
                    Surface(
                        color = NexusGlassSurfaceSecondary,
                        shape = RoundedCornerShape(8.dp),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.verticalGradient(listOf(NexusGlassBorderTop, NexusGlassBorderBottom))
                        )
                    ) {
                        Text(
                            text = "Score ${analysis.impactScore}/100",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = NexusTextPrimary
                        )
                    }
                }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ImpactPill(label = "Traffic", value = analysis.trafficDisruptionLevel)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ImpactPill(label = "Population", value = analysis.populationAtRisk)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ImpactPill(label = "Infrastructure", value = analysis.infrastructureRisk)
                    }
                }
            }

            // 5. Response Strategy SOP Card
            AIInsightCard(
                title = "Recommended Response Strategy",
                icon = Icons.Outlined.Checklist,
                badge = {
                    Surface(
                        color = NexusGlassSurfaceSecondary,
                        shape = RoundedCornerShape(8.dp),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.verticalGradient(listOf(NexusGlassBorderTop, NexusGlassBorderBottom))
                        )
                    ) {
                        Text(
                            text = "SOP PROTOCOL",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = NexusTextSecondary
                        )
                    }
                }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = analysis.responseStrategy,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = NexusTextPrimary,
                        lineHeight = 18.sp
                    )

                    // Parse JSON priority checklist if available
                    val cleanChecklist = analysis.priorityChecklistJson
                        .removePrefix("[").removeSuffix("]")
                        .split(",")
                        .map { it.trim().removeSurrounding("\"") }
                        .filter { it.isNotEmpty() }

                    if (cleanChecklist.isNotEmpty()) {
                        Column(
                            modifier = Modifier.padding(top = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            cleanChecklist.forEach { step ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircleOutline,
                                        contentDescription = null,
                                        tint = NexusAccent,
                                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                    )
                                    Text(
                                        text = step,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NexusTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AIInsightCard(
    title: String,
    icon: ImageVector,
    badge: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
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
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = NexusTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = NexusTextPrimary
                    )
                }
                badge()
            }
            content()
        }
    }
}

@Composable
private fun ImpactPill(
    label: String,
    value: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = NexusTextMuted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = NexusTextSecondary
        )
    }
}
