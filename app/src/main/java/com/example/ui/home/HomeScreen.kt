package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkGrayCard
import com.example.ui.theme.ElectricBabyBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MetricEmerald
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.DashboardViewModel

@Composable
fun HomeScreen(
    viewModel: DashboardViewModel,
    onNavigate: (AppScreen) -> Unit
) {
    val isGenerating by viewModel.isGenerating.collectAsState()
    var promptText by remember {
        mutableStateOf("SaaS recurring revenue & churn analytics with MRR growth, cohort retention and payment failure alerts")
    }

    val samplePrompts = listOf(
        "SaaS Recurring Revenue & Churn Center",
        "Hospital ICU Telemetry & Patient Flow",
        "E-Commerce Conversion Funnel & Velocity",
        "Smart City IoT Environmental Grid",
        "Fintech Algorithmic Trading Pulse",
        "DevOps Cloud Infrastructure SLA"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // Welcome Hero Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_welcome_banner"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(ElectricBabyBlue.copy(alpha = 0.6f), Color.Transparent))
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = ElectricBabyBlue.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBabyBlue.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "ENTERPRISE PLATFORM",
                                color = ElectricBabyBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = "● GEMINI PRO READY",
                            color = MetricEmerald,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Welcome to JS (Jeddah San) dashboard generator website",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 28.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Describe the data insights you need. Our AI automatically generates 5 functional dashboard variations with live data visualizations, real-time CSV/Excel/SQL integration, WordPress webhooks, and instant codebase exports.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Generator Input Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("prompt_input_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Describe Your Target Dashboard",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = promptText,
                        onValueChange = { promptText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 90.dp)
                            .testTag("dashboard_prompt_input"),
                        placeholder = {
                            Text(
                                "Describe metrics, chart types, operational goals...",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBabyBlue,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Quick Pre-Engineered Prompts:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(samplePrompts) { sample ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier.clickable { promptText = sample }
                            ) {
                                Text(
                                    text = sample,
                                    fontSize = 11.sp,
                                    color = if (promptText == sample) ElectricBabyBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (promptText == sample) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.generateDashboard(promptText) },
                        enabled = !isGenerating && promptText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricBabyBlue,
                            contentColor = Color(0xFF0B0F17)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("generate_variations_button")
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFF0B0F17),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Synthesizing 5 Architecture Variants...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate 5 Dashboard Variations with Gemini", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Feature Navigation Hub
        item {
            Text(
                text = "Autonomous System Capabilities",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HubActionCard(
                        title = "Active Workspace",
                        tagline = "Interactive Canvas & Widgets",
                        icon = Icons.Default.Dashboard,
                        accentColor = ElectricBabyBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AppScreen.WORKSPACE) }
                    )
                    HubActionCard(
                        title = "5 Variations Matrix",
                        tagline = "Compare & Select Designs",
                        icon = Icons.Default.ViewCarousel,
                        accentColor = ElectricCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AppScreen.VARIATIONS) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HubActionCard(
                        title = "CSV / Excel / SQL",
                        tagline = "Real-Time Data Ingestion",
                        icon = Icons.Default.CloudUpload,
                        accentColor = MetricEmerald,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AppScreen.DATA_INTEGRATION) }
                    )
                    HubActionCard(
                        title = "WordPress Webhooks",
                        tagline = "External Automated Triggers",
                        icon = Icons.Default.Webhook,
                        accentColor = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AppScreen.WORDPRESS_WEBHOOKS) }
                    )
                }

                HubActionCard(
                    title = "Multi-Device Live Web Sandbox",
                    tagline = "Android Phone, iPhone, iPod & Desktop Browser Responsive Preview",
                    icon = Icons.Default.Devices,
                    accentColor = ElectricCyan,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onNavigate(AppScreen.WEB_PREVIEW) }
                )

                HubActionCard(
                    title = "Enterprise Admin & Security Console",
                    tagline = "Role-Based Access Control, HMAC Secrets & Audit Logging",
                    icon = Icons.Default.Security,
                    accentColor = Color(0xFFA855F7),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onNavigate(AppScreen.ADMIN_SECURITY) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HubActionCard(
    title: String,
    tagline: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = accentColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = tagline,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
