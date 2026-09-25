package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChartPoint
import com.example.data.model.SliceItem
import com.example.ui.theme.DarkGrayBorder
import com.example.ui.theme.DarkGrayCard
import com.example.ui.theme.ElectricBabyBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MetricEmerald
import com.example.ui.theme.MetricRose

@Composable
fun InteractiveAreaChart(
    points: List<ChartPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = ElectricBabyBlue,
    fillGradient: List<Color> = listOf(ElectricBabyBlue.copy(alpha = 0.35f), Color.Transparent)
) {
    if (points.isEmpty()) return

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var animationPlayed by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "area_anim"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    val maxVal = (points.maxOfOrNull { it.value } ?: 100f).coerceAtLeast(10f) * 1.15f
    val minVal = 0f

    Column(modifier = modifier) {
        // Selection readout if touched
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            selectedIndex?.let { idx ->
                val pt = points.getOrNull(idx)
                if (pt != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .background(DarkGrayCard, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Box(modifier = Modifier.size(6.dp).background(ElectricBabyBlue, RoundedCornerShape(3.dp)))
                        Text(
                            text = "${pt.label}: ${pt.value.toInt()}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = ElectricBabyBlue
                        )
                    }
                }
            } ?: run {
                Text(
                    text = "Tap any point on the chart to inspect coordinates",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(points) {
                        detectTapGestures { offset ->
                            val width = size.width
                            val step = width / (points.size - 1).coerceAtLeast(1)
                            val idx = (offset.x / step).toInt().coerceIn(0, points.size - 1)
                            selectedIndex = idx
                        }
                    }
            ) {
                val width = size.width
                val height = size.height
                val bottomPadding = 20.dp.toPx()
                val usableHeight = height - bottomPadding

                // Horizontal grid lines
                val gridLines = 3
                for (i in 0..gridLines) {
                    val y = usableHeight * (i.toFloat() / gridLines)
                    drawLine(
                        color = Color.White.copy(alpha = 0.06f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                if (points.size < 2) return@Canvas

                val stepX = width / (points.size - 1)

                val linePath = Path()
                val fillPath = Path()

                points.forEachIndexed { i, pt ->
                    val x = i * stepX
                    val normY = (pt.value - minVal) / (maxVal - minVal)
                    val y = usableHeight - (normY * usableHeight * progress)

                    if (i == 0) {
                        linePath.moveTo(x, y)
                        fillPath.moveTo(x, usableHeight)
                        fillPath.lineTo(x, y)
                    } else {
                        val prevX = (i - 1) * stepX
                        val prevNormY = (points[i - 1].value - minVal) / (maxVal - minVal)
                        val prevY = usableHeight - (prevNormY * usableHeight * progress)

                        val cx = (prevX + x) / 2f
                        linePath.cubicTo(cx, prevY, cx, y, x, y)
                        fillPath.cubicTo(cx, prevY, cx, y, x, y)
                    }
                }

                fillPath.lineTo(width, usableHeight)
                fillPath.close()

                // Draw gradient fill
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = fillGradient,
                        startY = 0f,
                        endY = usableHeight
                    )
                )

                // Draw curve line
                drawPath(
                    path = linePath,
                    color = lineColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw glow nodes
                points.forEachIndexed { i, pt ->
                    val x = i * stepX
                    val normY = (pt.value - minVal) / (maxVal - minVal)
                    val y = usableHeight - (normY * usableHeight * progress)

                    val isSelected = selectedIndex == i

                    // Outer halo
                    drawCircle(
                        color = if (isSelected) ElectricCyan else lineColor.copy(alpha = 0.35f),
                        radius = if (isSelected) 8.dp.toPx() else 4.dp.toPx(),
                        center = Offset(x, y)
                    )
                    // Inner node
                    drawCircle(
                        color = if (isSelected) Color.White else lineColor,
                        radius = if (isSelected) 4.5.dp.toPx() else 2.5.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }
        }

        // X-Axis Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            points.forEachIndexed { i, pt ->
                Text(
                    text = pt.label,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = if (selectedIndex == i) ElectricBabyBlue else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontWeight = if (selectedIndex == i) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun InteractiveBarChart(
    points: List<ChartPoint>,
    modifier: Modifier = Modifier,
    barColor: Color = ElectricBabyBlue
) {
    if (points.isEmpty()) return

    var hoveredIndex by remember { mutableStateOf<Int?>(null) }
    var animPlayed by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (animPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 700),
        label = "bar_anim"
    )

    LaunchedEffect(Unit) {
        animPlayed = true
    }

    val maxVal = (points.maxOfOrNull { it.value } ?: 100f).coerceAtLeast(10f) * 1.2f

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(points) {
                        detectTapGestures { offset ->
                            val count = points.size
                            val barSlotWidth = size.width / count
                            val idx = (offset.x / barSlotWidth).toInt().coerceIn(0, count - 1)
                            hoveredIndex = idx
                        }
                    }
            ) {
                val width = size.width
                val height = size.height
                val slotWidth = width / points.size
                val barWidth = slotWidth * 0.55f

                // Subtle grid
                drawLine(
                    color = Color.White.copy(alpha = 0.08f),
                    start = Offset(0f, height),
                    end = Offset(width, height),
                    strokeWidth = 1.dp.toPx()
                )

                points.forEachIndexed { i, pt ->
                    val x = i * slotWidth + (slotWidth - barWidth) / 2f
                    val barHeight = (pt.value / maxVal) * height * progress
                    val y = height - barHeight

                    val isHovered = hoveredIndex == i

                    val brush = Brush.verticalGradient(
                        colors = listOf(
                            if (isHovered) ElectricCyan else barColor,
                            if (isHovered) ElectricBabyBlue else barColor.copy(alpha = 0.45f)
                        ),
                        startY = y,
                        endY = height
                    )

                    drawRoundRect(
                        brush = brush,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )

                    if (isHovered) {
                        drawRoundRect(
                            color = Color.White.copy(alpha = 0.8f),
                            topLeft = Offset(x, y),
                            size = Size(barWidth, 3.dp.toPx()),
                            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                        )
                    }
                }
            }
        }

        // X labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            points.forEachIndexed { idx, pt ->
                Text(
                    text = pt.label,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = if (hoveredIndex == idx) ElectricCyan else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontWeight = if (hoveredIndex == idx) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun InteractiveDonutChart(
    slices: List<SliceItem>,
    modifier: Modifier = Modifier
) {
    if (slices.isEmpty()) return

    val total = slices.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f)

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 24.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2f
                val center = Offset(size.width / 2f, size.height / 2f)

                var startAngle = -90f

                slices.forEach { slice ->
                    val sweepAngle = (slice.value / total) * 360f
                    val color = try {
                        Color(android.graphics.Color.parseColor(slice.colorHex))
                    } catch (e: Exception) {
                        ElectricBabyBlue
                    }

                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle - 2f,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2f, radius * 2f),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    startAngle += sweepAngle
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "100%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Total",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Legend chips
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            slices.forEach { slice ->
                val sliceColor = try {
                    Color(android.graphics.Color.parseColor(slice.colorHex))
                } catch (e: Exception) {
                    ElectricBabyBlue
                }
                val pct = ((slice.value / total) * 100).toInt()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(sliceColor, RoundedCornerShape(2.dp))
                        )
                        Text(
                            text = slice.label,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "$pct%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = ElectricBabyBlue
                    )
                }
            }
        }
    }
}

@Composable
fun InteractiveGaugeMeter(
    currentValue: Float,
    targetThreshold: Float?,
    metricValue: String,
    metricUnit: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = metricValue.ifEmpty { "${currentValue.toInt()}%" },
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = ElectricBabyBlue,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = metricUnit.uppercase(),
            fontSize = 10.sp,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Progress bar with threshold indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(DarkGrayBorder, RoundedCornerShape(5.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = (currentValue / 100f).coerceIn(0f, 1f))
                    .background(
                        Brush.horizontalGradient(listOf(ElectricCyan, ElectricBabyBlue)),
                        RoundedCornerShape(5.dp)
                    )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "0%", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            targetThreshold?.let {
                Text(
                    text = "Threshold: ${it.toInt()}%",
                    fontSize = 9.sp,
                    color = MetricEmerald,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(text = "100%", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun MetricKpiCard(
    title: String,
    value: String,
    unit: String,
    trendPercent: Double,
    isPositive: Boolean,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val trendColor = if (isPositive) MetricEmerald else MetricRose
                val sign = if (trendPercent >= 0) "+" else ""
                Surface(
                    color = trendColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "$sign${trendPercent}%",
                        color = trendColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricBabyBlue,
                    fontFamily = FontFamily.Monospace
                )
                if (unit.isNotBlank()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = unit,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            }

            if (subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}
