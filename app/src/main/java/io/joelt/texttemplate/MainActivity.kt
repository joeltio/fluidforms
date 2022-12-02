package io.joelt.texttemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.joelt.texttemplate.database.LocalPreferences
import io.joelt.texttemplate.database.UserPreferences
import io.joelt.texttemplate.database.UserPreferencesRepository
import io.joelt.texttemplate.navigation.Route
import io.joelt.texttemplate.navigation.ScaffoldOptions
import io.joelt.texttemplate.ui.components.SystemBarScaffold
import io.joelt.texttemplate.ui.screens.BottomNavBar
import io.joelt.texttemplate.ui.screens.TopNavBar
import io.joelt.texttemplate.ui.screens.archived.ArchivedScreen
import io.joelt.texttemplate.ui.screens.archived.archived
import io.joelt.texttemplate.ui.screens.archived_preview.ArchivedPreviewScreen
import io.joelt.texttemplate.ui.screens.draft_edit.DraftEditScreen
import io.joelt.texttemplate.ui.screens.drafts.DraftsScreen
import io.joelt.texttemplate.ui.screens.drafts.drafts
import io.joelt.texttemplate.ui.screens.settings.SettingsScreen
import io.joelt.texttemplate.ui.screens.template_edit.TemplateEditScreen
import io.joelt.texttemplate.ui.screens.template_preview.TemplatePreviewScreen
import io.joelt.texttemplate.ui.screens.templates.TemplatesScreen
import io.joelt.texttemplate.ui.screens.templates.templates
import io.joelt.texttemplate.ui.theme.TextTemplateTheme
import org.koin.android.ext.android.inject

@Composable
fun AppScaffold(scaffoldOptions: ScaffoldOptions, content: @Composable BoxScope.() -> Unit) {
    SystemBarScaffold(scaffoldOptions, content)
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        setContent {
            // Screens
            val screenStartIndex = 0
            val screens = listOf(
                TemplatesScreen,
                TemplatePreviewScreen,
                TemplateEditScreen,
                DraftsScreen,
                ArchivedScreen,
                SettingsScreen,
                TemplatesScreen,
                DraftEditScreen,
                ArchivedPreviewScreen,
            )

            // Navigation
            val navController = rememberNavController()
            var scaffoldOptions by remember { mutableStateOf(ScaffoldOptions()) }

            // Preferences
            val preferencesRepo: UserPreferencesRepository by inject()
            val preferences by preferencesRepo.flow.collectAsState(initial = UserPreferences())
            CompositionLocalProvider(LocalPreferences provides preferences) {
                TextTemplateTheme {
                    // Share the home bars
                    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
                    val homeBottomBar: @Composable () -> Unit = { BottomNavBar(navController) }
                    val homeTopBar: @Composable () -> Unit =
                        { TopNavBar(navController, scrollBehavior) }

                    AppScaffold(scaffoldOptions = scaffoldOptions) {
                        NavHost(navController, startDestination = screens[screenStartIndex].route) {
                            screens.forEach { screen ->
                                composable(
                                    screen.route,
                                    arguments = screen.arguments
                                ) { backStackEntry ->
                                    val screenContent =
                                        screen.contentFactory(backStackEntry, navController)

                                    if (screen.route in listOf(
                                            Route.templates,
                                            Route.drafts,
                                            Route.archived
                                        )
                                    ) {
                                        screenContent.scaffoldOptions.topBar = homeTopBar
                                        screenContent.scaffoldOptions.bottomBar = homeBottomBar
                                        screenContent.scaffoldOptions.modifier =
                                            Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                                    }

                                    // Run this only once. If put outside, it will trigger an
                                    // infinite loop of recomposing and setting the scaffold options
                                    LaunchedEffect(Unit) {
                                        scaffoldOptions = screenContent.scaffoldOptions
                                    }

                                    screenContent.content()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
