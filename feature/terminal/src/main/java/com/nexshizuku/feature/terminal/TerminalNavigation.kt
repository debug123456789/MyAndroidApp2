package com.nexshizuku.feature.terminal

import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavGraphBuilder

fun NavGraphBuilder.terminalGraph(
    viewModel: TerminalViewModel
) {
    composable("terminal") {
        TerminalScreen(viewModel = viewModel)
    }
}
