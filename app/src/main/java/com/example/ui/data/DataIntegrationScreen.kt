package com.example.ui.data

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Storage
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
import com.example.data.integration.DataParser
import com.example.data.model.DataSourceType
import com.example.ui.theme.DarkGrayCard
import com.example.ui.theme.ElectricBabyBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MetricEmerald
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.DashboardViewModel

@Composable
fun DataIntegrationScreen(
    viewModel: DashboardViewModel,
    onNavigate: (AppScreen) -> Unit
) {
    val integratedSources by viewModel.integratedDataSources.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: CSV, 1: Excel, 2: SQL

    var csvText by remember { mutableStateOf(DataParser.getSampleCsvContent()) }
    var excelText by remember { mutableStateOf(DataParser.getSampleExcelTabular()) }
    var sqlQuery by remember { mutableStateOf(DataParser.getSampleSqlQuery()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Column {
                Text(
                    text = "Real-Time Data Pipelines",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Stream CSV, Excel sheets, and SQL database queries directly into active widgets",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Tab Selector (CSV, Excel, SQL)
        item {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = ElectricBabyBlue,
                    indicator = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("CSV Ingestion", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Excel Tabular", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("SQL Database", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }
        }

        // Active Ingestion Box
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    when (selectedTab) {
                        0 -> {
                            // CSV Tab
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("CSV File Content / Payload", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                TextButton(onClick = { csvText = DataParser.getSampleCsvContent() }) {
                                    Text("Reset Sample", fontSize = 10.sp, color = ElectricBabyBlue)
                                }
                            }

                            OutlinedTextField(
                                value = csvText,
                                onValueChange = { csvText = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .testTag("csv_input_field"),
                                textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 10.5.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricBabyBlue,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    viewModel.importCsv(csvText, "SaaS_Metrics_Stream.csv")
                                    onNavigate(AppScreen.WORKSPACE)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricBabyBlue,
                                    contentColor = Color(0xFF0B0F17)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().testTag("ingest_csv_button")
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Ingest CSV & Map to Dashboard", fontWeight = FontWeight.Bold)
                            }
                        }

                        1 -> {
                            // Excel Tab
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Excel Workbook Sheet (Tab-Delimited)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                TextButton(onClick = { excelText = DataParser.getSampleExcelTabular() }) {
                                    Text("Reset Sample", fontSize = 10.sp, color = ElectricCyan)
                                }
                            }

                            OutlinedTextField(
                                value = excelText,
                                onValueChange = { excelText = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .testTag("excel_input_field"),
                                textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 10.5.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricCyan,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    viewModel.importExcel(excelText, "Q3_Performance_Sheet.xlsx")
                                    onNavigate(AppScreen.WORKSPACE)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricCyan,
                                    contentColor = Color(0xFF0B0F17)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().testTag("ingest_excel_button")
                            ) {
                                Icon(Icons.Default.Dataset, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Map Excel Sheet to Visualizations", fontWeight = FontWeight.Bold)
                            }
                        }

                        2 -> {
                            // SQL Tab
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("PostgreSQL / MySQL Query Runner", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                TextButton(onClick = { sqlQuery = DataParser.getSampleSqlQuery() }) {
                                    Text("Reset Query", fontSize = 10.sp, color = MetricEmerald)
                                }
                            }

                            OutlinedTextField(
                                value = sqlQuery,
                                onValueChange = { sqlQuery = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .testTag("sql_input_field"),
                                textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 10.5.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MetricEmerald,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    viewModel.importSql(sqlQuery, "js_transactions_pipeline.sql")
                                    onNavigate(AppScreen.WORKSPACE)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MetricEmerald,
                                    contentColor = Color(0xFF0B0F17)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().testTag("execute_sql_button")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Execute Query & Mount Real-time Feed", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Mounted Datasets List
        item {
            Text(
                text = "Mounted Data Sources (${integratedSources.size})",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(integratedSources) { src ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = when (src.type) {
                            DataSourceType.CSV -> ElectricBabyBlue.copy(alpha = 0.15f)
                            DataSourceType.EXCEL -> ElectricCyan.copy(alpha = 0.15f)
                            DataSourceType.SQL -> MetricEmerald.copy(alpha = 0.15f)
                            DataSourceType.WORDPRESS_WEBHOOK -> Color(0xFFF59E0B).copy(alpha = 0.15f)
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                when (src.type) {
                                    DataSourceType.CSV -> Icons.Default.CloudUpload
                                    DataSourceType.EXCEL -> Icons.Default.Dataset
                                    DataSourceType.SQL -> Icons.Default.Storage
                                    DataSourceType.WORDPRESS_WEBHOOK -> Icons.Default.CheckCircle
                                },
                                contentDescription = null,
                                tint = when (src.type) {
                                    DataSourceType.CSV -> ElectricBabyBlue
                                    DataSourceType.EXCEL -> ElectricCyan
                                    DataSourceType.SQL -> MetricEmerald
                                    DataSourceType.WORDPRESS_WEBHOOK -> Color(0xFFF59E0B)
                                },
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = src.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = src.previewSnippet,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }

                    Surface(
                        color = MetricEmerald.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "CONNECTED",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MetricEmerald,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
