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
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = "画布",
            style = MiuixTheme.textStyles.headline1,
            modifier = Modifier.padding(vertical = 16.dp),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
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

        Text(
            text = "${state.year} 年共 ${ContributionDomain.total(state.contributions)} 次贡献",
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            style = MiuixTheme.textStyles.body2,
            modifier = Modifier.padding(vertical = 12.dp),
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            insideMargin = PaddingValues(16.dp),
        ) {
            if (state.isLoading) {
                Text("正在加载贡献数据", style = MiuixTheme.textStyles.body1)
            } else {
                ContributionCalendar(
                    year = state.year,
                    contributions = state.contributions,
                    selectedValue = if (state.isEraserActive) 0 else state.selectedLevel.value,
                    onCellsApplied = onCellsApplied,
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            insideMargin = PaddingValues(16.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    text = "强度",
                    style = MiuixTheme.textStyles.title3,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp),
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

        TextButton(
            text = "GitHub 同步将在设置登录后启用",
            onClick = {},
            enabled = false,
            colors = ButtonDefaults.textButtonColorsPrimary(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
        )
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
