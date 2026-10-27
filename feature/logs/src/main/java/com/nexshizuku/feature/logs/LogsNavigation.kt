package com.nexshizuku.feature.logs

import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavGraphBuilder

fun NavGraphBuilder.logsGraph(
    viewModel: LogsViewModel
) {
    composable("logs") {
        LogsScreen(viewModel = viewModel)
    }
}
