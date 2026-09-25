package com.example.data.model

import java.util.UUID

enum class WidgetType {
    METRIC_CARD,
    AREA_CHART,
    BAR_CHART,
    DONUT_CHART,
    DATA_TABLE,
    GAUGE_METER
}

enum class WidgetWidth {
    HALF,
    FULL
}

data class ChartPoint(
    val label: String,
    val value: Float,
    val secondaryValue: Float? = null
)

data class SliceItem(
    val label: String,
    val value: Float,
    val colorHex: String
)

data class Widget(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val type: WidgetType,
    val width: WidgetWidth = WidgetWidth.FULL,
    val metricValue: String = "",
    val metricUnit: String = "",
    val trendPercent: Double = 0.0,
    val trendIsPositive: Boolean = true,
    val subtitle: String = "",
    val points: List<ChartPoint> = emptyList(),
    val slices: List<SliceItem> = emptyList(),
    val tableHeaders: List<String> = emptyList(),
    val tableRows: List<List<String>> = emptyList(),
    val targetThreshold: Float? = null,
    val currentValue: Float = 0f
)

data class DashboardVariation(
    val id: String = UUID.randomUUID().toString(),
    val variationNumber: Int,
    val title: String,
    val conceptTagline: String,
    val layoutDescription: String,
    val targetAudience: String,
    val widgets: List<Widget>
)

data class Dashboard(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val userPrompt: String,
    val description: String,
    val theme: String = "Enterprise Jeddah San Dark",
    val selectedVariationIndex: Int = 0,
    val variations: List<DashboardVariation> = emptyList(),
    val activeWidgets: List<Widget> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis()
)

enum class DataSourceType {
    CSV,
    EXCEL,
    SQL,
    WORDPRESS_WEBHOOK
}

data class IntegratedDataSource(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: DataSourceType,
    val rowCount: Int,
    val columnNames: List<String>,
    val uploadedAt: Long = System.currentTimeMillis(),
    val previewSnippet: String,
    val isLiveStreaming: Boolean = false
)

data class WebhookConfig(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "WordPress Production Hook",
    val endpointUrl: String = "https://jeddahsan-dash.internal/wp-json/js-dashboard/v1/trigger",
    val secretKey: String = "whsec_js_" + UUID.randomUUID().toString().take(16),
    val triggers: List<String> = listOf("WooCommerce New Order", "User Registration", "Inventory Alert", "Daily Report Cron"),
    val isActive: Boolean = true,
    val lastPingStatus: String = "200 OK - Connected",
    val totalRequestsDelivered: Int = 142
)

data class WebhookEventLog(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: String,
    val source: String,
    val payloadPreview: String,
    val httpStatus: Int,
    val isSuccess: Boolean
)

data class AdminSecurityRecord(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val actor: String,
    val role: String,
    val action: String,
    val resource: String,
    val ipAddress: String,
    val status: String = "AUTHORIZED"
)

enum class ExportLanguage {
    HTML_STANDALONE,
    REACT_TAILWIND,
    JETPACK_COMPOSE,
    EXCEL_CSV
}
