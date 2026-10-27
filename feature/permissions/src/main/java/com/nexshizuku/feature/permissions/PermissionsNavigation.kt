package com.nexshizuku.feature.permissions

import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavGraphBuilder

fun NavGraphBuilder.permissionsGraph(
    viewModel: PermissionsViewModel
) {
    composable("permissions") {
        PermissionsScreen(viewModel = viewModel)
    }
}
