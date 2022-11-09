package io.joelt.texttemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                NavHost(navController, startDestination = "templates") {
                    screens.forEach { screen ->
                        composable(screen.route(), arguments = screen.arguments()) {
                            screen.Composable(it, navController)
                        }
                    }
                }
            }
        }
    }
}
