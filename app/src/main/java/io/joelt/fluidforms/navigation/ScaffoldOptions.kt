package io.joelt.fluidforms.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

data class ScaffoldOptions @OptIn(ExperimentalMaterial3Api::class) constructor(
    var modifier: Modifier = Modifier,
    var topBar: @Composable () -> Unit = {},
    var bottomBar: @Composable () -> Unit = {},
    var snackbarHost: @Composable () -> Unit = {},
    var floatingActionButton: @Composable () -> Unit = {},
    var floatingActionButtonPosition: FabPosition = FabPosition.End,
)
