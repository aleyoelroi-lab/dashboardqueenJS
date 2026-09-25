package com.example.data.generator

import com.example.data.model.ChartPoint
import com.example.data.model.Dashboard
import com.example.data.model.DashboardVariation
import com.example.data.model.SliceItem
import com.example.data.model.Widget
import com.example.data.model.WidgetType
import com.example.data.model.WidgetWidth

object DashboardGeneratorEngine {

    fun generateVariations(userPrompt: String): Dashboard {
        val sanitizedPrompt = userPrompt.trim().ifEmpty { "SaaS Recurring Revenue & Growth Analytics" }
        val title = deriveTitle(sanitizedPrompt)

        val variation1 = createExecutiveSummaryVariation(sanitizedPrompt)
        val variation2 = createRealTimeOperationsVariation(sanitizedPrompt)
        val variation3 = createFunnelConversionVariation(sanitizedPrompt)
        val variation4 = createRegionalCohortVariation(sanitizedPrompt)
        val variation5 = createPredictiveAiRadarVariation(sanitizedPrompt)

        val variations = listOf(variation1, variation2, variation3, variation4, variation5)

        return Dashboard(
            title = title,
            userPrompt = sanitizedPrompt,
            description = "AI-synthesized 5-variant enterprise dashboard architecture tailored for: \"$sanitizedPrompt\". Built for high-frequency data integration, real-time widget reactivity, and multi-format exports.",
            selectedVariationIndex = 0,
            variations = variations,
            activeWidgets = variation1.widgets
        )
    }

    private fun deriveTitle(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("saas") || lower.contains("mrr") || lower.contains("churn") -> "JS Cloud SaaS Revenue & Churn Center"
            lower.contains("ecom") || lower.contains("shop") || lower.contains("order") -> "JS OmniCommerce Conversion Hub"
            lower.contains("health") || lower.contains("patient") || lower.contains("hospital") -> "JS Clinical ICU & Telemetry Console"
            lower.contains("iot") || lower.contains("city") || lower.contains("sensor") -> "JS Smart City IoT Grid Telemetry"
            lower.contains("crypto") || lower.contains("trad") || lower.contains("fintech") -> "JS Algorithmic Quantitative Pulse"
            else -> "JS ${prompt.take(28).replaceFirstChar { it.uppercase() }} Command Center"
        }
    }

    // Variation 1: Executive KPI & Revenue Velocity
    private fun createExecutiveSummaryVariation(prompt: String): DashboardVariation {
        return DashboardVariation(
            variationNumber = 1,
            title = "Executive KPI & Revenue Pulse",
            conceptTagline = "C-Suite High-Level Metric Cards with Trajectory Areas",
            layoutDescription = "Clean hierarchy with top-level financial metrics, continuous revenue progression, and customer health index.",
            targetAudience = "Executive Board & Finance Directors",
            widgets = listOf(
                Widget(
                    title = "Monthly Recurring Revenue",
                    type = WidgetType.METRIC_CARD,
                    width = WidgetWidth.HALF,
                    metricValue = "$284,500",
                    metricUnit = "USD / mo",
                    trendPercent = 14.8,
                    trendIsPositive = true,
                    subtitle = "+$36,200 net new ARR added"
                ),
                Widget(
                    title = "Net Revenue Retention",
                    type = WidgetType.METRIC_CARD,
                    width = WidgetWidth.HALF,
                    metricValue = "118.4%",
                    metricUnit = "YoY",
                    trendPercent = 2.6,
                    trendIsPositive = true,
                    subtitle = "Benchmark >110% Achieved"
                ),
                Widget(
                    title = "Gross Margin Trajectory",
                    type = WidgetType.AREA_CHART,
                    width = WidgetWidth.FULL,
                    subtitle = "Monthly Gross Profit vs Hosting Infrastructure (Last 7 Months)",
                    points = listOf(
                        ChartPoint("Mar", 142f, 38f),
                        ChartPoint("Apr", 168f, 41f),
                        ChartPoint("May", 192f, 44f),
                        ChartPoint("Jun", 215f, 48f),
                        ChartPoint("Jul", 239f, 51f),
                        ChartPoint("Aug", 262f, 53f),
                        ChartPoint("Sep", 284f, 55f)
                    )
                ),
                Widget(
                    title = "Revenue by Customer Tier",
                    type = WidgetType.DONUT_CHART,
                    width = WidgetWidth.HALF,
                    subtitle = "Enterprise vs Mid-Market vs Startup",
                    slices = listOf(
                        SliceItem("Enterprise ($50k+)", 58f, "#38BDF8"),
                        SliceItem("Mid-Market ($15k+)", 27f, "#00E5FF"),
                        SliceItem("Growth SMB ($5k+)", 15f, "#10B981")
                    )
                ),
                Widget(
                    title = "Runway & Cash Efficiency",
                    type = WidgetType.GAUGE_METER,
                    width = WidgetWidth.HALF,
                    subtitle = "Target: 24 Months Safety Cushion",
                    currentValue = 82f,
                    targetThreshold = 75f,
                    metricValue = "28.4 Mos",
                    metricUnit = "Runway"
                ),
                Widget(
                    title = "Recent Top Account Expansions",
                    type = WidgetType.DATA_TABLE,
                    width = WidgetWidth.FULL,
                    subtitle = "Real-time contracts synced from CRM",
                    tableHeaders = listOf("Account", "Tier", "Contract Value", "Status"),
                    tableRows = listOf(
                        listOf("Aramco Digital", "Enterprise", "$120,000/yr", "Active"),
                        listOf("Neom Tech OS", "Enterprise", "$95,000/yr", "Active"),
                        listOf("Red Sea Global", "Mid-Market", "$42,000/yr", "Expanded"),
                        listOf("Riyadh Fintech Lab", "Growth SMB", "$18,500/yr", "New")
                    )
                )
            )
        )
    }

    // Variation 2: High-Density Real-Time Operations Grid
    private fun createRealTimeOperationsVariation(prompt: String): DashboardVariation {
        return DashboardVariation(
            variationNumber = 2,
            title = "High-Density Operations Grid",
            conceptTagline = "Real-Time Telemetry & Micro-Fluctuations",
            layoutDescription = "Dense operational dashboard with live system throughput, queue latencies, and real-time status gauges.",
            targetAudience = "DevOps, SRE, and Operational Command",
            widgets = listOf(
                Widget(
                    title = "Active Live Stream Ingestion",
                    type = WidgetType.METRIC_CARD,
                    width = WidgetWidth.HALF,
                    metricValue = "48,219",
                    metricUnit = "req / sec",
                    trendPercent = 8.1,
                    trendIsPositive = true,
                    subtitle = "P99 Latency: 14.2ms"
                ),
                Widget(
                    title = "Webhook Deliverability SLA",
                    type = WidgetType.METRIC_CARD,
                    width = WidgetWidth.HALF,
                    metricValue = "99.98%",
                    metricUnit = "Uptime",
                    trendPercent = 0.02,
                    trendIsPositive = true,
                    subtitle = "0 dropped packets in 24h"
                ),
                Widget(
                    title = "Live Requests Throughput (QPS)",
                    type = WidgetType.BAR_CHART,
                    width = WidgetWidth.FULL,
                    subtitle = "Distributed edge cluster loads (10-minute bins)",
                    points = listOf(
                        ChartPoint("00:00", 32f),
                        ChartPoint("02:00", 24f),
                        ChartPoint("04:00", 18f),
                        ChartPoint("06:00", 29f),
                        ChartPoint("08:00", 44f),
                        ChartPoint("10:00", 52f),
                        ChartPoint("12:00", 49f),
                        ChartPoint("14:00", 56f)
                    )
                ),
                Widget(
                    title = "Cluster CPU Saturation",
                    type = WidgetType.GAUGE_METER,
                    width = WidgetWidth.HALF,
                    subtitle = "Threshold Limit: 80% Max Load",
                    currentValue = 64f,
                    targetThreshold = 80f,
                    metricValue = "64.2%",
                    metricUnit = "Cluster Load"
                ),
                Widget(
                    title = "Traffic by Protocol",
                    type = WidgetType.DONUT_CHART,
                    width = WidgetWidth.HALF,
                    subtitle = "HTTP/3, gRPC, and Webhooks",
                    slices = listOf(
                        SliceItem("HTTP/3 QUIC", 62f, "#38BDF8"),
                        SliceItem("gRPC Internal", 26f, "#00E5FF"),
                        SliceItem("WordPress Webhook", 12f, "#F59E0B")
                    )
                ),
                Widget(
                    title = "Operational Event Stream",
                    type = WidgetType.DATA_TABLE,
                    width = WidgetWidth.FULL,
                    subtitle = "Live automated triggers & webhook listeners",
                    tableHeaders = listOf("Event ID", "Source", "Payload Size", "Response"),
                    tableRows = listOf(
                        listOf("EVT-9041", "WP-WooCommerce", "1.4 KB", "200 OK"),
                        listOf("EVT-9040", "Stripe-Billing", "2.8 KB", "200 OK"),
                        listOf("EVT-9039", "Postgres-CDC", "8.1 KB", "202 Accepted"),
                        listOf("EVT-9038", "S3-Ingest-Worker", "420 B", "200 OK")
                    )
                )
            )
        )
    }

    // Variation 3: Funnel & Conversion Flow Matrix
    private fun createFunnelConversionVariation(prompt: String): DashboardVariation {
        return DashboardVariation(
            variationNumber = 3,
            title = "Conversion Funnel & Velocity Matrix",
            conceptTagline = "Multi-Stage Acquisition & Drop-Off Analytics",
            layoutDescription = "Specialized funnel visualization showing user acquisition steps, conversion bottlenecks, and CAC efficiency.",
            targetAudience = "Product Growth Managers & Marketers",
            widgets = listOf(
                Widget(
                    title = "Overall Visitor to Paid Conversion",
                    type = WidgetType.METRIC_CARD,
                    width = WidgetWidth.HALF,
                    metricValue = "4.92%",
                    metricUnit = "Conv Rate",
                    trendPercent = 0.84,
                    trendIsPositive = true,
                    subtitle = "Top decile in industry"
                ),
                Widget(
                    title = "Blended Customer Acquisition Cost",
                    type = WidgetType.METRIC_CARD,
                    width = WidgetWidth.HALF,
                    metricValue = "$184",
                    metricUnit = "CAC",
                    trendPercent = -12.4,
                    trendIsPositive = true,
                    subtitle = "Reduced via organic SEO"
                ),
                Widget(
                    title = "Acquisition Funnel Volume (k Visitors)",
                    type = WidgetType.BAR_CHART,
                    width = WidgetWidth.FULL,
                    subtitle = "Stages: Site Visit -> Sign Up -> Connect DB -> Upgrade",
                    points = listOf(
                        ChartPoint("Visit", 124f),
                        ChartPoint("SignUp", 48f),
                        ChartPoint("CreateDash", 26f),
                        ChartPoint("IntegrateData", 14f),
                        ChartPoint("Subscribed", 6.1f)
                    )
                ),
                Widget(
                    title = "Channel Acquisition Mix",
                    type = WidgetType.DONUT_CHART,
                    width = WidgetWidth.HALF,
                    subtitle = "Inbound lead distribution",
                    slices = listOf(
                        SliceItem("Organic Search", 44f, "#38BDF8"),
                        SliceItem("Developer Word-of-Mouth", 31f, "#10B981"),
                        SliceItem("WordPress Plugin", 16f, "#00E5FF"),
                        SliceItem("Paid Ads", 9f, "#A855F7")
                    )
                ),
                Widget(
                    title = "Trial to Paid Velocity",
                    type = WidgetType.GAUGE_METER,
                    width = WidgetWidth.HALF,
                    subtitle = "Average Days to Convert (Target: 7)",
                    currentValue = 72f,
                    targetThreshold = 60f,
                    metricValue = "5.2 Days",
                    metricUnit = "Avg Conversion"
                ),
                Widget(
                    title = "Top Converting Campaign Touchpoints",
                    type = WidgetType.DATA_TABLE,
                    width = WidgetWidth.FULL,
                    subtitle = "Live attribution tracking",
                    tableHeaders = listOf("Campaign", "Signups", "Conversion", "ROI"),
                    tableRows = listOf(
                        listOf("Google Cloud Summit", "1,240", "8.2%", "4.8x"),
                        listOf("WordPress Directory Feature", "890", "6.4%", "6.2x"),
                        listOf("AI Studio Showcase", "2,100", "9.1%", "8.0x"),
                        listOf("GitHub Community Repo", "650", "5.8%", "3.5x")
                    )
                )
            )
        )
    }

    // Variation 4: Regional Cohort & Geographic Breakdown
    private fun createRegionalCohortVariation(prompt: String): DashboardVariation {
        return DashboardVariation(
            variationNumber = 4,
            title = "Geographic & Cohort Retention",
            conceptTagline = "Cross-Territory Revenue & Long-Term Sticky Cohorts",
            layoutDescription = "Tailored for multi-region operations with regional contribution breakdowns, localization metrics, and monthly cohort retention curves.",
            targetAudience = "Global Expansion & Operations Leads",
            widgets = listOf(
                Widget(
                    title = "Total Active Global Organizations",
                    type = WidgetType.METRIC_CARD,
                    width = WidgetWidth.HALF,
                    metricValue = "3,480",
                    metricUnit = "Tenants",
                    trendPercent = 21.0,
                    trendIsPositive = true,
                    subtitle = "Spanning 42 Countries"
                ),
                Widget(
                    title = "Average Revenue Per Account",
                    type = WidgetType.METRIC_CARD,
                    width = WidgetWidth.HALF,
                    metricValue = "$1,840",
                    metricUnit = "ARPA / mo",
                    trendPercent = 7.4,
                    trendIsPositive = true,
                    subtitle = "+$130 vs prior quarter"
                ),
                Widget(
                    title = "Monthly Cohort Retention Curve (%)",
                    type = WidgetType.AREA_CHART,
                    width = WidgetWidth.FULL,
                    subtitle = "Retention over 6 months for Q1/Q2 cohorts",
                    points = listOf(
                        ChartPoint("M0", 100f),
                        ChartPoint("M1", 94f),
                        ChartPoint("M2", 91f),
                        ChartPoint("M3", 89f),
                        ChartPoint("M4", 88f),
                        ChartPoint("M5", 87f),
                        ChartPoint("M6", 87f)
                    )
                ),
                Widget(
                    title = "Regional Revenue Contribution",
                    type = WidgetType.DONUT_CHART,
                    width = WidgetWidth.HALF,
                    subtitle = "Middle East, Europe, North America, APAC",
                    slices = listOf(
                        SliceItem("GCC & Middle East", 46f, "#38BDF8"),
                        SliceItem("North America", 28f, "#00E5FF"),
                        SliceItem("Europe", 18f, "#10B981"),
                        SliceItem("Asia Pacific", 8f, "#F59E0B")
                    )
                ),
                Widget(
                    title = "Middle East Localization Compliance",
                    type = WidgetType.GAUGE_METER,
                    width = WidgetWidth.HALF,
                    subtitle = "PDPL & ZATCA Tax Ready",
                    currentValue = 96f,
                    targetThreshold = 90f,
                    metricValue = "96%",
                    metricUnit = "Audit Ready"
                ),
                Widget(
                    title = "Top Regional Client Nodes",
                    type = WidgetType.DATA_TABLE,
                    width = WidgetWidth.FULL,
                    subtitle = "Aggregated territorial analytics",
                    tableHeaders = listOf("City Hub", "Active Tenants", "Monthly Volume", "Health"),
                    tableRows = listOf(
                        listOf("Jeddah, KSA", "840", "$118,000", "99.9%"),
                        listOf("Riyadh, KSA", "920", "$142,000", "100.0%"),
                        listOf("Dubai, UAE", "610", "$78,000", "99.8%"),
                        listOf("London, UK", "430", "$54,000", "99.7%")
                    )
                )
            )
        )
    }

    // Variation 5: Predictive AI Forecasting & Anomaly Radar
    private fun createPredictiveAiRadarVariation(prompt: String): DashboardVariation {
        return DashboardVariation(
            variationNumber = 5,
            title = "Predictive AI Forecast & Anomaly Radar",
            conceptTagline = "Gemini Pro Powered Forecasting & Outlier Detection",
            layoutDescription = "Next-generation predictive dashboard forecasting future revenue runway, anomaly indicators, and automated recommendation engines.",
            targetAudience = "Data Architects & AI Strategy Leadership",
            widgets = listOf(
                Widget(
                    title = "Predicted Q4 Revenue Runway",
                    type = WidgetType.METRIC_CARD,
                    width = WidgetWidth.HALF,
                    metricValue = "$1.18M",
                    metricUnit = "Forecast",
                    trendPercent = 28.5,
                    trendIsPositive = true,
                    subtitle = "95% Confidence Interval (Gemini 3.1)"
                ),
                Widget(
                    title = "Anomaly Risk Severity Index",
                    type = WidgetType.METRIC_CARD,
                    width = WidgetWidth.HALF,
                    metricValue = "0.04",
                    metricUnit = "Low Risk",
                    trendPercent = -42.0,
                    trendIsPositive = true,
                    subtitle = "No critical spikes detected"
                ),
                Widget(
                    title = "Historical vs AI Forecast Trajectory",
                    type = WidgetType.AREA_CHART,
                    width = WidgetWidth.FULL,
                    subtitle = "Solid = Real Data | Dashed = Gemini Predictive Extrapolation",
                    points = listOf(
                        ChartPoint("Aug", 260f, 260f),
                        ChartPoint("Sep", 284f, 284f),
                        ChartPoint("Oct*", 312f, 310f),
                        ChartPoint("Nov*", 345f, 340f),
                        ChartPoint("Dec*", 380f, 375f),
                        ChartPoint("Jan*", 425f, 415f)
                    )
                ),
                Widget(
                    title = "System Anomaly Detection Radar",
                    type = WidgetType.DONUT_CHART,
                    width = WidgetWidth.HALF,
                    subtitle = "Telemetry anomaly taxonomy",
                    slices = listOf(
                        SliceItem("Normal Baseline", 88f, "#10B981"),
                        SliceItem("Transient Query Spikes", 8f, "#38BDF8"),
                        SliceItem("Webhook Delay Warning", 4f, "#F59E0B")
                    )
                ),
                Widget(
                    title = "Model Confidence Score",
                    type = WidgetType.GAUGE_METER,
                    width = WidgetWidth.HALF,
                    subtitle = "Gemini Pro Synthesis Accuracy",
                    currentValue = 94f,
                    targetThreshold = 85f,
                    metricValue = "94.8%",
                    metricUnit = "Confidence"
                ),
                Widget(
                    title = "AI Optimization Directives",
                    type = WidgetType.DATA_TABLE,
                    width = WidgetWidth.FULL,
                    subtitle = "Automated proactive recommendations",
                    tableHeaders = listOf("Area", "Identified Vector", "Recommended Action", "Impact"),
                    tableRows = listOf(
                        listOf("Cache Layer", "Redis Memory 78%", "Scale cache cluster +2 nodes", "+12% Speed"),
                        listOf("Retention", "SMB Day 14 Drop", "Trigger onboarding sequence", "+3.4% Conv"),
                        listOf("Database", "Unindexed Query", "Add B-Tree Index on user_id", "-80ms Lag"),
                        listOf("Webhooks", "WP Retry Queue", "Enable exponential backoff", "Zero Loss")
                    )
                )
            )
        )
    }
}
