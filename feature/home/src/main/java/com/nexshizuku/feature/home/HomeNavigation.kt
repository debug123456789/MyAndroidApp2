package com.nexshizuku.feature.home

import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavGraphBuilder

fun NavGraphBuilder.homeGraph(
    viewModel: HomeViewModel
) {
    composable("home") {
        HomeScreen(viewModel = viewModel)
    }
}
