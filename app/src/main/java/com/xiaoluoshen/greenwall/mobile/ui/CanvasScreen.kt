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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Redo
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xiaoluoshen.greenwall.mobile.domain.ContributionDomain
import com.xiaoluoshen.greenwall.mobile.domain.ContributionLevel
import java.time.Year

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
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = "画布",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(vertical = 16.dp),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
        ) {
            years.forEach { year ->
                FilterChip(
                    selected = state.year == year,
                    onClick = { onYearSelected(year) },
                    label = { Text(year.toString()) },
                )
            }
        }

        Text(
            text = "${state.year} 年共 ${ContributionDomain.total(state.contributions)} 次贡献",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 12.dp),
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            if (state.isLoading) {
                Text("正在加载贡献数据", modifier = Modifier.padding(24.dp))
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
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !state.isEraserActive,
                        onClick = { onEraserChanged(false) },
                        label = { Text("画笔") },
                    )
                    FilterChip(
                        selected = state.isEraserActive,
                        onClick = { onEraserChanged(true) },
                        label = { Text("橡皮擦") },
                    )
                }

                if (!state.isEraserActive) {
                    Text(
                        text = "强度",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            ContributionLevel.Low,
                            ContributionLevel.Medium,
                            ContributionLevel.High,
                            ContributionLevel.Maximum,
                        ).forEach { level ->
                            FilterChip(
                                selected = state.selectedLevel == level,
                                onClick = { onLevelSelected(level) },
                                label = { Text(level.value.toString()) },
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    OutlinedButton(onClick = onFillAll, modifier = Modifier.weight(1f)) {
                        Text("全绿")
                    }
                    OutlinedButton(onClick = onReset, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Outlined.Delete, contentDescription = null)
                        Text("重置")
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    OutlinedButton(
                        onClick = onUndo,
                        enabled = state.canUndo,
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.AutoMirrored.Outlined.Undo, contentDescription = null)
                        Text("撤销")
                    }
                    OutlinedButton(
                        onClick = onRedo,
                        enabled = state.canRedo,
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.AutoMirrored.Outlined.Redo, contentDescription = null)
                        Text("重做")
                    }
                }
            }
        }

        Button(
            onClick = { },
            enabled = false,
            contentPadding = PaddingValues(vertical = 14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
        ) {
            Text("GitHub 同步将在设置登录后启用")
        }
    }
}
