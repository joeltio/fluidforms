package io.joelt.texttemplate

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.joelt.texttemplate.database.LocalPreferences
import io.joelt.texttemplate.database.UserPreferences
import io.joelt.texttemplate.database.UserPreferencesRepository
import io.joelt.texttemplate.navigation.ScaffoldOptions
import io.joelt.texttemplate.navigation.currentRouteAsState
import io.joelt.texttemplate.ui.components.SystemBarScaffold
import io.joelt.texttemplate.ui.screens.archived.ArchivedScreen
import io.joelt.texttemplate.ui.screens.archived_preview.ArchivedPreviewScreen
import io.joelt.texttemplate.ui.screens.draft_edit.DraftEditScreen
import io.joelt.texttemplate.ui.screens.drafts.DraftsScreen
import io.joelt.texttemplate.ui.screens.settings.SettingsScreen
import io.joelt.texttemplate.ui.screens.template_edit.TemplateEditScreen
import io.joelt.texttemplate.ui.screens.template_preview.TemplatePreviewScreen
import io.joelt.texttemplate.ui.screens.templates.TemplatesScreen
import io.joelt.texttemplate.ui.theme.TextTemplateTheme

private val Context.dataStore by preferencesDataStore(
    name = "user_preferences"
)

@Composable
fun AppScaffold(scaffoldOptions: ScaffoldOptions, content: @Composable () -> Unit) {
    SystemBarScaffold(scaffoldOptions, content)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        val preferencesRepo = UserPreferencesRepository(dataStore)
        setContent {
            // Screens
            val screenStartIndex = 0
            val screens = listOf(
                TemplatesScreen(),
                TemplatePreviewScreen(),
                TemplateEditScreen(),
                DraftsScreen(),
                ArchivedScreen(),
                SettingsScreen(preferencesRepo),
                DraftEditScreen(),
                ArchivedPreviewScreen(),
            )

            // Navigation
            val navController = rememberNavController()
            val currentRoute = navController.currentRouteAsState()
            val screenMap = buildMap { screens.forEach { screen -> put(screen.route, screen) } }
            val startingScreen = screens[screenStartIndex]
            val currentScreen = screenMap[currentRoute] ?: startingScreen

            // Preferences
            val preferences by preferencesRepo.flow.collectAsState(initial = UserPreferences())
            CompositionLocalProvider(LocalPreferences provides preferences) {
                TextTemplateTheme {
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
}
