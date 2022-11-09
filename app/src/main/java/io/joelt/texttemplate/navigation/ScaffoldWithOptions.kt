package io.joelt.texttemplate.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import io.joelt.texttemplate.ui.components.*

data class ScaffoldOptions @OptIn(ExperimentalMaterial3Api::class) constructor(
    val modifier: Modifier = Modifier,
    val topBar: @Composable () -> Unit = {},
    val bottomBar: @Composable () -> Unit = {},
    val snackbarHost: @Composable () -> Unit = {},
    val floatingActionButton: @Composable () -> Unit = {},
    val floatingActionButtonPosition: FabPosition = FabPosition.End,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWithOptions(scaffoldOptions: ScaffoldOptions, content: @Composable () -> Unit) {
    SimpleScaffold(
        modifier = scaffoldOptions.modifier,
        topBar = scaffoldOptions.topBar,
        bottomBar = scaffoldOptions.bottomBar,
        snackbarHost = scaffoldOptions.snackbarHost,
        floatingActionButton = scaffoldOptions.floatingActionButton,
        floatingActionButtonPosition = scaffoldOptions.floatingActionButtonPosition,
        content = content
    )
}
