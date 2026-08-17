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
    onReset: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
) {
    val years = (0..9).map { Year.now().value - it }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = ScreenHorizontalPadding, vertical = 20.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(ContentSectionSpacing),
    ) {
        CanvasHeader()

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("年份", style = MiuixTheme.textStyles.title3)
            Row(
                horizontalArrangement = Arrangement.spacedBy(ControlSpacing),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
            ) {
                years.forEach { year ->
                    SelectionButton(
                        text = year.toString(),
                        isSelected = state.year == year,
                        onClick = { onYearSelected(year) },
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            insideMargin = PaddingValues(12.dp),
        ) {
            Text(
                text = "${state.year} 年共 ${ContributionDomain.total(state.contributions)} 次贡献",
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                style = MiuixTheme.textStyles.body2,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            if (state.isLoading) {
                Text(
                    text = "正在加载贡献数据",
                    style = MiuixTheme.textStyles.body1,
                    modifier = Modifier.padding(vertical = 32.dp),
                )
            } else {
                ContributionCalendar(
                    year = state.year,
                    contributions = state.contributions,
                    selectedValue = if (state.isEraserActive) 0 else state.selectedLevel.value,
                    onCellsApplied = onCellsApplied,
                )
            }
        }

        CanvasControls(
            state = state,
            onEraserChanged = onEraserChanged,
            onLevelSelected = onLevelSelected,
            onFillAll = onFillAll,
            onReset = onReset,
            onUndo = onUndo,
            onRedo = onRedo,
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            insideMargin = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Text(
                text = "GitHub 同步",
                style = MiuixTheme.textStyles.title3,
            )
            Text(
                text = "完成绘制后，可前往设置页登录并生成贡献提交",
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                style = MiuixTheme.textStyles.body2,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun CanvasHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "画布",
            style = MiuixTheme.textStyles.headline1,
        )
        Text(
            text = "选择颜色强度后，轻触单格或长按拖动即可绘制",
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            style = MiuixTheme.textStyles.body2,
        )
    }
}

@Composable
private fun CanvasControls(
    state: CanvasUiState,
    onEraserChanged: (Boolean) -> Unit,
    onLevelSelected: (ContributionLevel) -> Unit,
    onFillAll: () -> Unit,
    onReset: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        insideMargin = PaddingValues(16.dp),
    ) {
        Text("绘制工具", style = MiuixTheme.textStyles.title2)
        Text(
            text = "连续拖动会合并为一次操作，可随时撤销或重做",
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            style = MiuixTheme.textStyles.body2,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(ControlSpacing)) {
            SelectionButton(
                text = "画笔",
                isSelected = !state.isEraserActive,
                onClick = { onEraserChanged(false) },
            )
            SelectionButton(
                text = "橡皮擦",
                isSelected = state.isEraserActive,
                onClick = { onEraserChanged(true) },
            )
        }

        if (!state.isEraserActive) {
            Text(
                text = "贡献强度",
                style = MiuixTheme.textStyles.title3,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(ControlSpacing)) {
                listOf(
                    ContributionLevel.Low,
                    ContributionLevel.Medium,
                    ContributionLevel.High,
                    ContributionLevel.Maximum,
                ).forEach { level ->
                    SelectionButton(
                        text = level.value.toString(),
                        isSelected = state.selectedLevel == level,
                        onClick = { onLevelSelected(level) },
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(ControlSpacing),
            modifier = Modifier.padding(top = 16.dp),
        ) {
            TextButton(
                text = "全绿",
                onClick = onFillAll,
                modifier = Modifier.weight(1f),
            )
            TextButton(
                text = "重置",
                onClick = onReset,
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(ControlSpacing),
            modifier = Modifier.padding(top = ControlSpacing),
        ) {
            TextButton(
                text = "撤销",
                onClick = onUndo,
                enabled = state.canUndo,
                modifier = Modifier.weight(1f),
            )
            TextButton(
                text = "重做",
                onClick = onRedo,
                enabled = state.canRedo,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SelectionButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    TextButton(
        text = text,
        onClick = onClick,
        colors = if (isSelected) ButtonDefaults.textButtonColorsPrimary() else ButtonDefaults.textButtonColors(),
    )
}
