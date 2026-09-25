package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.admin.AdminSecurityScreen
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.data.DataIntegrationScreen
import com.example.ui.home.HomeScreen
import com.example.ui.theme.ElectricBabyBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.variations.VariationsScreen
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.DashboardViewModel
import com.example.ui.web.WebPreviewScreen
import com.example.ui.webhooks.WebhooksScreen

class MainActivity : ComponentActivity() {

    private val viewModel: DashboardViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val feedbackMessage by viewModel.userFeedbackMessage.collectAsState()
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(feedbackMessage) {
                feedbackMessage?.let { msg ->
                    snackbarHostState.showSnackbar(msg)
                    viewModel.clearFeedback()
                }
            }

            // Handle system back navigation
            BackHandler(enabled = currentScreen != AppScreen.HOME) {
                when (currentScreen) {
                    AppScreen.WEB_PREVIEW -> viewModel.navigateTo(AppScreen.WORKSPACE)
                    AppScreen.WORKSPACE -> viewModel.navigateTo(AppScreen.VARIATIONS)
                    AppScreen.VARIATIONS -> viewModel.navigateTo(AppScreen.HOME)
                    else -> viewModel.navigateTo(AppScreen.HOME)
                }
            }

            MyApplicationTheme(darkTheme = isDarkMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        color = ElectricBabyBlue.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBabyBlue),
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "JS",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 14.sp,
                                                color = ElectricBabyBlue,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = "Jeddah San Dash",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = when (currentScreen) {
                                                AppScreen.HOME -> "AI Dashboard Generator"
                                                AppScreen.VARIATIONS -> "5 Architecture Variations"
                                                AppScreen.WORKSPACE -> "Active Prototype Workspace"
                                                AppScreen.WEB_PREVIEW -> "Cross-Device Web Sandbox"
                                                AppScreen.DATA_INTEGRATION -> "CSV / Excel / SQL Ingestion"
                                                AppScreen.WORDPRESS_WEBHOOKS -> "WordPress Webhook Gateway"
                                                AppScreen.ADMIN_SECURITY -> "Enterprise RBAC & Security"
                                            },
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            },
                            actions = {
                                // Live Web Browser Sandbox Button (Android, iPhone, iPod, Web)
                                IconButton(
                                    onClick = {
                                        if (currentScreen == AppScreen.WEB_PREVIEW) {
                                            viewModel.navigateTo(AppScreen.WORKSPACE)
                                        } else {
                                            viewModel.navigateTo(AppScreen.WEB_PREVIEW)
                                        }
                                    },
                                    modifier = Modifier.testTag("web_preview_toggle")
                                ) {
                                    Icon(
                                        imageVector = if (currentScreen == AppScreen.WEB_PREVIEW) Icons.Default.Dashboard else Icons.Default.Language,
                                        contentDescription = "Toggle Multi-Device Web Sandbox",
                                        tint = if (currentScreen == AppScreen.WEB_PREVIEW) ElectricCyan else ElectricBabyBlue
                                    )
                                }

                                // Dark Mode Toggle Button (Required for accessibility)
                                IconButton(
                                    onClick = { viewModel.toggleDarkMode() },
                                    modifier = Modifier.testTag("dark_mode_toggle")
                                ) {
                                    Icon(
                                        imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                        contentDescription = "Toggle Dark Mode",
                                        tint = if (isDarkMode) ElectricBabyBlue else Color(0xFF0F172A)
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ) {
                            NavigationBarItem(
                                selected = currentScreen == AppScreen.HOME,
                                onClick = { viewModel.navigateTo(AppScreen.HOME) },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = ElectricBabyBlue.copy(alpha = 0.25f),
                                    selectedIconColor = ElectricBabyBlue,
                                    selectedTextColor = ElectricBabyBlue
                                )
                            )

                            NavigationBarItem(
                                selected = currentScreen == AppScreen.VARIATIONS,
                                onClick = { viewModel.navigateTo(AppScreen.VARIATIONS) },
                                icon = { Icon(Icons.Default.ViewCarousel, contentDescription = "5 Variations") },
                                label = { Text("5 Variants", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = ElectricBabyBlue.copy(alpha = 0.25f),
                                    selectedIconColor = ElectricBabyBlue,
                                    selectedTextColor = ElectricBabyBlue
                                )
                            )

                            NavigationBarItem(
                                selected = currentScreen == AppScreen.WORKSPACE,
                                onClick = { viewModel.navigateTo(AppScreen.WORKSPACE) },
                                icon = { Icon(Icons.Default.Dashboard, contentDescription = "Workspace") },
                                label = { Text("Workspace", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = ElectricBabyBlue.copy(alpha = 0.25f),
                                    selectedIconColor = ElectricBabyBlue,
                                    selectedTextColor = ElectricBabyBlue
                                )
                            )

                            NavigationBarItem(
                                selected = currentScreen == AppScreen.DATA_INTEGRATION,
                                onClick = { viewModel.navigateTo(AppScreen.DATA_INTEGRATION) },
                                icon = { Icon(Icons.Default.CloudUpload, contentDescription = "Data") },
                                label = { Text("Data", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = ElectricBabyBlue.copy(alpha = 0.25f),
                                    selectedIconColor = ElectricBabyBlue,
                                    selectedTextColor = ElectricBabyBlue
                                )
                            )

                            NavigationBarItem(
                                selected = currentScreen == AppScreen.WORDPRESS_WEBHOOKS,
                                onClick = { viewModel.navigateTo(AppScreen.WORDPRESS_WEBHOOKS) },
                                icon = { Icon(Icons.Default.Webhook, contentDescription = "Webhooks") },
                                label = { Text("Webhooks", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = ElectricBabyBlue.copy(alpha = 0.25f),
                                    selectedIconColor = ElectricBabyBlue,
                                    selectedTextColor = ElectricBabyBlue
                                )
                            )

                            NavigationBarItem(
                                selected = currentScreen == AppScreen.ADMIN_SECURITY,
                                onClick = { viewModel.navigateTo(AppScreen.ADMIN_SECURITY) },
                                icon = { Icon(Icons.Default.Security, contentDescription = "Admin") },
                                label = { Text("Security", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = ElectricBabyBlue.copy(alpha = 0.25f),
                                    selectedIconColor = ElectricBabyBlue,
                                    selectedTextColor = ElectricBabyBlue
                                )
                            )
                        }
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "screen_transition"
                        ) { screen ->
                            when (screen) {
                                AppScreen.HOME -> HomeScreen(
                                    viewModel = viewModel,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                                AppScreen.VARIATIONS -> VariationsScreen(
                                    viewModel = viewModel,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                                AppScreen.WORKSPACE -> DashboardScreen(
                                    viewModel = viewModel,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                                AppScreen.WEB_PREVIEW -> WebPreviewScreen(
                                    viewModel = viewModel,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                                AppScreen.DATA_INTEGRATION -> DataIntegrationScreen(
                                    viewModel = viewModel,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                                AppScreen.WORDPRESS_WEBHOOKS -> WebhooksScreen(
                                    viewModel = viewModel,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                                AppScreen.ADMIN_SECURITY -> AdminSecurityScreen(
                                    viewModel = viewModel,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
