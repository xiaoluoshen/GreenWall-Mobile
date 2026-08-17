package com.xiaoluoshen.greenwall.mobile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(top = 16.dp),
        )

        state.message?.let { message ->
            Card {
                Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(message, modifier = Modifier.weight(1f))
                    OutlinedButton(onClick = onConsumeMessage) { Text("关闭") }
                }
            }
        }

        Text("GitHub 账户", style = MaterialTheme.typography.titleMedium)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (state.session == null) {
                    Text("使用个人访问令牌登录，令牌仅加密保存在本机")
                    OutlinedTextField(
                        value = state.token,
                        onValueChange = onTokenChange,
                        label = { Text("个人访问令牌") },
                        singleLine = true,
                        visualTransformation = if (state.isTokenVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = onToggleTokenVisibility) {
                                Icon(
                                    if (state.isTokenVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = "显示或隐藏令牌",
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Button(
                        onClick = onLogin,
                        enabled = state.token.isNotBlank() && !state.isBusy,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (state.isBusy) CircularProgressIndicator() else Text("登录")
                    }
                } else {
                    Text("已登录为 ${state.session.name ?: state.session.login} @${state.session.login}")
                    OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                        Text("退出登录")
                    }
                }
            }
        }

        Text("语言", style = MaterialTheme.typography.titleMedium)
        Card(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("界面语言")
                OutlinedButton(onClick = onToggleLanguage) {
                    Text(if (state.language == AppLanguage.Chinese) "中文" else "English")
                }
            }
        }

        Text("GitHub 同步", style = MaterialTheme.typography.titleMedium)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("创建仓库并按绘制结果生成贡献提交")
                OutlinedTextField(
                    value = repositoryName,
                    onValueChange = { repositoryName = it },
                    label = { Text("仓库名称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("描述") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(if (isPrivate) "私有仓库" else "公开仓库")
                    Switch(checked = isPrivate, onCheckedChange = { isPrivate = it })
                }
                state.syncProgress?.let { (current, total) ->
                    Text("正在生成 $current/$total")
                }
                Button(
                    onClick = { onPublish(repositoryName, description, isPrivate, contributions) },
                    enabled = state.session != null && contributions.isNotEmpty() && !state.isBusy,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (state.isBusy) CircularProgressIndicator() else Text("生成并推送")
                }
            }
        }

        Text(
            text = "GreenWall Mobile 2.0.0 · Kotlin + Jetpack Compose",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp),
        )
    }
}
