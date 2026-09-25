package com.example.ui.dashboard

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExportLanguage
import com.example.data.model.Widget
import com.example.data.model.WidgetType
import com.example.data.model.WidgetWidth
import com.example.export.DashboardExporter
import com.example.export.FileExportHelper
import com.example.ui.components.*
import com.example.ui.dialogs.ExportModal
import com.example.ui.dialogs.WidgetEditDialog
import com.example.ui.theme.DarkGrayCard
import com.example.ui.theme.ElectricBabyBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MetricEmerald
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigate: (AppScreen) -> Unit
) {
    val context = LocalContext.current
    val dashboard by viewModel.dashboard.collectAsState()
    val isLiveStreaming by viewModel.isLiveStreaming.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val editingWidget by viewModel.editingWidget.collectAsState()
    val exportModalOpen by viewModel.exportModalOpen.collectAsState()
    val activeExportLanguage by viewModel.activeExportLanguage.collectAsState()

    var showNewWidgetDialog by remember { mutableStateOf(false) }

    // Dialogs
    if (editingWidget != null) {
        WidgetEditDialog(
            widget = editingWidget,
            onDismiss = { viewModel.setEditingWidget(null) },
            onSave = { updated -> viewModel.updateWidget(updated) },
            onDelete = { id -> viewModel.deleteWidget(id) }
        )
    }

    if (showNewWidgetDialog) {
        WidgetEditDialog(
            widget = null,
            onDismiss = { showNewWidgetDialog = false },
            onSave = { newWidget ->
                viewModel.addWidget(newWidget)
                showNewWidgetDialog = false
            }
        )
    }

    if (exportModalOpen) {
        ExportModal(
            dashboard = dashboard,
            initialLanguage = activeExportLanguage,
            isDarkMode = isDarkMode,
            onDismiss = { viewModel.setExportModal(false) }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // Dashboard Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = ElectricBabyBlue.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "ACTIVE PROTOTYPE",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = ElectricBabyBlue,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        // Live stream status pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isLiveStreaming) MetricEmerald.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isLiveStreaming) MetricEmerald else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.clickable { viewModel.toggleRealTimeStreaming() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(
                                            if (isLiveStreaming) MetricEmerald else Color.Gray,
                                            RoundedCornerShape(3.dp)
                                        )
                                )
                                Text(
                                    text = if (isLiveStreaming) "LIVE PULSE (3s)" else "STREAM PAUSED",
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLiveStreaming) MetricEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = dashboard.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = dashboard.description,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Action Toolbar (Print / Excel, Download HTML, Customize Widgets, Export Codebase)
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Print / Download to Excel Button
                item {
                    Button(
                        onClick = {
                            val excelReport = DashboardExporter.generateExcelReport(dashboard)
                            FileExportHelper.shareOrDownloadFile(
                                context,
                                "JS_Dashboard_Report_${System.currentTimeMillis()}.csv",
                                excelReport,
                                "text/csv"
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricBabyBlue,
                            contentColor = Color(0xFF0B0F17)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("download_excel_button")
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Print / Excel Sheet", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Download HTML Button
                item {
                    OutlinedButton(
                        onClick = {
                            val html = DashboardExporter.generateStandaloneHtml(dashboard, isDarkMode)
                            FileExportHelper.shareOrDownloadFile(
                                context,
                                "JS_Dashboard_Layout_${System.currentTimeMillis()}.html",
                                html,
                                "text/html"
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan),
                        modifier = Modifier.testTag("download_html_button")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Download HTML", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Multi-Device Web Simulator (Android, iPhone, iPod, Browser)
                item {
                    Button(
                        onClick = { onNavigate(AppScreen.WEB_PREVIEW) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricCyan,
                            contentColor = Color(0xFF0B0F17)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("launch_web_sandbox_button")
                    ) {
                        Icon(Icons.Default.Devices, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Live Web Simulator", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Customize / Add Widget Button
                item {
                    OutlinedButton(
                        onClick = { showNewWidgetDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("add_widget_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Widget", fontSize = 11.sp)
                    }
                }

                // Export Codebase (HTML, React, Compose)
                item {
                    OutlinedButton(
                        onClick = { viewModel.setExportModal(true, ExportLanguage.REACT_TAILWIND) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("export_codebase_button")
                    ) {
                        Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export Codebase", fontSize = 11.sp)
                    }
                }
            }
        }

        // Active Widgets Render List
        items(dashboard.activeWidgets) { widget ->
            WidgetCardContainer(
                widget = widget,
                onEditClick = { viewModel.setEditingWidget(widget) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun WidgetCardContainer(
    widget: Widget,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEditClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Widget Title Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = widget.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (widget.subtitle.isNotBlank()) {
                        Text(
                            text = widget.subtitle,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(0.6.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text(
                            text = widget.type.name.replace("_", " "),
                            fontSize = 8.5.sp,
                            fontFamily = FontFamily.Monospace,
                            color = ElectricBabyBlue,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Widget",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body Visualization
            when (widget.type) {
                WidgetType.METRIC_CARD -> {
                    MetricKpiCard(
                        title = widget.title,
                        value = widget.metricValue,
                        unit = widget.metricUnit,
                        trendPercent = widget.trendPercent,
                        isPositive = widget.trendIsPositive,
                        subtitle = widget.subtitle
                    )
                }

                WidgetType.AREA_CHART -> {
                    InteractiveAreaChart(
                        points = widget.points,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                }

                WidgetType.BAR_CHART -> {
                    InteractiveBarChart(
                        points = widget.points,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                }

                WidgetType.DONUT_CHART -> {
                    InteractiveDonutChart(
                        slices = widget.slices,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                WidgetType.GAUGE_METER -> {
                    InteractiveGaugeMeter(
                        currentValue = widget.currentValue,
                        targetThreshold = widget.targetThreshold,
                        metricValue = widget.metricValue,
                        metricUnit = widget.metricUnit,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )
                }

                WidgetType.DATA_TABLE -> {
                    DataTablePreview(
                        headers = widget.tableHeaders,
                        rows = widget.tableRows
                    )
                }
            }
        }
    }
}

@Composable
private fun DataTablePreview(headers: List<String>, rows: List<List<String>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        // Headers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                .padding(vertical = 6.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            headers.forEach { h ->
                Text(
                    text = h,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Rows
        rows.take(6).forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                row.forEach { cell ->
                    val isAccent = cell.contains("$") || cell.contains("%")
                    Text(
                        text = cell,
                        fontSize = 10.sp,
                        fontFamily = if (isAccent) FontFamily.Monospace else FontFamily.Default,
                        color = if (isAccent) ElectricBabyBlue else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 0.5.dp)
        }
    }
}
