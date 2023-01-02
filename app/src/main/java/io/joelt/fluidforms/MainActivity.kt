package io.joelt.fluidforms

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
import io.joelt.fluidforms.database.LocalPreferences
import io.joelt.fluidforms.database.UserPreferences
import io.joelt.fluidforms.database.UserPreferencesRepository
import io.joelt.fluidforms.navigation.Route
import io.joelt.fluidforms.navigation.ScaffoldOptions
import io.joelt.fluidforms.ui.components.SystemBarScaffold
import io.joelt.fluidforms.ui.screens.BottomNavBar
import io.joelt.fluidforms.ui.screens.TopNavBar
import io.joelt.fluidforms.ui.screens.archived.ArchivedScreen
import io.joelt.fluidforms.ui.screens.archived.archived
import io.joelt.fluidforms.ui.screens.archived_preview.ArchivedPreviewScreen
import io.joelt.fluidforms.ui.screens.draft_edit.DraftEditScreen
import io.joelt.fluidforms.ui.screens.drafts.DraftsScreen
import io.joelt.fluidforms.ui.screens.drafts.drafts
import io.joelt.fluidforms.ui.screens.settings.SettingsScreen
import io.joelt.fluidforms.ui.screens.form_edit.FormEditScreen
import io.joelt.fluidforms.ui.screens.form_preview.FormPreviewScreen
import io.joelt.fluidforms.ui.screens.forms.FormsScreen
import io.joelt.fluidforms.ui.screens.forms.forms
import io.joelt.fluidforms.ui.theme.FluidFormsTheme
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
                FormsScreen,
                FormPreviewScreen,
                FormEditScreen,
                DraftsScreen,
                ArchivedScreen,
                SettingsScreen,
                FormsScreen,
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
                FluidFormsTheme {
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
                                            Route.forms,
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
