package com.xiaoluoshen.greenwall.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoluoshen.greenwall.mobile.domain.CharacterPatterns

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
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = "字符",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(vertical = 16.dp),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            categoryTitles.forEach { (key, title) ->
                FilterChip(
                    selected = category == key,
                    onClick = { category = key },
                    label = { Text(title) },
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(vertical = 16.dp),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = "预览：$selectedCharacter",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    PatternPreview(
                        pattern = selectedPattern,
                        cellSize = 12.dp,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                    Button(
                        onClick = { onPatternConfirmed(selectedPattern) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                    ) {
                        Text("应用到画布")
                    }
                }
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
    val colorScheme = MaterialTheme.colorScheme
    val borderColor = if (isSelected) colorScheme.primary else colorScheme.outlineVariant

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .height(84.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) colorScheme.primaryContainer else colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(8.dp),
    ) {
        PatternPreview(pattern = pattern, cellSize = 4.dp)
        Text(
            text = character,
            color = borderColor,
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 4.dp),
        )
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
                                if (value == 1) Color(0xFF216E39) else MaterialTheme.colorScheme.surface,
                                RoundedCornerShape(1.dp),
                            ),
                    )
                }
            }
        }
    }
}
