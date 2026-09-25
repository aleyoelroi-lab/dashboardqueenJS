package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.admin.AdminSecurityManager
import com.example.data.api.GeminiDashboardService
import com.example.data.generator.DashboardGeneratorEngine
import com.example.data.integration.DataParser
import com.example.data.integration.WordPressWebhookManager
import com.example.data.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    VARIATIONS,
    WORKSPACE,
    WEB_PREVIEW,
    DATA_INTEGRATION,
    WORDPRESS_WEBHOOKS,
    ADMIN_SECURITY
}

class DashboardViewModel : ViewModel() {

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _dashboard = MutableStateFlow(
        DashboardGeneratorEngine.generateVariations("SaaS Recurring Revenue & Growth Analytics")
    )
    val dashboard: StateFlow<Dashboard> = _dashboard.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _isLiveStreaming = MutableStateFlow(false)
    val isLiveStreaming: StateFlow<Boolean> = _isLiveStreaming.asStateFlow()

    private val _integratedDataSources = MutableStateFlow<List<IntegratedDataSource>>(
        listOf(
            IntegratedDataSource(
                name = "Production_SaaS_Seed.csv",
                type = DataSourceType.CSV,
                rowCount = 120,
                columnNames = listOf("Month", "Revenue_USD", "Gross_Margin", "Churn"),
                previewSnippet = "Columns: Month, Revenue_USD, Gross_Margin, Churn (120 rows synced)"
            )
        )
    )
    val integratedDataSources: StateFlow<List<IntegratedDataSource>> = _integratedDataSources.asStateFlow()

    private val _editingWidget = MutableStateFlow<Widget?>(null)
    val editingWidget: StateFlow<Widget?> = _editingWidget.asStateFlow()

    private val _exportModalOpen = MutableStateFlow(false)
    val exportModalOpen: StateFlow<Boolean> = _exportModalOpen.asStateFlow()

    private val _activeExportLanguage = MutableStateFlow(ExportLanguage.HTML_STANDALONE)
    val activeExportLanguage: StateFlow<ExportLanguage> = _activeExportLanguage.asStateFlow()

    private val _userFeedbackMessage = MutableStateFlow<String?>(null)
    val userFeedbackMessage: StateFlow<String?> = _userFeedbackMessage.asStateFlow()

    private var streamingJob: Job? = null

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
        AdminSecurityManager.logAction("THEME_TOGGLE", "Dark mode: ${_isDarkMode.value}")
    }

    fun setExportModal(open: Boolean, language: ExportLanguage = ExportLanguage.HTML_STANDALONE) {
        _exportModalOpen.value = open
        _activeExportLanguage.value = language
        if (open) {
            AdminSecurityManager.logAction("EXPORT_REQUEST", "Language: $language")
        }
    }

    fun setEditingWidget(widget: Widget?) {
        _editingWidget.value = widget
    }

    fun generateDashboard(prompt: String) {
        viewModelScope.launch {
            _isGenerating.value = true
            AdminSecurityManager.logAction("DASHBOARD_GENERATE_START", "Prompt: $prompt")
            try {
                val newDashboard = GeminiDashboardService.generateDashboard(prompt)
                _dashboard.value = newDashboard
                _currentScreen.value = AppScreen.VARIATIONS
                AdminSecurityManager.logAction("DASHBOARD_GENERATE_SUCCESS", "Variants: 5")
                _userFeedbackMessage.value = "Generated 5 enterprise dashboard variations!"
            } catch (e: Exception) {
                val fallback = DashboardGeneratorEngine.generateVariations(prompt)
                _dashboard.value = fallback
                _currentScreen.value = AppScreen.VARIATIONS
                _userFeedbackMessage.value = "Generated 5 variations via offline architecture engine."
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun selectVariation(variationIndex: Int) {
        val currentDash = _dashboard.value
        if (variationIndex in currentDash.variations.indices) {
            val selected = currentDash.variations[variationIndex]
            _dashboard.value = currentDash.copy(
                selectedVariationIndex = variationIndex,
                activeWidgets = selected.widgets
            )
            _currentScreen.value = AppScreen.WORKSPACE
            AdminSecurityManager.logAction("VARIATION_SELECT", "Selected: ${selected.title}")
            _userFeedbackMessage.value = "Loaded ${selected.title} into workspace!"
        }
    }

    fun updateWidget(updated: Widget) {
        val current = _dashboard.value.activeWidgets.toMutableList()
        val index = current.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            current[index] = updated
            _dashboard.value = _dashboard.value.copy(activeWidgets = current)
            _editingWidget.value = null
            AdminSecurityManager.logAction("WIDGET_CUSTOMIZE", "Updated: ${updated.title}")
            _userFeedbackMessage.value = "Widget '${updated.title}' updated successfully"
        }
    }

    fun deleteWidget(widgetId: String) {
        val current = _dashboard.value.activeWidgets.filter { it.id != widgetId }
        _dashboard.value = _dashboard.value.copy(activeWidgets = current)
        AdminSecurityManager.logAction("WIDGET_DELETE", "Widget ID: $widgetId")
        _userFeedbackMessage.value = "Widget removed"
    }

    fun addWidget(widget: Widget) {
        val current = _dashboard.value.activeWidgets + widget
        _dashboard.value = _dashboard.value.copy(activeWidgets = current)
        _editingWidget.value = null
        AdminSecurityManager.logAction("WIDGET_ADD", "Added: ${widget.title}")
        _userFeedbackMessage.value = "New widget added!"
    }

    fun importCsv(content: String, filename: String = "data_feed.csv") {
        val (source, newWidgets) = DataParser.parseCsv(content, filename)
        _integratedDataSources.value = listOf(source) + _integratedDataSources.value
        if (newWidgets.isNotEmpty()) {
            _dashboard.value = _dashboard.value.copy(
                activeWidgets = newWidgets + _dashboard.value.activeWidgets
            )
        }
        AdminSecurityManager.logAction("DATA_INGEST_CSV", "File: $filename (${source.rowCount} rows)")
        _userFeedbackMessage.value = "CSV imported: ${source.rowCount} rows mapped to charts"
    }

    fun importExcel(content: String, filename: String = "sheet.xlsx") {
        val (source, newWidgets) = DataParser.parseExcelTabular(content, filename)
        _integratedDataSources.value = listOf(source) + _integratedDataSources.value
        if (newWidgets.isNotEmpty()) {
            _dashboard.value = _dashboard.value.copy(
                activeWidgets = newWidgets + _dashboard.value.activeWidgets
            )
        }
        AdminSecurityManager.logAction("DATA_INGEST_EXCEL", "File: $filename (${source.rowCount} records)")
        _userFeedbackMessage.value = "Excel sheet mapped to dashboard!"
    }

    fun importSql(query: String, queryName: String = "custom_query.sql") {
        val (source, newWidgets) = DataParser.parseSqlQuery(query, queryName)
        _integratedDataSources.value = listOf(source) + _integratedDataSources.value
        if (newWidgets.isNotEmpty()) {
            _dashboard.value = _dashboard.value.copy(
                activeWidgets = newWidgets + _dashboard.value.activeWidgets
            )
        }
        AdminSecurityManager.logAction("DATA_INGEST_SQL", "Query execution simulated ($queryName)")
        _userFeedbackMessage.value = "SQL query executed & feed mounted!"
    }

    fun triggerWordPressWebhook(eventType: String) {
        val log = WordPressWebhookManager.triggerEvent(eventType)
        // Dynamically update the first metric or table row to reflect live reaction
        val currentWidgets = _dashboard.value.activeWidgets.toMutableList()
        val firstMetricIndex = currentWidgets.indexOfFirst { it.type == WidgetType.METRIC_CARD }
        if (firstMetricIndex != -1) {
            val w = currentWidgets[firstMetricIndex]
            val numericVal = w.metricValue.filter { it.isDigit() }.toIntOrNull() ?: 100
            val updatedVal = if (w.metricValue.startsWith("$")) "$${numericVal + (10..500).random()}" else "${numericVal + 1}"
            currentWidgets[firstMetricIndex] = w.copy(
                metricValue = updatedVal,
                trendPercent = w.trendPercent + 0.3
            )
            _dashboard.value = _dashboard.value.copy(activeWidgets = currentWidgets)
        }
        AdminSecurityManager.logAction("WEBHOOK_INGEST", "Event: $eventType (${log.httpStatus})")
        _userFeedbackMessage.value = "Webhook event received: ${log.eventType}"
    }

    fun toggleRealTimeStreaming() {
        val target = !_isLiveStreaming.value
        _isLiveStreaming.value = target

        if (target) {
            streamingJob = viewModelScope.launch {
                while (isActive) {
                    delay(3000)
                    pulseRealTimeData()
                }
            }
            AdminSecurityManager.logAction("STREAM_START", "WebSocket Live Pulse Active (3s)")
            _userFeedbackMessage.value = "Live telemetry streaming enabled"
        } else {
            streamingJob?.cancel()
            streamingJob = null
            AdminSecurityManager.logAction("STREAM_STOP", "WebSocket Paused")
            _userFeedbackMessage.value = "Live streaming paused"
        }
    }

    private fun pulseRealTimeData() {
        val widgets = _dashboard.value.activeWidgets.map { widget ->
            when (widget.type) {
                WidgetType.AREA_CHART, WidgetType.BAR_CHART -> {
                    val updatedPoints = widget.points.mapIndexed { idx, pt ->
                        if (idx == widget.points.size - 1) {
                            val jitter = (-5..8).random().toFloat()
                            pt.copy(value = (pt.value + jitter).coerceAtLeast(10f))
                        } else pt
                    }
                    widget.copy(points = updatedPoints)
                }
                WidgetType.GAUGE_METER -> {
                    val jitter = (-3..3).random()
                    val newGauge = (widget.currentValue + jitter).coerceIn(10f, 98f)
                    widget.copy(
                        currentValue = newGauge,
                        metricValue = "${newGauge.toInt()}%"
                    )
                }
                else -> widget
            }
        }
        _dashboard.value = _dashboard.value.copy(activeWidgets = widgets)
    }

    fun clearFeedback() {
        _userFeedbackMessage.value = null
    }
}
