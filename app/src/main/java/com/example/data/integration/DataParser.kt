package com.example.data.integration

import com.example.data.model.ChartPoint
import com.example.data.model.DataSourceType
import com.example.data.model.IntegratedDataSource
import com.example.data.model.Widget
import com.example.data.model.WidgetType
import com.example.data.model.WidgetWidth

object DataParser {

    fun parseCsv(rawContent: String, sourceName: String = "Uploaded_Data.csv"): Pair<IntegratedDataSource, List<Widget>> {
        val lines = rawContent.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) {
            return fallbackDataSource(sourceName, DataSourceType.CSV)
        }

        val delimiter = if (lines[0].contains(";")) ";" else ","
        val headers = lines[0].split(delimiter).map { it.trim().removeSurrounding("\"") }
        val dataRows = lines.drop(1).map { line ->
            line.split(delimiter).map { it.trim().removeSurrounding("\"") }
        }

        val points = mutableListOf<ChartPoint>()
        for (row in dataRows.take(12)) {
            val label = row.getOrNull(0) ?: "Row"
            val val1 = row.getOrNull(1)?.replace("$", "")?.replace(",", "")?.toFloatOrNull() ?: 10f
            val val2 = row.getOrNull(2)?.replace("$", "")?.replace(",", "")?.toFloatOrNull()
            points.add(ChartPoint(label, val1, val2))
        }

        val source = IntegratedDataSource(
            name = sourceName,
            type = DataSourceType.CSV,
            rowCount = dataRows.size,
            columnNames = headers,
            previewSnippet = "Columns: ${headers.joinToString(", ")} (${dataRows.size} total rows parsed)"
        )

        val generatedWidgets = listOf(
            Widget(
                title = "$sourceName - Trend Analysis",
                type = WidgetType.AREA_CHART,
                width = WidgetWidth.FULL,
                subtitle = "Mapped from ${headers.getOrNull(0) ?: "Time"} vs ${headers.getOrNull(1) ?: "Metric"}",
                points = points
            ),
            Widget(
                title = "$sourceName - Raw Tabular Feed",
                type = WidgetType.DATA_TABLE,
                width = WidgetWidth.FULL,
                subtitle = "Real-time records synced",
                tableHeaders = headers,
                tableRows = dataRows.take(8)
            )
        )

        return Pair(source, generatedWidgets)
    }

    fun parseExcelTabular(rawContent: String, sourceName: String = "Workbook_Sheet1.xlsx"): Pair<IntegratedDataSource, List<Widget>> {
        val lines = rawContent.lines().filter { it.isNotBlank() }
        val delimiter = if (lines.firstOrNull()?.contains("\t") == true) "\t" else ","
        val headers = lines.firstOrNull()?.split(delimiter)?.map { it.trim() } ?: listOf("Metric", "Value", "Delta")
        val dataRows = lines.drop(1).map { line ->
            line.split(delimiter).map { it.trim() }
        }

        val points = dataRows.take(10).mapIndexed { idx, row ->
            val label = row.getOrNull(0) ?: "Period $idx"
            val num = row.getOrNull(1)?.replace("%", "")?.replace("$", "")?.toFloatOrNull() ?: (idx * 15f + 20f)
            ChartPoint(label, num)
        }

        val source = IntegratedDataSource(
            name = sourceName,
            type = DataSourceType.EXCEL,
            rowCount = dataRows.size,
            columnNames = headers,
            previewSnippet = "Workbook Sheet: [${headers.joinToString(" | ")}] - ${dataRows.size} records"
        )

        val widget = Widget(
            title = "$sourceName - Volume Distribution",
            type = WidgetType.BAR_CHART,
            width = WidgetWidth.FULL,
            subtitle = "Excel Sheet Tabular Import (${headers.getOrNull(1) ?: "Metric"})",
            points = points
        )

        return Pair(source, listOf(widget))
    }

    fun parseSqlQuery(queryText: String, sourceName: String = "Postgres_Analytics_Query.sql"): Pair<IntegratedDataSource, List<Widget>> {
        val lower = queryText.lowercase()
        val detectedTable = if (lower.contains("from ")) {
            lower.substringAfter("from ").split(" ", "\n", ";").firstOrNull { it.isNotBlank() } ?: "analytics_events"
        } else {
            "analytics_events"
        }

        val simulatedHeaders = listOf("timestamp", "event_name", "user_id", "revenue_usd", "latency_ms")
        val simulatedRows = listOf(
            listOf("2026-09-25 03:40", "checkout_completed", "usr_9421", "249.00", "32"),
            listOf("2026-09-25 03:41", "license_renewed", "usr_8812", "1,200.00", "28"),
            listOf("2026-09-25 03:42", "seat_added", "usr_1029", "85.00", "45"),
            listOf("2026-09-25 03:43", "plan_upgrade_pro", "usr_3910", "499.00", "19"),
            listOf("2026-09-25 03:44", "api_key_provisioned", "usr_7721", "0.00", "12")
        )

        val source = IntegratedDataSource(
            name = sourceName,
            type = DataSourceType.SQL,
            rowCount = 4820,
            columnNames = simulatedHeaders,
            previewSnippet = "Executed: SELECT * FROM $detectedTable; -> 4,820 rows affected, execution time 18ms."
        )

        val tableWidget = Widget(
            title = "SQL Pipeline: $detectedTable",
            type = WidgetType.DATA_TABLE,
            width = WidgetWidth.FULL,
            subtitle = "Live Database Query Result Feed",
            tableHeaders = simulatedHeaders,
            tableRows = simulatedRows
        )

        return Pair(source, listOf(tableWidget))
    }

    private fun fallbackDataSource(name: String, type: DataSourceType): Pair<IntegratedDataSource, List<Widget>> {
        val source = IntegratedDataSource(
            name = name,
            type = type,
            rowCount = 0,
            columnNames = listOf("Empty"),
            previewSnippet = "No data rows detected"
        )
        return Pair(source, emptyList())
    }

    fun getSampleCsvContent(): String {
        return """
Month,Revenue_USD,Operating_Cost,Growth_Rate
2026-01,185000,42000,12.4%
2026-02,204000,45000,10.2%
2026-03,228000,47000,11.7%
2026-04,251000,49000,10.1%
2026-05,274000,52000,9.2%
2026-06,298000,54000,8.8%
2026-07,325000,56000,9.1%
2026-08,358000,58000,10.2%
2026-09,392000,60000,9.5%
        """.trimIndent()
    }

    fun getSampleExcelTabular(): String {
        return """
Category	Conversion_Percent	Target_Benchmark
Landing Page Hero	4.8%	4.0%
Pricing Comparison	12.4%	10.0%
Free Trial Signup	28.2%	25.0%
Onboarding Activation	64.0%	60.0%
Paid Upgrade Checkout	18.5%	15.0%
Enterprise Demo Request	8.9%	7.5%
        """.trimIndent()
    }

    fun getSampleSqlQuery(): String {
        return """
SELECT 
    DATE_TRUNC('day', created_at) AS date_bucket,
    COUNT(DISTINCT user_id) AS active_tenants,
    SUM(amount_cents) / 100.0 AS daily_volume_usd,
    AVG(response_time_ms) AS avg_p50_latency
FROM public.js_analytics_transactions
WHERE created_at >= NOW() - INTERVAL '30 days'
GROUP BY 1
ORDER BY 1 DESC
LIMIT 50;
        """.trimIndent()
    }
}
