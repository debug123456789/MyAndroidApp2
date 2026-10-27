package com.nexshizuku.feature.service

import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavGraphBuilder

fun NavGraphBuilder.serviceGraph(
    viewModel: ServiceViewModel
) {
    composable("service") {
        ServiceScreen(viewModel = viewModel)
    }
}
