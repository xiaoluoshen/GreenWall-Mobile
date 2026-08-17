package com.xiaoluoshen.greenwall.mobile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xiaoluoshen.greenwall.mobile.data.ContributionRepository
import com.xiaoluoshen.greenwall.mobile.domain.ContributionDomain
import com.xiaoluoshen.greenwall.mobile.domain.ContributionLevel
import com.xiaoluoshen.greenwall.mobile.domain.ContributionMap
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Year

private const val MAX_HISTORY_ENTRIES = 100

data class CanvasUiState(
    val year: Int = Year.now().value,
    val contributions: ContributionMap = emptyMap(),
    val selectedLevel: ContributionLevel = ContributionLevel.Maximum,
    val isEraserActive: Boolean = false,
    val isLoading: Boolean = true,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
)

class CanvasViewModel(
    private val repository: ContributionRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(CanvasUiState())
    val state: StateFlow<CanvasUiState> = _state.asStateFlow()

    private var snapshots: List<ContributionMap> = listOf(emptyMap())
    private var historyIndex = 0
    private var observeJob: Job? = null
    private var isApplyingLocalChange = false

    init {
        observeYear(_state.value.year)
    }

    fun selectYear(year: Int) {
        if (year == _state.value.year) return
        observeYear(year)
    }

    fun selectLevel(level: ContributionLevel) {
        if (level == ContributionLevel.None) return
        _state.value = _state.value.copy(selectedLevel = level, isEraserActive = false)
    }

    fun setEraserActive(isActive: Boolean) {
        _state.value = _state.value.copy(isEraserActive = isActive)
    }

    fun applyCell(date: String) {
        val value = if (_state.value.isEraserActive) 0 else _state.value.selectedLevel.value
        applyUpdates(mapOf(date to value))
    }

    fun applyUpdates(updates: Map<String, Int>) {
        val currentState = _state.value
        val nextContributions = ContributionDomain.applyUpdates(
            currentState.contributions,
            updates,
            currentState.year,
        )
        if (nextContributions == currentState.contributions) return

        commit(nextContributions)
    }

    fun applyCharacterPattern(pattern: Array<IntArray>) {
        if (pattern.isEmpty()) return

        val currentState = _state.value
        val days = ContributionDomain.getYearDays(currentState.year)
        val maxWeek = days.maxOf { it.week }
        val width = pattern.maxOf { it.size }
        val startWeek = ((maxWeek + 1 - width) / 2).coerceAtLeast(0)
        val daysByPosition = days.associateBy { it.week to it.weekday }
        val updates = buildMap {
            pattern.forEachIndexed { weekday, row ->
                repeat(width) { column ->
                    val day = daysByPosition[startWeek + column to weekday] ?: return@repeat
                    if (day.date.year != currentState.year) return@repeat
                    put(
                        ContributionDomain.formatDate(day.date),
                        if (row.getOrElse(column) { 0 } == 1) currentState.selectedLevel.value else 0,
                    )
                }
            }
        }
        applyUpdates(updates)
    }

    fun fillAllPastDays() {
        val currentState = _state.value
        val contributions = ContributionDomain.createAllGreen(
            ContributionDomain.getYearDays(currentState.year),
            currentState.year,
            currentState.selectedLevel,
        )
        commit(contributions)
    }

    fun reset() {
        commit(emptyMap())
    }

    fun undo() {
        if (historyIndex <= 0) return
        historyIndex -= 1
        restoreSnapshot()
    }

    fun redo() {
        if (historyIndex >= snapshots.lastIndex) return
        historyIndex += 1
        restoreSnapshot()
    }

    private fun observeYear(year: Int) {
        observeJob?.cancel()
        _state.value = _state.value.copy(year = year, contributions = emptyMap(), isLoading = true)
        snapshots = listOf(emptyMap())
        historyIndex = 0

        observeJob = viewModelScope.launch {
            repository.observeYear(year).collectLatest { contributions ->
                if (isApplyingLocalChange) return@collectLatest
                snapshots = listOf(contributions)
                historyIndex = 0
                _state.value = _state.value.copy(
                    contributions = contributions,
                    isLoading = false,
                    canUndo = false,
                    canRedo = false,
                )
            }
        }
    }

    private fun commit(contributions: ContributionMap) {
        val retainedSnapshots = snapshots.take(historyIndex + 1)
        snapshots = (retainedSnapshots + contributions).takeLast(MAX_HISTORY_ENTRIES)
        historyIndex = snapshots.lastIndex
        persistAndUpdate(contributions)
    }

    private fun restoreSnapshot() {
        persistAndUpdate(snapshots[historyIndex])
    }

    private fun persistAndUpdate(contributions: ContributionMap) {
        val currentState = _state.value
        _state.value = currentState.copy(
            contributions = contributions,
            isLoading = false,
            canUndo = historyIndex > 0,
            canRedo = historyIndex < snapshots.lastIndex,
        )

        viewModelScope.launch {
            isApplyingLocalChange = true
            try {
                repository.save(currentState.year, contributions)
            } finally {
                isApplyingLocalChange = false
            }
        }
    }

    class Factory(
        private val repository: ContributionRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(CanvasViewModel::class.java))
            return CanvasViewModel(repository) as T
        }
    }
}
