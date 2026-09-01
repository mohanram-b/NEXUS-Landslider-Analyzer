package com.example.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.components.NexusSideDrawer
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.NexusViewModel
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Dashboard : Screen("dashboard", "Command", Icons.Outlined.Dashboard)
    data object Incidents : Screen("incidents", "Incidents", Icons.Outlined.Emergency)
    data object Sensors : Screen("sensors", "Sensors", Icons.Outlined.Sensors)
    data object Analytics : Screen("analytics", "Analytics", Icons.Outlined.Insights)
    data object Settings : Screen("settings", "System", Icons.Outlined.Tune)
    data object Report : Screen("report", "Report", Icons.Default.Add)
    data object IncidentDetail : Screen("incident/{incidentId}", "Incident Detail", Icons.Default.Info) {
        fun createRoute(incidentId: String) = "incident/$incidentId"
    }
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Incidents,
    Screen.Sensors,
    Screen.Analytics,
    Screen.Settings
)

@Composable
fun NexusApp(
    navController: NavHostController = rememberNavController(),
    viewModel: NexusViewModel = viewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentUser by viewModel.currentUser.collectAsState()
    val activeIncidents by viewModel.activeIncidents.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val showBottomBar = bottomNavItems.any { it.route == currentRoute }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        scrimColor = Color.Black.copy(alpha = 0.65f),
        drawerContent = {
            NexusSideDrawer(
                currentRoute = currentRoute,
                currentUser = currentUser,
                activeIncidentCount = activeIncidents.size,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onCloseDrawer = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    Surface(
                        modifier = Modifier.border(
                            1.dp,
                            Brush.verticalGradient(listOf(NexusGlassBorderTop, Color.Transparent)),
                            androidx.compose.ui.graphics.RectangleShape
                        ),
                        color = NexusGlassCard
                    ) {
                        NavigationBar(
                            containerColor = Color.Transparent,
                            contentColor = NexusTextPrimary,
                            tonalElevation = 0.dp
                        ) {
                            bottomNavItems.forEach { screen ->
                                val selected = currentRoute == screen.route
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = screen.title,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = screen.title,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                                        )
                                    },
                                    selected = selected,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NexusAccent,
                                        selectedTextColor = NexusAccent,
                                        unselectedIconColor = NexusTextMuted,
                                        unselectedTextColor = NexusTextMuted,
                                        indicatorColor = NexusAccent.copy(alpha = 0.15f)
                                    ),
                                    modifier = Modifier.testTag("nav_tab_${screen.route}")
                                )
                            }
                        }
                    }
                }
            },
            containerColor = NexusBackground
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route,
                enterTransition = {
                    fadeIn(animationSpec = tween(220)) + slideInHorizontally(
                        initialOffsetX = { 30 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
                    )
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(180)) + slideOutHorizontally(
                        targetOffsetX = { -30 },
                        animationSpec = tween(180)
                    )
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(220)) + slideInHorizontally(
                        initialOffsetX = { -30 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
                    )
                },
                popExitTransition = {
                    fadeOut(animationSpec = tween(180)) + slideOutHorizontally(
                        targetOffsetX = { 30 },
                        animationSpec = tween(180)
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable(Screen.Dashboard.route) {
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToIncidents = { navController.navigate(Screen.Incidents.route) },
                        onNavigateToDetail = { incidentId ->
                            navController.navigate(Screen.IncidentDetail.createRoute(incidentId))
                        },
                        onNavigateToReport = { navController.navigate(Screen.Report.route) },
                        onNavigateToSensors = { navController.navigate(Screen.Sensors.route) },
                        onOpenDrawer = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }
                    )
                }

                composable(Screen.Incidents.route) {
                    IncidentsScreen(
                        viewModel = viewModel,
                        onNavigateToDetail = { incidentId ->
                            navController.navigate(Screen.IncidentDetail.createRoute(incidentId))
                        },
                        onNavigateToReport = { navController.navigate(Screen.Report.route) },
                        onOpenDrawer = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }
                    )
                }

                composable(Screen.Sensors.route) {
                    SensorsScreen(
                        viewModel = viewModel,
                        onNavigateToDetail = { incidentId ->
                            navController.navigate(Screen.IncidentDetail.createRoute(incidentId))
                        },
                        onOpenDrawer = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }
                    )
                }

                composable(Screen.Analytics.route) {
                    AnalyticsScreen(
                        viewModel = viewModel,
                        onOpenDrawer = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }
                    )
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(
                        viewModel = viewModel,
                        onOpenDrawer = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }
                    )
                }

                composable(Screen.Report.route) {
                    ReportIncidentScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onIncidentCreated = { newIncidentId ->
                            navController.navigate(Screen.IncidentDetail.createRoute(newIncidentId)) {
                                popUpTo(Screen.Dashboard.route)
                            }
                        }
                    )
                }

                composable(
                    route = Screen.IncidentDetail.route,
                    arguments = listOf(navArgument("incidentId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val incidentId = backStackEntry.arguments?.getString("incidentId") ?: ""
                    IncidentDetailScreen(
                        incidentId = incidentId,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

