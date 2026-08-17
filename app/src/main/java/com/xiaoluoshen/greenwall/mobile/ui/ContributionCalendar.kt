package com.xiaoluoshen.greenwall.mobile.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoluoshen.greenwall.mobile.domain.ContributionDay
import com.xiaoluoshen.greenwall.mobile.domain.ContributionDomain
import com.xiaoluoshen.greenwall.mobile.domain.ContributionMap
import java.time.Month
import java.time.format.TextStyle as DateTextStyle
import java.util.Locale
import kotlin.math.ceil
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

private val cellSize = 18.dp
private val cellGap = 3.dp
private val labelWidth = 26.dp
private val monthLabelHeight = 24.dp

@Composable
fun ContributionCalendar(
    year: Int,
    contributions: ContributionMap,
    selectedValue: Int,
    isEditMode: Boolean,
    onCellsApplied: (Map<String, Int>) -> Unit,
) {
    val days = remember(year) { ContributionDomain.getYearDays(year) }
    val maxWeek = remember(days) { days.maxOf { it.week } }
    val cellsByPosition = remember(days) { days.associateBy { it.week to it.weekday } }
    val months = remember(days, year) {
        days.filter { it.date.year == year }
            .groupBy { it.date.month }
            .mapValues { (_, monthDays) -> monthDays.minOf { it.week } }
    }
    val scrollState = rememberScrollState()

    Column {
        Row {
            Column(modifier = Modifier.width(labelWidth)) {
                Spacer(modifier = Modifier.height(monthLabelHeight))
                WeekdayLabels()
            }
            Column(
                modifier = Modifier.horizontalScroll(
                    state = scrollState,
                    enabled = !isEditMode,
                ),
            ) {
                MonthLabels(months, maxWeek)
                HeatmapGrid(
                    year = year,
                    maxWeek = maxWeek,
                    cellsByPosition = cellsByPosition,
                    contributions = contributions,
                    selectedValue = selectedValue,
                    emptyColor = MiuixTheme.colorScheme.surfaceVariant,
                    isEditMode = isEditMode,
                    onCellsApplied = onCellsApplied,
                )
            }
        }
        CalendarLegend(modifier = Modifier.padding(start = labelWidth))
    }
}

@Composable
private fun MonthLabels(months: Map<Month, Int>, maxWeek: Int) {
    val cellStep = cellSize + cellGap
    Box(
        modifier = Modifier
            .width(cellStep * (maxWeek + 1))
            .height(monthLabelHeight),
    ) {
        months.forEach { (month, week) ->
            Text(
                text = month.getDisplayName(DateTextStyle.SHORT, Locale.CHINESE),
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = cellStep * week),
            )
        }
    }
}

@Composable
private fun WeekdayLabels() {
    val labels = listOf("", "一", "", "三", "", "五", "")
    Column {
        labels.forEach { label ->
            Box(modifier = Modifier.height(cellSize + cellGap)) {
                Text(
                    text = label,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    fontSize = 10.sp,
                )
            }
        }
    }
}

@Composable
private fun HeatmapGrid(
    year: Int,
    maxWeek: Int,
    cellsByPosition: Map<Pair<Int, Int>, ContributionDay>,
    contributions: ContributionMap,
    selectedValue: Int,
    emptyColor: Color,
    isEditMode: Boolean,
    onCellsApplied: (Map<String, Int>) -> Unit,
) {
    val density = LocalDensity.current
    val cellStep = cellSize + cellGap
    val cellStepPx = with(density) { cellStep.toPx() }
    val cellSizePx = with(density) { cellSize.toPx() }
    val cornerRadiusPx = with(density) { 4.dp.toPx() }
    val strokeUpdates = remember(year, selectedValue) { mutableStateMapOf<String, Int>() }
    val cellColors = cellsByPosition.mapValues { (_, day) ->
        val date = ContributionDomain.formatDate(day.date)
        val value = strokeUpdates[date] ?: contributions[date] ?: 0
        contributionColor(value, emptyColor)
    }

    val gestureModifier = if (isEditMode) {
        Modifier.pointerInput(year, selectedValue, cellsByPosition) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                val stroke = linkedMapOf<String, Int>()
                var previousPosition = down.position

                fun recordCell(offset: Offset) {
                    dateAt(
                        offset = offset,
                        cellStepPx = cellStepPx,
                        cellSizePx = cellSizePx,
                        maxWeek = maxWeek,
                        cellsByPosition = cellsByPosition,
                        year = year,
                    )?.let { date ->
                        stroke[date] = selectedValue
                        strokeUpdates[date] = selectedValue
                    }
                }

                fun recordPath(from: Offset, to: Offset) {
                    val distance = (to - from).getDistance()
                    val stepCount = ceil(distance / (cellStepPx / 2f)).toInt().coerceAtLeast(1)
                    repeat(stepCount) { index ->
                        val fraction = (index + 1).toFloat() / stepCount
                        recordCell(from + (to - from) * fraction)
                    }
                }

                recordCell(down.position)

                while (true) {
                    val event = awaitPointerEvent()
                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                    if (change.pressed) {
                        recordPath(previousPosition, change.position)
                        previousPosition = change.position
                        change.consume()
                    }
                    if (change.changedToUp()) {
                        if (stroke.isNotEmpty()) {
                            onCellsApplied(stroke.toMap())
                        }
                        strokeUpdates.clear()
                        break
                    }
                }
            }
        }
    } else {
        Modifier
    }

    Canvas(
        modifier = Modifier
            .width(cellStep * (maxWeek + 1))
            .height(cellStep * 7)
            .clipToBounds()
            .semantics {
                contentDescription = if (isEditMode) {
                    "实时编辑模式。手指按下立即显示，拖动时持续绘制，抬手后保存整条笔触"
                } else {
                    "浏览模式。左右滑动可浏览全年日期"
                }
            }
            .then(gestureModifier),
    ) {
        for (week in 0..maxWeek) {
            for (weekday in 0..6) {
                val color = cellColors[week to weekday] ?: Color.Transparent
                drawRoundRect(
                    color = color,
                    topLeft = Offset(week * cellStepPx, weekday * cellStepPx),
                    size = androidx.compose.ui.geometry.Size(cellSizePx, cellSizePx),
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                )
            }
        }
    }
}

private fun dateAt(
    offset: Offset,
    cellStepPx: Float,
    cellSizePx: Float,
    maxWeek: Int,
    cellsByPosition: Map<Pair<Int, Int>, ContributionDay>,
    year: Int,
): String? {
    if (offset.x < 0f || offset.y < 0f) return null

    val week = (offset.x / cellStepPx).toInt()
    val weekday = (offset.y / cellStepPx).toInt()
    if (week !in 0..maxWeek || weekday !in 0..6) return null

    val localX = offset.x - week * cellStepPx
    val localY = offset.y - weekday * cellStepPx
    if (localX > cellSizePx || localY > cellSizePx) return null

    val day = cellsByPosition[week to weekday] ?: return null
    return if (day.date.year == year) ContributionDomain.formatDate(day.date) else null
}

@Composable
private fun CalendarLegend(modifier: Modifier = Modifier) {
    val emptyColor = MiuixTheme.colorScheme.surfaceVariant

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier.padding(top = 12.dp),
    ) {
        Text("少", color = MiuixTheme.colorScheme.onSurfaceVariantSummary, fontSize = 12.sp)
        listOf(0, 1, 3, 6, 9).forEach { value ->
            Canvas(Modifier.width(cellSize).height(cellSize)) {
                drawRoundRect(
                    color = contributionColor(value, emptyColor),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                )
            }
        }
        Text("多", color = MiuixTheme.colorScheme.onSurfaceVariantSummary, fontSize = 12.sp)
    }
}

private fun contributionColor(value: Int, emptyColor: Color): Color = when {
    value <= 0 -> emptyColor
    value <= 2 -> Color(0xFF9BE9A8)
    value <= 5 -> Color(0xFF40C463)
    value <= 8 -> Color(0xFF30A14E)
    else -> Color(0xFF216E39)
}
