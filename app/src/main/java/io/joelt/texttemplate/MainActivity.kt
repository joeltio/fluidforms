package io.joelt.texttemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.joelt.texttemplate.navigation.ScaffoldOptions
import io.joelt.texttemplate.navigation.currentRouteAsState
import io.joelt.texttemplate.ui.components.SystemBarScaffold
import io.joelt.texttemplate.ui.screens.archived.ArchivedScreen
import io.joelt.texttemplate.ui.screens.archived_view.ArchivedViewScreen
import io.joelt.texttemplate.ui.screens.draft_edit.DraftEditScreen
import io.joelt.texttemplate.ui.screens.drafts.DraftsScreen
import io.joelt.texttemplate.ui.screens.settings.SettingsScreen
import io.joelt.texttemplate.ui.screens.template_edit.TemplateEditScreen
import io.joelt.texttemplate.ui.screens.templates.TemplatesScreen
import io.joelt.texttemplate.ui.theme.TextTemplateTheme

@Composable
fun AppScaffold(scaffoldOptions: ScaffoldOptions, content: @Composable () -> Unit) {
    SystemBarScaffold(scaffoldOptions, content)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {
            TextTemplateTheme {
                val screenStartIndex = 0
                val screens = listOf(
                    TemplatesScreen(),
                    DraftsScreen(),
                    ArchivedScreen(),
                    SettingsScreen(),
                    TemplateEditScreen(),
                    DraftEditScreen(),
                    ArchivedViewScreen(),
                )


                val navController = rememberNavController()

                val currentRoute = navController.currentRouteAsState()
                val screenMap = buildMap { screens.forEach { screen -> put(screen.route, screen) } }
                val startingScreen = screens[screenStartIndex]
                val currentScreen = screenMap[currentRoute] ?: startingScreen

                AppScaffold(scaffoldOptions = currentScreen.scaffold(navController)) {
                    NavHost(navController, startDestination = startingScreen.route) {
                        screens.forEach { screen ->
                            composable(screen.route, arguments = screen.arguments) {
                                screen.Composable(it, navController)
                            }
                        }
                    }
                }
            }
        }
    }
}
