package com.xiaoluoshen.greenwall.mobile.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.xiaoluoshen.greenwall.mobile.domain.ContributionMap
import com.xiaoluoshen.greenwall.mobile.domain.ContributionDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject

private val Context.contributionDataStore by preferencesDataStore(name = "greenwall_contributions")

class ContributionRepository(private val context: Context) {
    fun observeYear(year: Int): Flow<ContributionMap> {
        val key = contributionKey(year)
        return context.contributionDataStore.data.map { preferences ->
            decode(preferences[key]).let { ContributionDomain.sanitize(it, year) }
        }
    }

    suspend fun save(year: Int, contributions: ContributionMap) {
        val key = contributionKey(year)
        val sanitized = ContributionDomain.sanitize(contributions, year)
        context.contributionDataStore.edit { preferences ->
            preferences[key] = encode(sanitized)
        }
    }

    private fun contributionKey(year: Int) = stringPreferencesKey("contributions_$year")

    private fun decode(serialized: String?): Map<String, Int> {
        if (serialized.isNullOrBlank()) return emptyMap()
        return runCatching {
            val json = JSONObject(serialized)
            buildMap {
                json.keys().forEach { date ->
                    val count = json.optInt(date, Int.MIN_VALUE)
                    if (count != Int.MIN_VALUE) put(date, count)
                }
            }
        }.getOrDefault(emptyMap())
    }

    private fun encode(contributions: ContributionMap): String = JSONObject(contributions).toString()
}
