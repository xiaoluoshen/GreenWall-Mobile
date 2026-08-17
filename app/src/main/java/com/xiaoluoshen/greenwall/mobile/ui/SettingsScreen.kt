package com.xiaoluoshen.greenwall.mobile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.xiaoluoshen.greenwall.mobile.domain.ContributionMap
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.Switch
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    contributions: ContributionMap,
    onTokenChange: (String) -> Unit,
    onToggleTokenVisibility: () -> Unit,
    onLogin: () -> Unit,
    onLogout: () -> Unit,
    onToggleLanguage: () -> Unit,
    onPublish: (String, String, Boolean, ContributionMap) -> Unit,
    onConsumeMessage: () -> Unit,
) {
    var repositoryName by remember { mutableStateOf("greenwall-contributions") }
    var description by remember { mutableStateOf("Generated with GreenWall") }
    var isPrivate by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "设置",
            style = MiuixTheme.textStyles.headline1,
            modifier = Modifier.padding(top = 16.dp),
        )

        state.message?.let { message ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                insideMargin = androidx.compose.foundation.layout.PaddingValues(16.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = message,
                        style = MiuixTheme.textStyles.body2,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(text = "关闭", onClick = onConsumeMessage)
                }
            }
        }

        Text("GitHub 账户", style = MiuixTheme.textStyles.title2)
        Card(
            modifier = Modifier.fillMaxWidth(),
            insideMargin = androidx.compose.foundation.layout.PaddingValues(16.dp),
        ) {
            if (state.session == null) {
                Text(
                    text = "使用个人访问令牌登录，令牌仅加密保存在本机",
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
                TextField(
                    value = state.token,
                    onValueChange = onTokenChange,
                    label = "个人访问令牌",
                    singleLine = true,
                    visualTransformation = if (state.isTokenVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        TextButton(
                            text = if (state.isTokenVisible) "隐藏" else "显示",
                            onClick = onToggleTokenVisibility,
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                )
                TextButton(
                    text = "登录",
                    onClick = onLogin,
                    enabled = state.token.isNotBlank() && !state.isBusy,
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                )
                if (state.isBusy) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .padding(top = 8.dp),
                    )
                }
            } else {
                Text(
                    text = "已登录为 ${state.session.name ?: state.session.login} @${state.session.login}",
                    style = MiuixTheme.textStyles.body1,
                )
                TextButton(
                    text = "退出登录",
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                )
            }
        }

        Text("语言", style = MiuixTheme.textStyles.title2)
        Card(
            modifier = Modifier.fillMaxWidth(),
            insideMargin = androidx.compose.foundation.layout.PaddingValues(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("界面语言", style = MiuixTheme.textStyles.body1)
                TextButton(
                    text = if (state.language == AppLanguage.Chinese) "中文" else "English",
                    onClick = onToggleLanguage,
                )
            }
        }

        Text("GitHub 同步", style = MiuixTheme.textStyles.title2)
        Card(
            modifier = Modifier.fillMaxWidth(),
            insideMargin = androidx.compose.foundation.layout.PaddingValues(16.dp),
        ) {
            Text(
                text = "创建仓库并按绘制结果生成贡献提交",
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
            TextField(
                value = repositoryName,
                onValueChange = { repositoryName = it },
                label = "仓库名称",
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            )
            TextField(
                value = description,
                onValueChange = { description = it },
                label = "描述",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            ) {
                Text(if (isPrivate) "私有仓库" else "公开仓库", style = MiuixTheme.textStyles.body1)
                Switch(checked = isPrivate, onCheckedChange = { isPrivate = it })
            }
            state.syncProgress?.let { (current, total) ->
                Text(
                    text = "正在生成 $current/$total",
                    style = MiuixTheme.textStyles.body2,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
            TextButton(
                text = "生成并推送",
                onClick = { onPublish(repositoryName, description, isPrivate, contributions) },
                enabled = state.session != null && contributions.isNotEmpty() && !state.isBusy,
                colors = ButtonDefaults.textButtonColorsPrimary(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            )
            if (state.isBusy) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(top = 8.dp),
                )
            }
        }

        Text(
            text = "GreenWall Mobile 2.1.0 · Kotlin + Jetpack Compose + MIUIX",
            style = MiuixTheme.textStyles.footnote1,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            modifier = Modifier.padding(bottom = 32.dp),
        )
    }
}
