package com.xiaoluoshen.greenwall.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoluoshen.greenwall.mobile.domain.CharacterPatterns
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.theme.MiuixTheme

private val categoryTitles = mapOf(
    "uppercase" to "大写",
    "lowercase" to "小写",
    "numbers" to "数字",
    "symbols" to "符号",
)

@Composable
fun CharactersScreen(
    onPatternConfirmed: (Array<IntArray>) -> Unit,
) {
    var category by remember { mutableStateOf("uppercase") }
    var selectedCharacter by remember(category) { mutableStateOf<String?>(null) }
    val patterns = CharacterPatterns.categories.getValue(category)
    val selectedPattern = selectedCharacter?.let(patterns::get)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "字符",
                style = MiuixTheme.textStyles.headline1,
            )
            Text(
                text = "选择一个字符图案，再应用到当前贡献画布",
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                style = MiuixTheme.textStyles.body2,
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .horizontalScroll(rememberScrollState()),
        ) {
            categoryTitles.forEach { (key, title) ->
                TextButton(
                    text = title,
                    onClick = { category = key },
                    colors = if (category == key) {
                        ButtonDefaults.textButtonColorsPrimary()
                    } else {
                        ButtonDefaults.textButtonColors()
                    },
                )
            }
        }

        Text(
            text = "轻触卡片选择图案",
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            style = MiuixTheme.textStyles.footnote1,
            modifier = Modifier.padding(top = 12.dp),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(top = 12.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f),
        ) {
            items(patterns.keys.sorted()) { character ->
                CharacterTile(
                    character = character,
                    pattern = patterns.getValue(character),
                    isSelected = character == selectedCharacter,
                    onClick = { selectedCharacter = character },
                )
            }
        }

        if (selectedPattern != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                insideMargin = PaddingValues(16.dp),
            ) {
                Text(
                    text = "已选择：$selectedCharacter",
                    style = MiuixTheme.textStyles.title2,
                )
                Text(
                    text = "确认后将覆盖到画布中的对应日期格",
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    style = MiuixTheme.textStyles.body2,
                    modifier = Modifier.padding(top = 4.dp),
                )
                PatternPreview(
                    pattern = selectedPattern,
                    cellSize = 12.dp,
                    modifier = Modifier.padding(top = 14.dp),
                )
                TextButton(
                    text = "应用到画布",
                    onClick = { onPatternConfirmed(selectedPattern) },
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun CharacterTile(
    character: String,
    pattern: Array<IntArray>,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val cardColor = if (isSelected) {
        MiuixTheme.colorScheme.primaryVariant
    } else {
        MiuixTheme.colorScheme.surfaceContainer
    }
    val contentColor = if (isSelected) {
        MiuixTheme.colorScheme.onPrimaryVariant
    } else {
        MiuixTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier
            .height(88.dp)
            .fillMaxWidth(),
        insideMargin = PaddingValues(8.dp),
        colors = CardDefaults.defaultColors(color = cardColor),
        onClick = onClick,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            PatternPreview(pattern = pattern, cellSize = 4.dp)
            Text(
                text = character,
                color = contentColor,
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

@Composable
private fun PatternPreview(
    pattern: Array<IntArray>,
    cellSize: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        pattern.forEach { row ->
            Row {
                row.forEach { value ->
                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .padding(0.5.dp)
                            .background(
                                if (value == 1) Color(0xFF216E39) else MiuixTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(1.dp),
                            ),
                    )
                }
            }
        }
    }
}
