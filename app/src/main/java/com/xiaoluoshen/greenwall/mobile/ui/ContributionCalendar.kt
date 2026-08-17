package com.xiaoluoshen.greenwall.mobile.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity
import com.xiaoluoshen.greenwall.mobile.domain.ContributionDay
import com.xiaoluoshen.greenwall.mobile.domain.ContributionDomain
import com.xiaoluoshen.greenwall.mobile.domain.ContributionMap
import java.time.Month

private val cellSize = 14.dp
private val cellGap = 3.dp
private val labelWidth = 28.dp

@Composable
fun ContributionCalendar(
    year: Int,
    contributions: ContributionMap,
    onCellsApplied: (Map<String, Int>) -> Unit,
    selectedValue: Int,
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
    val colorScheme = MaterialTheme.colorScheme

    Column(Modifier.horizontalScroll(scrollState)) {
        MonthLabels(months, maxWeek)
        Row {
            WeekdayLabels()
            HeatmapGrid(
                year = year,
                maxWeek = maxWeek,
                cellsByPosition = cellsByPosition,
                contributions = contributions,
                selectedValue = selectedValue,
                onCellsApplied = onCellsApplied,
                emptyColor = colorScheme.surfaceVariant,
            )
        }
        CalendarLegend()
    }
}

@Composable
private fun MonthLabels(months: Map<Month, Int>, maxWeek: Int) {
    val cellStep = cellSize + cellGap
    Box(
        modifier = Modifier
            .padding(start = labelWidth)
            .width(cellStep * (maxWeek + 1))
            .height(24.dp),
    ) {
        months.forEach { (month, week) ->
            Text(
                text = month.name.take(3).lowercase().replaceFirstChar { it.uppercase() },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = cellStep * week),
            )
        }
    }
}

@Composable
private fun WeekdayLabels() {
    val labels = listOf("", "Mon", "", "Wed", "", "Fri", "")
    Column(Modifier.width(labelWidth)) {
        labels.forEach { label ->
            Box(
                modifier = Modifier.height(cellSize + cellGap),
            ) {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
    onCellsApplied: (Map<String, Int>) -> Unit,
) {
    val density = LocalDensity.current
    val cellStep = cellSize + cellGap
    val cellStepPx = with(density) { cellStep.toPx() }
    val cellSizePx = with(density) { cellSize.toPx() }
    val cornerRadiusPx = with(density) { 3.dp.toPx() }
    val cellColors = remember(contributions, emptyColor) {
        buildMap {
            cellsByPosition.forEach { (position, day) ->
                put(position, contributionColor(contributions[ContributionDomain.formatDate(day.date)] ?: 0, emptyColor))
            }
        }
    }

    Canvas(
        modifier = Modifier
            .width(cellStep * (maxWeek + 1))
            .height(cellStep * 7)
            .clipToBounds()
            .pointerInput(year, selectedValue, cellsByPosition) {
                val touchedCells = linkedMapOf<String, Int>()

                fun recordTouch(offset: Offset) {
                    val week = (offset.x / cellStepPx).toInt()
                    val weekday = (offset.y / cellStepPx).toInt()
                    val day = cellsByPosition[week to weekday] ?: return
                    if (day.date.year != year) return
                    touchedCells[ContributionDomain.formatDate(day.date)] = selectedValue
                }

                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        event.changes.forEach { change ->
                            if (change.pressed) recordTouch(change.position)
                            if (change.changedToUp() && touchedCells.isNotEmpty()) {
                                onCellsApplied(touchedCells.toMap())
                                touchedCells.clear()
                            }
                        }
                    }
                }
            },
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

@Composable
private fun CalendarLegend() {
    val emptyColor = MaterialTheme.colorScheme.surfaceVariant

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(top = 12.dp),
    ) {
        Text("Less", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        listOf(0, 1, 3, 6, 9).forEach { value ->
            Canvas(Modifier.width(cellSize).height(cellSize)) {
                drawRoundRect(
                    color = contributionColor(value, emptyColor),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
                )
            }
        }
        Text("More", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
    }
}

private fun contributionColor(value: Int, emptyColor: Color): Color = when {
    value <= 0 -> emptyColor
    value <= 2 -> Color(0xFF9BE9A8)
    value <= 5 -> Color(0xFF40C463)
    value <= 8 -> Color(0xFF30A14E)
    else -> Color(0xFF216E39)
}
