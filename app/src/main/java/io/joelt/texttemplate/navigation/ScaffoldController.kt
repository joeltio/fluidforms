package io.joelt.texttemplate.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import io.joelt.texttemplate.ui.components.*

enum class ScaffoldType {
    HOME_SCREEN,
    SETTINGS_SCREEN,
    TEMPLATE_EDIT_SCREEN,
    DRAFT_EDIT_SCREEN,
}

/**
 * The constructor of this class should not be called directly. Use
 * `scaffoldController()` instead.
 * @see scaffoldController()
 */
class ScaffoldController(
    private var scaffoldType: MutableState<ScaffoldType>
) {
    fun changeNavBars(type: ScaffoldType) {
        scaffoldType.value = type
    }

    @Composable
    fun Scaffold(
        nav: NavHostController, content: @Composable () -> Unit
    ) {
        // Create the top and bottom app bars
        var topBar: @Composable () -> Unit = {}
        var bottomBar: @Composable () -> Unit = {}
        when (scaffoldType.value) {
            ScaffoldType.HOME_SCREEN -> {
                topBar = { TopNavBar(nav) }
                bottomBar = { BottomNavBar(nav) }
            }
            ScaffoldType.SETTINGS_SCREEN -> {
                topBar = { SettingsTopNavBar(nav) }
            }
            ScaffoldType.TEMPLATE_EDIT_SCREEN -> {
                topBar = { TemplateEditTopNavBar(nav) }
            }
            ScaffoldType.DRAFT_EDIT_SCREEN -> {
                topBar = { DraftEditTopNavBar(nav) }
                bottomBar = { DraftEditBottomAppBar(nav) }
            }
        }

        Scaffold(topBar = topBar, bottomBar = bottomBar) {
            Box(modifier = Modifier.padding(it)) {
                content()
            }
        }
    }
}

@Composable
fun scaffoldController(): ScaffoldController {
    val scaffoldType = rememberSaveable { mutableStateOf(ScaffoldType.HOME_SCREEN) }
    return ScaffoldController(scaffoldType)
}
