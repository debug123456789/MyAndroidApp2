package com.nexshizuku.feature.settings

import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavGraphBuilder

fun NavGraphBuilder.settingsGraph(
    viewModel: SettingsViewModel
) {
    composable("settings") {
        SettingsScreen(viewModel = viewModel)
    }
}
