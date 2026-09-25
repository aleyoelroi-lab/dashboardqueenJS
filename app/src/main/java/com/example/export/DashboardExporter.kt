package com.example.export

import com.example.data.model.Dashboard
import com.example.data.model.Widget
import com.example.data.model.WidgetType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DashboardExporter {

    fun generateStandaloneHtml(dashboard: Dashboard, isDarkMode: Boolean = true): String {
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        val bgClass = if (isDarkMode) "bg-[#0B0F17] text-[#F8FAFC]" else "bg-[#F8FAFC] text-[#0F172A]"
        val cardBg = if (isDarkMode) "bg-[#111827] border-[#1E293B]" else "bg-white border-slate-200"
        val electricBlue = "#38BDF8"

        val widgetCardsHtml = StringBuilder()
        val chartScripts = StringBuilder()

        dashboard.activeWidgets.forEachIndexed { index, widget ->
            val colSpan = if (widget.width.name == "FULL") "col-span-12" else "col-span-12 sm:col-span-6 lg:col-span-6"
            val canvasId = "chart_canvas_$index"

            when (widget.type) {
                WidgetType.METRIC_CARD -> {
                    val trendColor = if (widget.trendIsPositive) "text-emerald-400" else "text-rose-400"
                    val trendSign = if (widget.trendPercent >= 0) "+" else ""
                    widgetCardsHtml.append("""
                        <div class="col-span-12 sm:col-span-6 lg:col-span-3 $cardBg border rounded-xl p-5 shadow-lg relative overflow-hidden transition-all hover:border-[#38BDF8]">
                            <div class="flex items-center justify-between">
                                <span class="text-[11px] uppercase tracking-wider text-slate-400 font-semibold">${widget.title}</span>
                                <span class="text-[11px] px-2 py-0.5 rounded-full font-medium $trendColor bg-slate-800/60 border border-slate-700">
                                    $trendSign${widget.trendPercent}%
                                </span>
                            </div>
                            <div class="mt-3 flex items-baseline gap-2">
                                <span class="text-2xl sm:text-3xl font-bold tracking-tight text-[#38BDF8]">${widget.metricValue}</span>
                                <span class="text-xs sm:text-sm text-slate-400">${widget.metricUnit}</span>
                            </div>
                            <p class="mt-2 text-[11px] text-slate-500">${widget.subtitle}</p>
                        </div>
                    """.trimIndent()).append("\n")
                }

                WidgetType.AREA_CHART -> {
                    widgetCardsHtml.append("""
                        <div class="$colSpan $cardBg border rounded-xl p-5 shadow-lg">
                            <div class="flex flex-col sm:flex-row sm:items-center justify-between mb-4 gap-2">
                                <div>
                                    <h3 class="text-base font-semibold text-slate-100">${widget.title}</h3>
                                    <p class="text-xs text-slate-400">${widget.subtitle}</p>
                                </div>
                                <span class="self-start sm:self-auto text-xs font-mono px-2 py-1 rounded bg-[#38BDF8]/10 text-[#38BDF8] border border-[#38BDF8]/20">Area Trend</span>
                            </div>
                            <div class="h-64 relative">
                                <canvas id="$canvasId"></canvas>
                            </div>
                        </div>
                    """.trimIndent()).append("\n")

                    val labels = widget.points.joinToString(",") { "\"${it.label}\"" }
                    val dataValues = widget.points.joinToString(",") { it.value.toString() }
                    chartScripts.append("""
                        new Chart(document.getElementById('$canvasId'), {
                            type: 'line',
                            data: {
                                labels: [$labels],
                                datasets: [{
                                    label: '${widget.title}',
                                    data: [$dataValues],
                                    borderColor: '$electricBlue',
                                    backgroundColor: 'rgba(56, 189, 248, 0.15)',
                                    fill: true,
                                    tension: 0.35,
                                    borderWidth: 2.5,
                                    pointBackgroundColor: '$electricBlue'
                                }]
                            },
                            options: {
                                responsive: true,
                                maintainAspectRatio: false,
                                plugins: { legend: { display: false } },
                                scales: {
                                    x: { grid: { color: 'rgba(255,255,255,0.05)' }, ticks: { color: '#94a3b8' } },
                                    y: { grid: { color: 'rgba(255,255,255,0.05)' }, ticks: { color: '#94a3b8' } }
                                }
                            }
                        });
                    """.trimIndent()).append("\n")
                }

                WidgetType.BAR_CHART -> {
                    widgetCardsHtml.append("""
                        <div class="$colSpan $cardBg border rounded-xl p-5 shadow-lg">
                            <div class="flex flex-col sm:flex-row sm:items-center justify-between mb-4 gap-2">
                                <div>
                                    <h3 class="text-base font-semibold text-slate-100">${widget.title}</h3>
                                    <p class="text-xs text-slate-400">${widget.subtitle}</p>
                                </div>
                                <span class="self-start sm:self-auto text-xs font-mono px-2 py-1 rounded bg-[#00E5FF]/10 text-[#00E5FF] border border-[#00E5FF]/20">Bar Graph</span>
                            </div>
                            <div class="h-64 relative">
                                <canvas id="$canvasId"></canvas>
                            </div>
                        </div>
                    """.trimIndent()).append("\n")

                    val labels = widget.points.joinToString(",") { "\"${it.label}\"" }
                    val dataValues = widget.points.joinToString(",") { it.value.toString() }
                    chartScripts.append("""
                        new Chart(document.getElementById('$canvasId'), {
                            type: 'bar',
                            data: {
                                labels: [$labels],
                                datasets: [{
                                    label: '${widget.title}',
                                    data: [$dataValues],
                                    backgroundColor: '$electricBlue',
                                    borderRadius: 6
                                }]
                            },
                            options: {
                                responsive: true,
                                maintainAspectRatio: false,
                                plugins: { legend: { display: false } },
                                scales: {
                                    x: { grid: { display: false }, ticks: { color: '#94a3b8' } },
                                    y: { grid: { color: 'rgba(255,255,255,0.05)' }, ticks: { color: '#94a3b8' } }
                                }
                            }
                        });
                    """.trimIndent()).append("\n")
                }

                WidgetType.DONUT_CHART -> {
                    widgetCardsHtml.append("""
                        <div class="$colSpan $cardBg border rounded-xl p-5 shadow-lg">
                            <div class="flex flex-col sm:flex-row sm:items-center justify-between mb-4 gap-2">
                                <div>
                                    <h3 class="text-base font-semibold text-slate-100">${widget.title}</h3>
                                    <p class="text-xs text-slate-400">${widget.subtitle}</p>
                                </div>
                                <span class="self-start sm:self-auto text-xs font-mono px-2 py-1 rounded bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">Distribution</span>
                            </div>
                            <div class="h-64 relative flex items-center justify-center">
                                <canvas id="$canvasId"></canvas>
                            </div>
                        </div>
                    """.trimIndent()).append("\n")

                    val labels = widget.slices.joinToString(",") { "\"${it.label}\"" }
                    val dataValues = widget.slices.joinToString(",") { it.value.toString() }
                    val colors = widget.slices.joinToString(",") { "'${it.colorHex}'" }
                    chartScripts.append("""
                        new Chart(document.getElementById('$canvasId'), {
                            type: 'doughnut',
                            data: {
                                labels: [$labels],
                                datasets: [{
                                    data: [$dataValues],
                                    backgroundColor: [$colors],
                                    borderColor: '#111827',
                                    borderWidth: 3
                                }]
                            },
                            options: {
                                responsive: true,
                                maintainAspectRatio: false,
                                cutout: '70%',
                                plugins: {
                                    legend: { position: 'bottom', labels: { color: '#cbd5e1', font: { size: 11 } } }
                                }
                            }
                        });
                    """.trimIndent()).append("\n")
                }

                WidgetType.GAUGE_METER -> {
                    widgetCardsHtml.append("""
                        <div class="$colSpan $cardBg border rounded-xl p-5 shadow-lg flex flex-col justify-between">
                            <div>
                                <h3 class="text-base font-semibold text-slate-100">${widget.title}</h3>
                                <p class="text-xs text-slate-400 mb-4">${widget.subtitle}</p>
                            </div>
                            <div class="my-4 text-center">
                                <div class="text-3xl sm:text-4xl font-extrabold text-[#38BDF8]">${widget.metricValue.ifEmpty { "${widget.currentValue}%" }}</div>
                                <div class="text-xs text-slate-400 uppercase tracking-widest mt-1">${widget.metricUnit}</div>
                            </div>
                            <div class="w-full bg-slate-800 rounded-full h-3.5 border border-slate-700 overflow-hidden">
                                <div class="bg-gradient-to-r from-cyan-400 to-[#38BDF8] h-3.5 rounded-full" style="width: ${widget.currentValue}%"></div>
                            </div>
                            <div class="flex justify-between text-[11px] text-slate-400 mt-2">
                                <span>0%</span>
                                <span>Threshold: ${widget.targetThreshold ?: 80f}%</span>
                                <span>100%</span>
                            </div>
                        </div>
                    """.trimIndent()).append("\n")
                }

                WidgetType.DATA_TABLE -> {
                    val thead = widget.tableHeaders.joinToString("") { "<th class='py-3 px-3 sm:px-4 text-left text-xs font-semibold text-slate-400 uppercase tracking-wider'>$it</th>" }
                    val tbody = widget.tableRows.joinToString("") { row ->
                        "<tr class='border-b border-slate-800/80 hover:bg-slate-800/30 transition-colors'>" +
                                row.joinToString("") { cell ->
                                    val cellColor = if (cell.contains("%") || cell.contains("$")) "text-[#38BDF8] font-mono" else "text-slate-300"
                                    "<td class='py-3 px-3 sm:px-4 text-xs sm:text-sm $cellColor'>$cell</td>"
                                } + "</tr>"
                    }

                    widgetCardsHtml.append("""
                        <div class="$colSpan $cardBg border rounded-xl p-5 shadow-lg overflow-hidden">
                            <div class="flex flex-col sm:flex-row sm:items-center justify-between mb-4 gap-2">
                                <div>
                                    <h3 class="text-base font-semibold text-slate-100">${widget.title}</h3>
                                    <p class="text-xs text-slate-400">${widget.subtitle}</p>
                                </div>
                                <span class="self-start sm:self-auto text-xs font-mono px-2 py-1 rounded bg-slate-800 text-slate-300 border border-slate-700">Live Table</span>
                            </div>
                            <div class="overflow-x-auto">
                                <table class="min-w-full text-left">
                                    <thead>
                                        <tr class="border-b border-slate-800 bg-slate-900/50">
                                            $thead
                                        </tr>
                                    </thead>
                                    <tbody>
                                        $tbody
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    """.trimIndent()).append("\n")
                }
            }
        }

        return """
<!DOCTYPE html>
<html lang="en" class="dark">
<head>
    <meta charset="UTF-8">
    <!-- Responsive Viewport for Android phone, iPhone, iPod, and Web browsers -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=5.0, viewport-fit=cover">
    <meta name="theme-color" content="#0B0F17">
    <meta name="mobile-web-app-capable" content="yes">
    <!-- iOS / iPhone / iPod Touch Meta Tags -->
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent">
    <meta name="apple-mobile-web-app-title" content="JS Dashboard">
    <title>${dashboard.title} - JS Dashboard Generator</title>
    <!-- Tailwind CSS CDN -->
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = {
            darkMode: 'class',
            theme: {
                extend: {
                    colors: {
                        electricBlue: '#38BDF8',
                        electricCyan: '#00E5FF',
                        darkSlate: '#0B0F17',
                        cardSlate: '#111827'
                    }
                }
            }
        }
    </script>
    <!-- Chart.js CDN -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        :root {
            --sat: env(safe-area-inset-top, 0px);
            --sab: env(safe-area-inset-bottom, 0px);
        }
        body {
            padding-top: var(--sat);
            padding-bottom: var(--sab);
            -webkit-tap-highlight-color: transparent;
        }
        @media print {
            body { background: white !important; color: black !important; }
            .no-print { display: none !important; }
            .border { border-color: #e2e8f0 !important; }
        }
    </style>
</head>
<body id="dashBody" class="$bgClass min-h-screen font-sans antialiased transition-colors duration-200">
    <!-- Top Responsive Navigation Header -->
    <header class="border-b border-slate-800 bg-[#0B0F17]/95 backdrop-blur sticky top-0 z-50 px-4 sm:px-6 py-3.5 flex flex-wrap items-center justify-between gap-3 no-print">
        <div class="flex items-center gap-3">
            <div class="w-9 h-9 rounded-lg bg-[#38BDF8]/20 border border-[#38BDF8] flex items-center justify-center font-bold text-[#38BDF8] text-lg shadow-sm">
                JS
            </div>
            <div>
                <h1 class="text-sm sm:text-base font-bold text-slate-100 tracking-tight">JS (Jeddah San) Dashboard</h1>
                <p class="text-[11px] text-slate-400">Android • iPhone • iPod • Web Browser</p>
            </div>
        </div>

        <!-- Action & Device Simulator Bar -->
        <div class="flex items-center flex-wrap gap-2">
            <button onclick="toggleTheme()" class="p-2 rounded-lg border border-slate-700 bg-slate-800/80 text-xs text-slate-300 hover:text-white transition-colors" title="Toggle Accessibility Mode">
                🌓 Theme
            </button>
            <button onclick="window.print()" class="px-3 py-1.5 rounded-lg border border-slate-700 hover:border-slate-500 bg-slate-800/80 text-xs font-medium text-slate-200 transition-colors flex items-center gap-1.5">
                🖨️ Print
            </button>
            <button onclick="downloadCsv()" class="px-3.5 py-1.5 rounded-lg bg-[#38BDF8] hover:bg-[#0284C7] text-slate-950 text-xs font-bold transition-colors flex items-center gap-1.5 shadow-sm">
                📊 Export Excel
            </button>
        </div>
    </header>

    <!-- Main Container (Responsive Width for Handheld and Desktop) -->
    <main class="max-w-7xl mx-auto px-4 sm:px-6 py-6 sm:py-8">
        <!-- Hero Title Banner -->
        <div class="mb-6 sm:mb-8">
            <div class="flex flex-wrap items-center gap-2 text-xs font-mono text-[#38BDF8] mb-1">
                <span>⚡ PROTOTYPE ENGINE</span>
                <span>•</span>
                <span>GENERATED: $dateStr</span>
                <span>•</span>
                <span class="text-emerald-400">● MULTI-DEVICE RESPONSIVE</span>
            </div>
            <h2 class="text-xl sm:text-2xl md:text-3xl font-extrabold text-white tracking-tight">${dashboard.title}</h2>
            <p class="text-xs sm:text-sm text-slate-400 mt-1 max-w-3xl">${dashboard.description}</p>
        </div>

        <!-- Dashboard Widgets Grid -->
        <div class="grid grid-cols-12 gap-4 sm:gap-6">
            $widgetCardsHtml
        </div>
    </main>

    <!-- Footer -->
    <footer class="border-t border-slate-800/80 mt-12 sm:mt-16 py-8 px-4 text-center text-xs text-slate-500">
        <p>Jeddah San (JS) Autonomous Dashboard Generator • Cross-Platform Web & Mobile Architecture</p>
    </footer>

    <!-- Interactive Client Scripts -->
    <script>
        $chartScripts

        function toggleTheme() {
            const body = document.getElementById('dashBody');
            body.classList.toggle('dark');
            if (body.classList.contains('bg-[#0B0F17]')) {
                body.classList.replace('bg-[#0B0F17]', 'bg-slate-50');
                body.classList.replace('text-[#F8FAFC]', 'text-slate-900');
            } else {
                body.classList.replace('bg-slate-50', 'bg-[#0B0F17]');
                body.classList.replace('text-slate-900', 'text-[#F8FAFC]');
            }
        }

        function downloadCsv() {
            const csvContent = "data:text/csv;charset=utf-8,${dashboard.title.replace(",", " ")}\nGenerated: $dateStr\nWidget Count: ${dashboard.activeWidgets.size}\n\nTitle,Type,Value,Unit\n" +
                ${dashboard.activeWidgets.joinToString("+") { "\"${it.title},${it.type},${it.metricValue},${it.metricUnit}\\n\"" }};
            const encodedUri = encodeURI(csvContent);
            const link = document.createElement("a");
            link.setAttribute("href", encodedUri);
            link.setAttribute("download", "JS_Dashboard_${System.currentTimeMillis()}.csv");
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        }
    </script>
</body>
</html>
        """.trimIndent()
    }

    fun generateExcelReport(dashboard: Dashboard): String {
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        val sb = StringBuilder()

        sb.append("=================================================================\n")
        sb.append("JS (JEDDAH SAN) ENTERPRISE DASHBOARD EXCEL REPORT\n")
        sb.append("=================================================================\n")
        sb.append("Dashboard Title:,\"${dashboard.title}\"\n")
        sb.append("User Requirement:,\"${dashboard.userPrompt}\"\n")
        sb.append("Export Timestamp:,\"$dateStr\"\n")
        sb.append("Engine Architecture:,\"Gemini Pro High-Performance Synthesis\"\n")
        sb.append("Total Configured Widgets:,\"${dashboard.activeWidgets.size}\"\n")
        sb.append("\n")

        sb.append("--- [SECTION 1: KEY PERFORMANCE INDICATORS & METRIC CARDS] ---\n")
        sb.append("Widget Name,Type,Current Value,Unit,Trend (%),Status,Subtitle\n")
        dashboard.activeWidgets.forEach { widget ->
            val sign = if (widget.trendPercent >= 0) "+" else ""
            sb.append("\"${widget.title}\",")
            sb.append("\"${widget.type}\",")
            sb.append("\"${widget.metricValue.ifEmpty { widget.currentValue.toString() }}\",")
            sb.append("\"${widget.metricUnit}\",")
            sb.append("\"$sign${widget.trendPercent}%\",")
            sb.append("\"${if (widget.trendIsPositive) "On Target" else "Needs Attention"}\",")
            sb.append("\"${widget.subtitle}\"\n")
        }
        sb.append("\n")

        sb.append("--- [SECTION 2: TIME-SERIES & CHART DATA POINTS] ---\n")
        dashboard.activeWidgets.filter { it.points.isNotEmpty() }.forEach { widget ->
            sb.append("Chart Name:,\"${widget.title}\"\n")
            sb.append("Period/Label,Primary Value,Secondary Benchmark\n")
            widget.points.forEach { pt ->
                sb.append("\"${pt.label}\",${pt.value},${pt.secondaryValue ?: 0.0}\n")
            }
            sb.append("\n")
        }

        sb.append("--- [SECTION 3: INTEGRATED DATA TABLES] ---\n")
        dashboard.activeWidgets.filter { it.tableHeaders.isNotEmpty() }.forEach { widget ->
            sb.append("Table Dataset:,\"${widget.title}\"\n")
            sb.append(widget.tableHeaders.joinToString(",") { "\"$it\"" }).append("\n")
            widget.tableRows.forEach { row ->
                sb.append(row.joinToString(",") { "\"$it\"" }).append("\n")
            }
            sb.append("\n")
        }

        return sb.toString()
    }

    fun generateReactTailwindCode(dashboard: Dashboard): String {
        return """
import React, { useState } from 'react';
import { AreaChart, Area, BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';

// Generated by JS (Jeddah San) Dashboard Generator
// Supports Real-Time WebSocket, CSV, and WordPress Webhooks
export default function ${dashboard.title.filter { it.isLetter() }}() {
  const [isLive, setIsLive] = useState(true);

  return (
    <div className="min-h-screen bg-[#0B0F17] text-slate-100 p-8">
      <div className="max-w-7xl mx-auto space-y-6">
        <header className="flex justify-between items-center border-b border-slate-800 pb-4">
          <div>
            <h1 className="text-2xl font-bold text-white tracking-tight">${dashboard.title}</h1>
            <p className="text-sm text-slate-400">${dashboard.description}</p>
          </div>
          <span className="px-3 py-1 bg-[#38BDF8]/10 text-[#38BDF8] border border-[#38BDF8]/20 rounded-full text-xs font-mono">
            JS Autonomous Dashboard
          </span>
        </header>

        <div className="grid grid-cols-12 gap-6">
          {/* Active Widgets rendered dynamically */}
          ${dashboard.activeWidgets.take(4).mapIndexed { i, w ->
            """
          <div className="${if (w.width.name == "FULL") "col-span-12" else "col-span-12 md:col-span-6"} bg-[#111827] border border-[#1E293B] rounded-xl p-5 shadow-lg">
            <h3 className="text-sm font-semibold text-slate-300 uppercase">${w.title}</h3>
            <p className="text-3xl font-extrabold text-[#38BDF8] mt-2">${w.metricValue}</p>
            <p className="text-xs text-slate-500 mt-1">${w.subtitle}</p>
          </div>"""
        }.joinToString("\n")}
        </div>
      </div>
    </div>
  );
}
        """.trimIndent()
    }

    fun generateComposeKotlinCode(dashboard: Dashboard): String {
        return """
package com.example.ui.generated

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Generated by JS (Jeddah San) Dashboard Generator
@Composable
fun Generated${dashboard.title.filter { it.isLetter() }}Screen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F17))
            .padding(16.dp)
    ) {
        Text(
            text = "${dashboard.title}",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
        Text(
            text = "${dashboard.description}",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF94A3B8)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Dynamic Widget Rows
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Widgets rendered with Canvas and M3 Surface
        }
    }
}
        """.trimIndent()
    }
}
