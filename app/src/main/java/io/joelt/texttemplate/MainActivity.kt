package io.joelt.texttemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.joelt.texttemplate.navigation.scaffoldController
import io.joelt.texttemplate.ui.screens.*
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
                    TemplateCreateScreen(),
                )

                val navController = rememberNavController()
                val scaffoldController = scaffoldController()
                scaffoldController.Scaffold(navController) {
                    NavHost(navController, startDestination = "templates") {
                        screens.forEach { screen ->
                            composable(screen.route(), arguments = screen.arguments()) {
                                screen.makeComposable(it)(
                                    navController, scaffoldController
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
