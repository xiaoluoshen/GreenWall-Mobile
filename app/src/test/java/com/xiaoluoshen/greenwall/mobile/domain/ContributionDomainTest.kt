package com.xiaoluoshen.greenwall.mobile.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ContributionDomainTest {
    @Test
    fun `year grid includes every day in a leap year`() {
        val days = ContributionDomain.getYearDays(2024)
        assertEquals(366, days.count { it.date.year == 2024 })
    }

    @Test
    fun `date validation rejects invalid and cross year dates`() {
        assertTrue(ContributionDomain.isDateInYear("2024-02-29", 2024))
        assertFalse(ContributionDomain.isDateInYear("2025-02-29", 2025))
        assertFalse(ContributionDomain.isDateInYear("2025-12-31", 2026))
    }

    @Test
    fun `sanitize keeps valid nonzero contribution levels in selected year`() {
        val result = ContributionDomain.sanitize(
            mapOf(
                "2026-01-01" to 1,
                "2026-01-02" to 9,
                "2025-12-31" to 6,
                "2026-01-03" to 2,
                "2026-01-04" to 0,
            ),
            2026,
        )

        assertEquals(mapOf("2026-01-01" to 1, "2026-01-02" to 9), result)
    }

    @Test
    fun `batch updates apply valid cells and ignore invalid inputs`() {
        val result = ContributionDomain.applyUpdates(
            contributions = mapOf("2026-01-01" to 3),
            updates = mapOf(
                "2026-01-01" to 0,
                "2026-01-02" to 6,
                "2025-12-31" to 9,
                "2026-01-03" to 2,
            ),
            year = 2026,
        )

        assertEquals(mapOf("2026-01-02" to 6), result)
    }

    @Test
    fun `all green only includes selected year dates through today`() {
        val days = ContributionDomain.getYearDays(2026)
        val result = ContributionDomain.createAllGreen(
            days = days,
            year = 2026,
            level = ContributionLevel.High,
            today = LocalDate.of(2026, 1, 3),
        )

        assertEquals(
            mapOf(
                "2026-01-01" to 6,
                "2026-01-02" to 6,
                "2026-01-03" to 6,
            ),
            result,
        )
    }
}
