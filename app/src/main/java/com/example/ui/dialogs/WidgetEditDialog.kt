package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ChartPoint
import com.example.data.model.SliceItem
import com.example.data.model.Widget
import com.example.data.model.WidgetType
import com.example.data.model.WidgetWidth
import com.example.ui.theme.ElectricBabyBlue
import com.example.ui.theme.ElectricCyan

@Composable
fun WidgetEditDialog(
    widget: Widget?,
    onDismiss: () -> Unit,
    onSave: (Widget) -> Unit,
    onDelete: ((String) -> Unit)? = null
) {
    var title by remember { mutableStateOf(widget?.title ?: "New Custom Metric") }
    var subtitle by remember { mutableStateOf(widget?.subtitle ?: "Live stream analytics") }
    var selectedType by remember { mutableStateOf(widget?.type ?: WidgetType.METRIC_CARD) }
    var widthSpan by remember { mutableStateOf(widget?.width ?: WidgetWidth.FULL) }
    var metricValue by remember { mutableStateOf(widget?.metricValue ?: "1,420") }
    var metricUnit by remember { mutableStateOf(widget?.metricUnit ?: "Units") }
    var trendPercent by remember { mutableStateOf(widget?.trendPercent?.toString() ?: "12.5") }
    var isPositive by remember { mutableStateOf(widget?.trendIsPositive ?: true) }
    var threshold by remember { mutableStateOf(widget?.targetThreshold?.toString() ?: "85") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (widget != null) "Customize Widget" else "Add New Widget",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Widget Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Subtitle / Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text("Visualization Type", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(WidgetType.METRIC_CARD, WidgetType.AREA_CHART, WidgetType.BAR_CHART).forEach { type ->
                        val isSel = selectedType == type
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) ElectricBabyBlue else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedType = type }
                        ) {
                            Text(
                                text = type.name.replace("_", " "),
                                fontSize = 10.sp,
                                color = if (isSel) Color(0xFF0B0F17) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(WidgetType.DONUT_CHART, WidgetType.GAUGE_METER, WidgetType.DATA_TABLE).forEach { type ->
                        val isSel = selectedType == type
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) ElectricBabyBlue else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedType = type }
                        ) {
                            Text(
                                text = type.name.replace("_", " "),
                                fontSize = 10.sp,
                                color = if (isSel) Color(0xFF0B0F17) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (selectedType == WidgetType.METRIC_CARD || selectedType == WidgetType.GAUGE_METER) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = metricValue,
                            onValueChange = { metricValue = it },
                            label = { Text("Display Value") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = metricUnit,
                            onValueChange = { metricUnit = it },
                            label = { Text("Unit (e.g. USD, %)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = trendPercent,
                            onValueChange = { trendPercent = it },
                            label = { Text("Trend %") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = threshold,
                            onValueChange = { threshold = it },
                            label = { Text("Target Threshold") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Layout Width Span", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip(
                        selected = widthSpan == WidgetWidth.HALF,
                        onClick = { widthSpan = WidgetWidth.HALF },
                        label = { Text("Half Grid (2-column)") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = widthSpan == WidgetWidth.FULL,
                        onClick = { widthSpan = WidgetWidth.FULL },
                        label = { Text("Full Width (1-column)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (widget != null && onDelete != null) {
                        OutlinedButton(
                            onClick = {
                                onDelete(widget.id)
                                onDismiss()
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Delete")
                        }
                    }

                    Button(
                        onClick = {
                            val newWidget = Widget(
                                id = widget?.id ?: java.util.UUID.randomUUID().toString(),
                                title = title,
                                type = selectedType,
                                width = widthSpan,
                                metricValue = metricValue,
                                metricUnit = metricUnit,
                                trendPercent = trendPercent.toDoubleOrNull() ?: 10.0,
                                trendIsPositive = isPositive,
                                subtitle = subtitle,
                                targetThreshold = threshold.toFloatOrNull(),
                                currentValue = threshold.toFloatOrNull() ?: 70f,
                                points = widget?.points ?: listOf(
                                    ChartPoint("Day 1", 20f),
                                    ChartPoint("Day 2", 35f),
                                    ChartPoint("Day 3", 28f),
                                    ChartPoint("Day 4", 45f),
                                    ChartPoint("Day 5", 60f)
                                ),
                                slices = widget?.slices ?: listOf(
                                    SliceItem("Primary", 55f, "#38BDF8"),
                                    SliceItem("Secondary", 30f, "#00E5FF"),
                                    SliceItem("Other", 15f, "#10B981")
                                ),
                                tableHeaders = widget?.tableHeaders ?: listOf("Metric", "Target", "Status"),
                                tableRows = widget?.tableRows ?: listOf(
                                    listOf("Active Users", "10,000", "Achieved"),
                                    listOf("Latency", "20ms", "Optimal")
                                )
                            )
                            onSave(newWidget)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricBabyBlue,
                            contentColor = Color(0xFF0B0F17)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Widget")
                    }
                }
            }
        }
    }
}
