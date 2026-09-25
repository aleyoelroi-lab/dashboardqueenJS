package com.example.ui.dialogs

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Dashboard
import com.example.data.model.ExportLanguage
import com.example.export.DashboardExporter
import com.example.export.FileExportHelper
import com.example.ui.theme.DarkGrayCard
import com.example.ui.theme.ElectricBabyBlue
import com.example.ui.theme.ElectricCyan

@Composable
fun ExportModal(
    dashboard: Dashboard,
    initialLanguage: ExportLanguage = ExportLanguage.HTML_STANDALONE,
    isDarkMode: Boolean = true,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedLang by remember { mutableStateOf(initialLanguage) }

    val codeContent = remember(selectedLang, dashboard, isDarkMode) {
        when (selectedLang) {
            ExportLanguage.HTML_STANDALONE -> DashboardExporter.generateStandaloneHtml(dashboard, isDarkMode)
            ExportLanguage.REACT_TAILWIND -> DashboardExporter.generateReactTailwindCode(dashboard)
            ExportLanguage.JETPACK_COMPOSE -> DashboardExporter.generateComposeKotlinCode(dashboard)
            ExportLanguage.EXCEL_CSV -> DashboardExporter.generateExcelReport(dashboard)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Export Dashboard Codebase",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Production-grade standalone artifacts by Jeddah San",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tabs for Language Selection
                ScrollableTabRow(
                    selectedTabIndex = selectedLang.ordinal,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    contentColor = ElectricBabyBlue,
                    divider = {}
                ) {
                    ExportLanguage.values().forEach { lang ->
                        val isSelected = selectedLang == lang
                        Tab(
                            selected = isSelected,
                            onClick = { selectedLang = lang },
                            text = {
                                Text(
                                    text = when (lang) {
                                        ExportLanguage.HTML_STANDALONE -> "HTML5 Webpage"
                                        ExportLanguage.REACT_TAILWIND -> "React + Tailwind"
                                        ExportLanguage.JETPACK_COMPOSE -> "Compose Kotlin"
                                        ExportLanguage.EXCEL_CSV -> "Excel / CSV Sheet"
                                    },
                                    fontSize = 11.sp,
                                    color = if (isSelected) ElectricBabyBlue else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Bar (Copy & Download/Share)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (selectedLang) {
                            ExportLanguage.HTML_STANDALONE -> "Single-file interactive HTML (Tailwind & Chart.js)"
                            ExportLanguage.REACT_TAILWIND -> "Next.js / Vite React TypeScript Component"
                            ExportLanguage.JETPACK_COMPOSE -> "Android Jetpack Compose Composable"
                            ExportLanguage.EXCEL_CSV -> "Formatted Spreadsheet Data Report"
                        },
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = {
                                FileExportHelper.copyToClipboard(
                                    context,
                                    selectedLang.name,
                                    codeContent
                                )
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = ElectricBabyBlue)
                        }

                        IconButton(
                            onClick = {
                                val (filename, mime) = when (selectedLang) {
                                    ExportLanguage.HTML_STANDALONE -> Pair("dashboard_${System.currentTimeMillis()}.html", "text/html")
                                    ExportLanguage.REACT_TAILWIND -> Pair("DashboardComponent.tsx", "text/plain")
                                    ExportLanguage.JETPACK_COMPOSE -> Pair("GeneratedDashboard.kt", "text/plain")
                                    ExportLanguage.EXCEL_CSV -> Pair("JS_Dashboard_Report_${System.currentTimeMillis()}.csv", "text/csv")
                                }
                                FileExportHelper.shareOrDownloadFile(context, filename, codeContent, mime)
                            }
                        ) {
                            Icon(Icons.Default.Download, contentDescription = "Download", tint = ElectricCyan)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Code Box Viewer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(DarkGrayCard, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    val vScroll = rememberScrollState()
                    val hScroll = rememberScrollState()

                    Text(
                        text = codeContent,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = Color(0xFFE2E8F0),
                        modifier = Modifier
                            .verticalScroll(vScroll)
                            .horizontalScroll(hScroll)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Primary Action
                Button(
                    onClick = {
                        val (filename, mime) = when (selectedLang) {
                            ExportLanguage.HTML_STANDALONE -> Pair("dashboard_${System.currentTimeMillis()}.html", "text/html")
                            ExportLanguage.REACT_TAILWIND -> Pair("DashboardComponent.tsx", "text/plain")
                            ExportLanguage.JETPACK_COMPOSE -> Pair("GeneratedDashboard.kt", "text/plain")
                            ExportLanguage.EXCEL_CSV -> Pair("JS_Dashboard_Report_${System.currentTimeMillis()}.csv", "text/csv")
                        }
                        FileExportHelper.shareOrDownloadFile(context, filename, codeContent, mime)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricBabyBlue,
                        contentColor = Color(0xFF0B0F17)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (selectedLang) {
                            ExportLanguage.HTML_STANDALONE -> "Download Interactive HTML"
                            ExportLanguage.EXCEL_CSV -> "Print / Export Excel Sheet"
                            else -> "Export & Share File"
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
