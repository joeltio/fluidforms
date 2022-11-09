package io.joelt.texttemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.joelt.texttemplate.navigation.ScaffoldWithOptions
import io.joelt.texttemplate.navigation.currentRouteAsState
import io.joelt.texttemplate.ui.components.SimpleScaffold
import io.joelt.texttemplate.ui.screens.BottomNavBar
import io.joelt.texttemplate.ui.screens.TopNavBar
import io.joelt.texttemplate.ui.screens.archived.ArchivedScreen
import io.joelt.texttemplate.ui.screens.archived_view.ArchivedViewScreen
import io.joelt.texttemplate.ui.screens.draft_edit.DraftEditScreen
import io.joelt.texttemplate.ui.screens.drafts.DraftsScreen
import io.joelt.texttemplate.ui.screens.settings.SettingsScreen
import io.joelt.texttemplate.ui.screens.template_edit.TemplateEditScreen
import io.joelt.texttemplate.ui.screens.templates.TemplatesScreen
import io.joelt.texttemplate.ui.theme.TextTemplateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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

                ScaffoldWithOptions(scaffoldOptions = currentScreen.scaffold(navController)) {
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
