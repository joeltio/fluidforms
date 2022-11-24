package io.joelt.texttemplate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.joelt.texttemplate.navigation.ScaffoldOptions

/**
 * Scaffold that handles the system bar padding that fixes issue 249727298.
 *
 * There is a bug where if Modifier.systemBarsPadding() is used, the Scaffold
 * will have innerPadding for the bottomBar, regardless whether or not there is
 * a bottomBar. This innerPadding is introduced through the contentWindowInsets.
 *
 * See: https://issuetracker.google.com/issues/249727298
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SystemBarScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        containerColor = containerColor,
        contentColor = contentColor,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .consumedWindowInsets(innerPadding)
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemBarScaffold(scaffoldOptions: ScaffoldOptions, content: @Composable () -> Unit) {
    SystemBarScaffold(
        modifier = scaffoldOptions.modifier,
        topBar = scaffoldOptions.topBar,
        bottomBar = scaffoldOptions.bottomBar,
        snackbarHost = scaffoldOptions.snackbarHost,
        floatingActionButton = scaffoldOptions.floatingActionButton,
        floatingActionButtonPosition = scaffoldOptions.floatingActionButtonPosition,
        content = content
    )
}
