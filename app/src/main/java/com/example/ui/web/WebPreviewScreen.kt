package com.example.ui.web

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.export.DashboardExporter
import com.example.export.FileExportHelper
import com.example.ui.theme.DarkGrayCard
import com.example.ui.theme.ElectricBabyBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MetricEmerald
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.DashboardViewModel

enum class DevicePreviewType(val label: String, val badge: String, val widthDp: Int?) {
    RESPONSIVE_BROWSER("Desktop Web", "💻 Chrome/Safari", null),
    ANDROID_PHONE("Android Phone", "🤖 Pixel/Galaxy", 360),
    IPHONE("iPhone", "🍏 iOS 18", 380),
    IPOD_TABLET("iPod / iPad", "📱 iOS Touch", 520)
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebPreviewScreen(
    viewModel: DashboardViewModel,
    onNavigate: (AppScreen) -> Unit
) {
    val context = LocalContext.current
    val dashboard by viewModel.dashboard.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    var selectedDevice by remember { mutableStateOf(DevicePreviewType.RESPONSIVE_BROWSER) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var reloadTrigger by remember { mutableStateOf(0) }

    val rawHtml = remember(dashboard, isDarkMode, reloadTrigger) {
        DashboardExporter.generateStandaloneHtml(dashboard, isDarkMode)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Multi-Device Web Sandbox",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Live preview for Android phone, iPhone, iPod touch & desktop web",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Refresh
                IconButton(
                    onClick = { reloadTrigger++ },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reload Web Sandbox",
                        tint = ElectricBabyBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Copy HTML
                IconButton(
                    onClick = {
                        FileExportHelper.copyToClipboard(context, "Dashboard HTML", rawHtml)
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Copy Webpage HTML",
                        tint = ElectricCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Share / Download HTML
                IconButton(
                    onClick = {
                        FileExportHelper.shareOrDownloadFile(
                            context,
                            "JS_Web_Dashboard_${System.currentTimeMillis()}.html",
                            rawHtml,
                            "text/html"
                        )
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share HTML File",
                        tint = MetricEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Device Selector Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedDevice.ordinal,
            edgePadding = 0.dp,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = ElectricBabyBlue,
            divider = {}
        ) {
            DevicePreviewType.values().forEach { device ->
                val isSelected = selectedDevice == device
                Tab(
                    selected = isSelected,
                    onClick = { selectedDevice = device },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = device.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) ElectricBabyBlue else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Viewport Dimensions Banner
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(6.dp),
            border = androidx.compose.foundation.BorderStroke(0.6.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TARGET: ${selectedDevice.badge}",
                    fontSize = 9.5.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = ElectricBabyBlue
                )
                Text(
                    text = if (selectedDevice.widthDp != null) "${selectedDevice.widthDp}dp Viewport" else "100% Fluid Width",
                    fontSize = 9.5.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Device Frame Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF07090E), RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                .padding(if (selectedDevice.widthDp != null) 8.dp else 2.dp),
            contentAlignment = Alignment.Center
        ) {
            val deviceModifier = if (selectedDevice.widthDp != null) {
                Modifier
                    .width(selectedDevice.widthDp!!.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(18.dp))
                    .border(2.dp, ElectricBabyBlue.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
            } else {
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
            }

            Box(modifier = deviceModifier) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.loadWithOverviewMode = true
                            settings.useWideViewPort = true
                            settings.builtInZoomControls = true
                            settings.displayZoomControls = false
                            webViewClient = WebViewClient()
                            loadDataWithBaseURL("https://jeddahsan.internal/", rawHtml, "text/html", "UTF-8", null)
                            webViewInstance = this
                        }
                    },
                    update = { view ->
                        view.loadDataWithBaseURL("https://jeddahsan.internal/", rawHtml, "text/html", "UTF-8", null)
                    },
                    modifier = Modifier.fillMaxSize().testTag("live_web_sandbox_view")
                )
            }
        }
    }
}
