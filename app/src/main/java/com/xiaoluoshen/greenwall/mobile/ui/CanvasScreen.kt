package com.xiaoluoshen.greenwall.mobile.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xiaoluoshen.greenwall.mobile.domain.ContributionDomain
import com.xiaoluoshen.greenwall.mobile.domain.ContributionLevel
import java.time.Year
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.theme.MiuixTheme

private val ScreenHorizontalPadding = 16.dp
private val ContentSectionSpacing = 16.dp
private val ControlSpacing = 8.dp

@Composable
fun CanvasScreen(
    state: CanvasUiState,
    onYearSelected: (Int) -> Unit,
    onEraserChanged: (Boolean) -> Unit,
    onLevelSelected: (ContributionLevel) -> Unit,
    onCellsApplied: (Map<String, Int>) -> Unit,
    onFillAll: () -> Unit,
    onGenerateRandomActive: () -> Unit,
    onReset: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
) {
    val currentYear = Year.now().value
    val minYear = currentYear - 9
    var isEditMode by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = ScreenHorizontalPadding, vertical = 20.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(ContentSectionSpacing),
    ) {
        CanvasHeader()
        CanvasStage(
            state = state,
            minYear = minYear,
            currentYear = currentYear,
            isEditMode = isEditMode,
            onYearSelected = onYearSelected,
            onCellsApplied = onCellsApplied,
        )
        CanvasDock(
            state = state,
            isEditMode = isEditMode,
            onEditModeChanged = { isEditMode = it },
            onEraserChanged = onEraserChanged,
            onLevelSelected = onLevelSelected,
            onFillAll = onFillAll,
            onGenerateRandomActive = onGenerateRandomActive,
            onReset = onReset,
            onUndo = onUndo,
            onRedo = onRedo,
        )
        Text(
            text = "字符模板可在“字符”页面选择，GitHub 同步可在“设置”页面完成",
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            style = MiuixTheme.textStyles.footnote1,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
    }
}

@Composable
private fun CanvasHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "GreenWall 贡献画布",
            style = MiuixTheme.textStyles.headline1,
        )
        Text(
            text = "选择工具后直接在日历上绘制，笔触会实时跟随手指",
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            style = MiuixTheme.textStyles.body2,
        )
    }
}

@Composable
private fun CanvasStage(
    state: CanvasUiState,
    minYear: Int,
    currentYear: Int,
    isEditMode: Boolean,
    onYearSelected: (Int) -> Unit,
    onCellsApplied: (Map<String, Int>) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(ControlSpacing),
            modifier = Modifier.fillMaxWidth(),
        ) {
            StageBadge(
                text = canvasStatusText(state, isEditMode),
                modifier = Modifier.weight(1f),
            )
            StageBadge(
                text = "${ContributionDomain.total(state.contributions)} 次",
            )
        }

        if (state.isLoading) {
            Text(
                text = "正在加载贡献数据",
                style = MiuixTheme.textStyles.body1,
                modifier = Modifier.padding(vertical = 44.dp),
            )
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                insideMargin = PaddingValues(horizontal = 12.dp, vertical = 14.dp),
            ) {
                ContributionCalendar(
                    year = state.year,
                    contributions = state.contributions,
                    selectedValue = if (state.isEraserActive) 0 else state.selectedLevel.value,
                    isEditMode = isEditMode,
                    onCellsApplied = onCellsApplied,
                )
            }
        }

        YearSwitcher(
            year = state.year,
            minYear = minYear,
            currentYear = currentYear,
            onYearSelected = onYearSelected,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Composable
private fun StageBadge(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        style = MiuixTheme.textStyles.body2,
        maxLines = 1,
        modifier = modifier,
    )
}

@Composable
private fun YearSwitcher(
    year: Int,
    minYear: Int,
    currentYear: Int,
    onYearSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(ControlSpacing),
        modifier = modifier.fillMaxWidth(),
    ) {
        TextButton(
            text = "‹",
            onClick = { onYearSelected(year - 1) },
            enabled = year > minYear,
        )
        Text(
            text = "$year 年",
            style = MiuixTheme.textStyles.title2,
            modifier = Modifier.weight(1f),
        )
        TextButton(
            text = "›",
            onClick = { onYearSelected(year + 1) },
            enabled = year < currentYear,
        )
    }
}

@Composable
private fun CanvasDock(
    state: CanvasUiState,
    isEditMode: Boolean,
    onEditModeChanged: (Boolean) -> Unit,
    onEraserChanged: (Boolean) -> Unit,
    onLevelSelected: (ContributionLevel) -> Unit,
    onFillAll: () -> Unit,
    onGenerateRandomActive: () -> Unit,
    onReset: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        insideMargin = PaddingValues(12.dp),
    ) {
        Text("编辑工具", style = MiuixTheme.textStyles.title2)
        ToolRow {
            DockToolButton(
                text = "画笔",
                isSelected = !state.isEraserActive,
                onClick = { onEraserChanged(false) },
            )
            DockToolButton(
                text = "橡皮擦",
                isSelected = state.isEraserActive,
                onClick = { onEraserChanged(true) },
            )
            DockToolButton(
                text = if (isEditMode) "绘制中" else "浏览",
                isSelected = isEditMode,
                onClick = { onEditModeChanged(!isEditMode) },
            )
            DockToolButton(
                text = "撤销",
                isSelected = false,
                enabled = state.canUndo,
                onClick = onUndo,
            )
            DockToolButton(
                text = "重做",
                isSelected = false,
                enabled = state.canRedo,
                onClick = onRedo,
            )
        }

        if (!state.isEraserActive) {
            Text(
                text = "画笔强度",
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                style = MiuixTheme.textStyles.body2,
                modifier = Modifier.padding(top = 14.dp, bottom = 8.dp),
            )
            ToolRow {
                listOf(
                    ContributionLevel.Low,
                    ContributionLevel.Medium,
                    ContributionLevel.High,
                    ContributionLevel.Maximum,
                ).forEach { level ->
                    DockToolButton(
                        text = level.value.toString(),
                        isSelected = state.selectedLevel == level,
                        onClick = { onLevelSelected(level) },
                    )
                }
            }
        }

        Text(
            text = "批量操作",
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            style = MiuixTheme.textStyles.body2,
            modifier = Modifier.padding(top = 14.dp, bottom = 8.dp),
        )
        ToolRow {
            DockToolButton(
                text = "全绿",
                isSelected = false,
                onClick = onFillAll,
            )
            DockToolButton(
                text = "随机活跃",
                isSelected = false,
                onClick = onGenerateRandomActive,
            )
            DockToolButton(
                text = "重置",
                isSelected = false,
                onClick = onReset,
            )
        }
    }
}

@Composable
private fun ToolRow(content: @Composable () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(ControlSpacing),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .horizontalScroll(rememberScrollState()),
    ) {
        content()
    }
}

@Composable
private fun DockToolButton(
    text: String,
    isSelected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    TextButton(
        text = text,
        onClick = onClick,
        enabled = enabled,
        colors = if (isSelected) ButtonDefaults.textButtonColorsPrimary() else ButtonDefaults.textButtonColors(),
    )
}

private fun canvasStatusText(state: CanvasUiState, isEditMode: Boolean): String = when {
    !isEditMode -> "浏览全年日期"
    state.isEraserActive -> "橡皮擦 · 实时编辑"
    else -> "画笔 ${state.selectedLevel.value} · 实时编辑"
}
