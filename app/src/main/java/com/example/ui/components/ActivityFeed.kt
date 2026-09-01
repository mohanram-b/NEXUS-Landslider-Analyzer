package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ActivityEntity
import com.example.data.model.ActivityType
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ActivityFeedItem(
    activity: ActivityEntity,
    modifier: Modifier = Modifier
) {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val timeStr = timeFormat.format(Date(activity.createdAt))

    val (icon, iconColor) = when (activity.type) {
        ActivityType.INCIDENT_CREATED -> Icons.Default.AddCircleOutline to NexusAccent
        ActivityType.AI_ANALYZED, ActivityType.SEVERITY_SCORED, ActivityType.IMPACT_PREDICTED -> Icons.Default.AutoAwesome to StatusAnalyzing
        ActivityType.DEPARTMENT_ASSIGNED, ActivityType.DEPARTMENT_ROUTED -> Icons.Default.AssignmentInd to Color(0xFF818CF8)
        ActivityType.SENSOR_TRIGGERED -> Icons.Default.Sensors to StatusCritical
        ActivityType.RESOLVED -> Icons.Default.CheckCircle to StatusResolved
        ActivityType.STATUS_CHANGED -> Icons.Default.SwapHoriz to StatusHigh
        ActivityType.OPERATOR_NOTE -> Icons.Default.Notes to NexusTextSecondary
        ActivityType.DUPLICATE_FLAGGED -> Icons.Default.Layers to StatusHigh
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(14.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = activity.userName,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = NexusTextPrimary
                )
                Text(
                    text = timeStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = NexusTextMuted
                )
            }

            Text(
                text = activity.message,
                style = MaterialTheme.typography.bodySmall,
                color = NexusTextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}
