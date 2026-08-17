package com.xiaoluoshen.greenwall.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.xiaoluoshen.greenwall.mobile.data.ContributionRepository
import com.xiaoluoshen.greenwall.mobile.data.SecureSessionRepository
import com.xiaoluoshen.greenwall.mobile.github.GitHubService
import com.xiaoluoshen.greenwall.mobile.ui.CanvasScreen
import com.xiaoluoshen.greenwall.mobile.ui.CanvasViewModel
import com.xiaoluoshen.greenwall.mobile.ui.CharactersScreen
import com.xiaoluoshen.greenwall.mobile.ui.SettingsScreen
import com.xiaoluoshen.greenwall.mobile.ui.SettingsViewModel
import com.xiaoluoshen.greenwall.mobile.ui.theme.GreenWallTheme
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.theme.MiuixTheme

private const val CANVAS_ROUTE = "canvas"
private const val CHARACTERS_ROUTE = "characters"
private const val SETTINGS_ROUTE = "settings"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GreenWallTheme {
                GreenWallApp(
                    contributionRepository = ContributionRepository(applicationContext),
                    sessionRepository = SecureSessionRepository(applicationContext),
                    githubService = GitHubService(),
                )
            }
        }
    }
}

@Composable
private fun GreenWallApp(
    contributionRepository: ContributionRepository,
    sessionRepository: SecureSessionRepository,
    githubService: GitHubService,
) {
    val canvasViewModel: CanvasViewModel = viewModel(
        factory = CanvasViewModel.Factory(contributionRepository),
    )
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(sessionRepository, githubService),
    )
    val canvasState by canvasViewModel.state.collectAsState()
    val settingsState by settingsViewModel.state.collectAsState()
    val navigationController = rememberNavController()
    val navigationEntry by navigationController.currentBackStackEntryAsState()
    val currentRoute = navigationEntry?.destination?.route ?: CANVAS_ROUTE

    Scaffold(
        containerColor = MiuixTheme.colorScheme.background,
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == CANVAS_ROUTE,
                    onClick = { navigationController.navigateTo(CANVAS_ROUTE) },
                    icon = Icons.Outlined.Brush,
                    label = "画布",
                )
                NavigationBarItem(
                    selected = currentRoute == CHARACTERS_ROUTE,
                    onClick = { navigationController.navigateTo(CHARACTERS_ROUTE) },
                    icon = Icons.Outlined.TextFields,
                    label = "字符",
                )
                NavigationBarItem(
                    selected = currentRoute == SETTINGS_ROUTE,
                    onClick = { navigationController.navigateTo(SETTINGS_ROUTE) },
                    icon = Icons.Outlined.Settings,
                    label = "设置",
                )
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navigationController,
            startDestination = CANVAS_ROUTE,
            modifier = Modifier.padding(paddingValues),
        ) {
            composable(CANVAS_ROUTE) {
                CanvasScreen(
                    state = canvasState,
                    onYearSelected = canvasViewModel::selectYear,
                    onEraserChanged = canvasViewModel::setEraserActive,
                    onLevelSelected = canvasViewModel::selectLevel,
                    onCellsApplied = canvasViewModel::applyUpdates,
                    onFillAll = canvasViewModel::fillAllPastDays,
                    onReset = canvasViewModel::reset,
                    onUndo = canvasViewModel::undo,
                    onRedo = canvasViewModel::redo,
                )
            }
            composable(CHARACTERS_ROUTE) {
                CharactersScreen { pattern ->
                    canvasViewModel.applyCharacterPattern(pattern)
                    navigationController.navigateTo(CANVAS_ROUTE)
                }
            }
            composable(SETTINGS_ROUTE) {
                SettingsScreen(
                    state = settingsState,
                    contributions = canvasState.contributions,
                    onTokenChange = settingsViewModel::updateToken,
                    onToggleTokenVisibility = settingsViewModel::toggleTokenVisibility,
                    onLogin = settingsViewModel::login,
                    onLogout = settingsViewModel::logout,
                    onToggleLanguage = settingsViewModel::toggleLanguage,
                    onPublish = settingsViewModel::publish,
                    onConsumeMessage = settingsViewModel::consumeMessage,
                )
            }
        }
    }
}

private fun androidx.navigation.NavController.navigateTo(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
