package com.xiaoluoshen.greenwall.mobile.domain

import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

enum class ContributionLevel(val value: Int) {
    None(0),
    Low(1),
    Medium(3),
    High(6),
    Maximum(9);

    companion object {
        fun fromValue(value: Int): ContributionLevel? = entries.firstOrNull { it.value == value }
    }
}

data class ContributionDay(
    val date: LocalDate,
    val weekday: Int,
    val week: Int,
)

typealias ContributionMap = Map<String, Int>

object ContributionDomain {
    fun getYearDays(year: Int): List<ContributionDay> {
        val firstDay = LocalDate.of(year, 1, 1)
        val lastDay = LocalDate.of(year, 12, 31)
        var currentDate = firstDay.minusDays((firstDay.dayOfWeek.value % 7).toLong())
        var week = 0
        val days = mutableListOf<ContributionDay>()

        while (!currentDate.isAfter(lastDay)) {
            if (currentDate.dayOfWeek.value % 7 == 0 && days.isNotEmpty()) week += 1
            days += ContributionDay(
                date = currentDate,
                weekday = currentDate.dayOfWeek.value % 7,
                week = week,
            )
            currentDate = currentDate.plusDays(1)
        }

        return days
    }

    fun formatDate(date: LocalDate): String = date.format(dateFormatter)

    fun parseDate(value: String): LocalDate? = runCatching {
        LocalDate.parse(value, dateFormatter)
    }.getOrNull()

    fun isDateInYear(value: String, year: Int): Boolean = parseDate(value)?.year == year

    fun sanitize(contributions: Map<String, Int>, year: Int): ContributionMap =
        contributions.filter { (date, count) ->
            isDateInYear(date, year) && ContributionLevel.fromValue(count) != null && count > 0
        }

    fun applyUpdates(
        contributions: ContributionMap,
        updates: Map<String, Int>,
        year: Int,
    ): ContributionMap {
        val result = contributions.toMutableMap()
        updates.forEach { (date, count) ->
            if (!isDateInYear(date, year) || ContributionLevel.fromValue(count) == null) return@forEach
            if (count == ContributionLevel.None.value) result.remove(date) else result[date] = count
        }
        return result
    }

    fun createAllGreen(
        days: List<ContributionDay>,
        year: Int,
        level: ContributionLevel,
        today: LocalDate = LocalDate.now(),
    ): ContributionMap = days
        .asSequence()
        .filter { it.date.year == year && !it.date.isAfter(today) }
        .associate { formatDate(it.date) to level.value }

    fun total(contributions: ContributionMap): Int = contributions.values.sum()
}
