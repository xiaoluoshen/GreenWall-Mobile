package com.xiaoluoshen.greenwall.mobile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
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
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "设置",
                style = MiuixTheme.textStyles.headline1,
            )
            Text(
                text = "管理账户、显示语言与贡献同步",
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                style = MiuixTheme.textStyles.body2,
            )
        }

        state.message?.let { message ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                insideMargin = PaddingValues(16.dp),
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

        SettingsSectionTitle(
            title = "GitHub 账户",
            summary = "令牌将通过设备安全存储加密保存",
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            insideMargin = PaddingValues(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (state.session == null) {
                    Text(
                        text = "使用个人访问令牌登录后，即可创建仓库并生成贡献提交",
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
                        modifier = Modifier.fillMaxWidth(),
                    )
                    TextButton(
                        text = "登录",
                        onClick = onLogin,
                        enabled = state.token.isNotBlank() && !state.isBusy,
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    BusyIndicator(isBusy = state.isBusy, label = "正在验证账户")
                } else {
                    Text(
                        text = "已登录为 ${state.session.name ?: state.session.login} @${state.session.login}",
                        style = MiuixTheme.textStyles.body1,
                    )
                    TextButton(
                        text = "退出登录",
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        SettingsSectionTitle(
            title = "语言",
            summary = "切换应用界面显示语言",
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
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

        SettingsSectionTitle(
            title = "GitHub 同步",
            summary = "根据当前画布生成历史贡献提交",
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            insideMargin = PaddingValues(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                    modifier = Modifier.fillMaxWidth(),
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "描述",
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(if (isPrivate) "私有仓库" else "公开仓库", style = MiuixTheme.textStyles.body1)
                        Text(
                            text = "仓库可见性",
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            style = MiuixTheme.textStyles.footnote1,
                        )
                    }
                    Switch(checked = isPrivate, onCheckedChange = { isPrivate = it })
                }
                state.syncProgress?.let { (current, total) ->
                    Text(
                        text = "正在生成 $current/$total",
                        style = MiuixTheme.textStyles.body2,
                    )
                }
                TextButton(
                    text = "生成并推送",
                    onClick = { onPublish(repositoryName, description, isPrivate, contributions) },
                    enabled = state.session != null && contributions.isNotEmpty() && !state.isBusy,
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                    modifier = Modifier.fillMaxWidth(),
                )
                BusyIndicator(isBusy = state.isBusy, label = "正在同步贡献记录")
            }
        }

        Text(
            text = "GreenWall Mobile 2.2.0 · Kotlin + Jetpack Compose + MIUIX",
            style = MiuixTheme.textStyles.footnote1,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
    }
}

@Composable
private fun SettingsSectionTitle(
    title: String,
    summary: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(title, style = MiuixTheme.textStyles.title2)
        Text(
            text = summary,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            style = MiuixTheme.textStyles.footnote1,
        )
    }
}

@Composable
private fun BusyIndicator(
    isBusy: Boolean,
    label: String,
) {
    if (isBusy) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 4.dp),
        ) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp))
            Text(label, style = MiuixTheme.textStyles.body2)
        }
    }
}
