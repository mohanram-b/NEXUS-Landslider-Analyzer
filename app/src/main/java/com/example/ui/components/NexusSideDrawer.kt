package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.UserEntity
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@Composable
fun NexusSideDrawer(
    currentRoute: String?,
    currentUser: UserEntity?,
    activeIncidentCount: Int,
    onNavigate: (String) -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    ModalDrawerSheet(
        drawerContainerColor = NexusBackground,
        drawerContentColor = NexusTextPrimary,
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
        modifier = modifier
            .width(320.dp)
            .fillMaxHeight()
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(
                        NexusGlassBorderTop,
                        NexusGlassBorderBottom,
                        Color.Transparent
                    )
                ),
                RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
            )
            .testTag("nexus_side_drawer")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            NexusGlassCard,
                            NexusBackground,
                            NexusSurfaceSecondary.copy(alpha = 0.5f)
                        )
                    )
                )
                .padding(vertical = 20.dp, horizontal = 16.dp)
        ) {
            // Header Section: Brand & Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        NexusAccent,
                                        Color(0xFF2563EB)
                                    )
                                )
                            )
                            .border(
                                1.dp,
                                Brush.verticalGradient(
                                    listOf(Color.White.copy(alpha = 0.4f), Color.Transparent)
                                ),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Nexus Shield",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "NEXUS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            ),
                            color = NexusTextPrimary
                        )
                        Text(
                            text = "COMMAND CENTER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                letterSpacing = 1.sp
                            ),
                            color = NexusAccent
                        )
                    }
                }

                IconButton(
                    onClick = onCloseDrawer,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(NexusGlassSurface)
                        .testTag("btn_close_drawer")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Drawer",
                        tint = NexusTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // System Status Pill
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                shape = RoundedCornerShape(10.dp),
                color = NexusGlassSurfaceSecondary,
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(
                        listOf(NexusGlassBorderTop, NexusGlassBorderBottom)
                    )
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
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
                        text = "AI Multi-Agent Pipeline Active",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        ),
                        color = StatusLow
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "99.8%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = NexusTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = NexusGlassBorderBottom,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Links with Smooth Spring Physics
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "PRIMARY MODULES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    ),
                    color = NexusTextMuted,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                // 1. Dashboard
                DrawerNavItem(
                    title = "Dashboard",
                    subtitle = "Command & Live Metrics",
                    icon = Icons.Outlined.Dashboard,
                    selectedIcon = Icons.Filled.Dashboard,
                    isSelected = currentRoute == Screen.Dashboard.route,
                    onClick = {
                        onNavigate(Screen.Dashboard.route)
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_dashboard"
                )

                // 2. Incidents
                DrawerNavItem(
                    title = "Incidents",
                    subtitle = "Incident Queue & Triage",
                    icon = Icons.Outlined.Emergency,
                    selectedIcon = Icons.Filled.Emergency,
                    isSelected = currentRoute == Screen.Incidents.route,
                    badgeText = if (activeIncidentCount > 0) "$activeIncidentCount" else null,
                    badgeColor = StatusHigh,
                    onClick = {
                        onNavigate(Screen.Incidents.route)
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_incidents"
                )

                // 3. Settings
                DrawerNavItem(
                    title = "Settings",
                    subtitle = "System & Operator Roles",
                    icon = Icons.Outlined.Tune,
                    selectedIcon = Icons.Filled.Tune,
                    isSelected = currentRoute == Screen.Settings.route,
                    onClick = {
                        onNavigate(Screen.Settings.route)
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_settings"
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "TELEMETRY & INTELLIGENCE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    ),
                    color = NexusTextMuted,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                // 4. Sensors
                DrawerNavItem(
                    title = "IoT Sensors",
                    subtitle = "Environmental Grid",
                    icon = Icons.Outlined.Sensors,
                    selectedIcon = Icons.Filled.Sensors,
                    isSelected = currentRoute == Screen.Sensors.route,
                    onClick = {
                        onNavigate(Screen.Sensors.route)
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_sensors"
                )

                // 5. Analytics
                DrawerNavItem(
                    title = "Analytics",
                    subtitle = "Predictive Insights",
                    icon = Icons.Outlined.Insights,
                    selectedIcon = Icons.Filled.Insights,
                    isSelected = currentRoute == Screen.Analytics.route,
                    onClick = {
                        onNavigate(Screen.Analytics.route)
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_analytics"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Action Item: Quick Report Incident
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                        .clickable {
                            onNavigate(Screen.Report.route)
                            onCloseDrawer()
                        }
                        .testTag("drawer_btn_new_report"),
                    shape = RoundedCornerShape(12.dp),
                    color = NexusAccent.copy(alpha = 0.12f),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.verticalGradient(
                            listOf(
                                NexusAccent.copy(alpha = 0.5f),
                                NexusAccent.copy(alpha = 0.2f)
                            )
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NexusAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Report Incident",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = NexusAccent
                            )
                            Text(
                                text = "Submit new emergency event",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = NexusTextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(
                color = NexusGlassBorderBottom,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Footer Section: Active Operator Profile
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .clickable {
                        onNavigate(Screen.Settings.route)
                        onCloseDrawer()
                    }
                    .testTag("drawer_user_profile_card"),
                shape = RoundedCornerShape(14.dp),
                color = NexusGlassSurfaceSecondary,
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(
                        listOf(NexusGlassBorderTop, NexusGlassBorderBottom)
                    )
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NexusAccent.copy(alpha = 0.2f))
                            .border(
                                1.dp,
                                Brush.verticalGradient(
                                    listOf(NexusAccent.copy(alpha = 0.6f), NexusAccent.copy(alpha = 0.2f))
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (currentUser?.name?.firstOrNull() ?: 'U').toString(),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = NexusAccent
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentUser?.name ?: "Operator",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            ),
                            color = NexusTextPrimary
                        )
                        Text(
                            text = currentUser?.role?.displayName ?: "Dispatcher",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = NexusTextSecondary
                            )
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Switch Profile",
                        tint = NexusTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerNavItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selectedIcon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeText: String? = null,
    badgeColor: Color = NexusAccent,
    testTag: String = ""
) {
    // Smooth Spring physics transitions (Framer Motion equivalent in Compose)
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.18f else 0.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bg_alpha"
    )

    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "icon_scale"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) NexusTextPrimary else NexusTextSecondary,
        animationSpec = tween(durationMillis = 200),
        label = "content_color"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) NexusAccent else NexusTextMuted,
        animationSpec = tween(durationMillis = 200),
        label = "icon_color"
    )

    val borderAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.45f else 0.0f,
        animationSpec = tween(durationMillis = 220),
        label = "border_alpha"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            )
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) NexusAccent.copy(alpha = backgroundAlpha) else Color.Transparent,
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(
                    listOf(
                        NexusAccent.copy(alpha = borderAlpha),
                        Color.Transparent
                    )
                )
            )
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon with spring scale
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .scale(iconScale)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) NexusAccent.copy(alpha = 0.2f) else NexusGlassSurface)
                    .border(
                        1.dp,
                        if (isSelected) NexusAccent.copy(alpha = 0.4f) else NexusGlassBorderBottom,
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSelected) selectedIcon else icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Labels
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.5.sp
                    ),
                    color = contentColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = if (isSelected) NexusTextSecondary else NexusTextMuted
                )
            }

            // Optional Badge
            if (badgeText != null) {
                Surface(
                    color = badgeColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.verticalGradient(
                            listOf(badgeColor.copy(alpha = 0.5f), badgeColor.copy(alpha = 0.2f))
                        )
                    )
                ) {
                    Text(
                        text = badgeText,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = badgeColor
                    )
                }
            }
        }
    }
}
