package io.joelt.texttemplate.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

data class ScaffoldOptions @OptIn(ExperimentalMaterial3Api::class) constructor(
    val modifier: Modifier = Modifier,
    val topBar: @Composable () -> Unit = {},
    val bottomBar: @Composable () -> Unit = {},
    val snackbarHost: @Composable () -> Unit = {},
    val floatingActionButton: @Composable () -> Unit = {},
    val floatingActionButtonPosition: FabPosition = FabPosition.End,
)
