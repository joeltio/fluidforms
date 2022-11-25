package io.joelt.texttemplate.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import io.joelt.texttemplate.R
import io.joelt.texttemplate.database.LocalPreferences
import io.joelt.texttemplate.database.ThemeColor
import io.joelt.texttemplate.database.UserPreferencesRepository
import io.joelt.texttemplate.navigation.*
import io.joelt.texttemplate.ui.components.ListSettingRow
import io.joelt.texttemplate.ui.theme.Typography
import kotlinx.coroutines.launch

val Route.settings: String
    get() = "settings"

class SettingsScreen(private val preferencesRepo: UserPreferencesRepository) : Screen {
    override val route: String = Route.settings
    override val arguments: List<NamedNavArgument> = listOf()

    @Composable
    override fun scaffold(nav: NavHostController) = ScaffoldOptions(
        topBar = { SettingsTopNavBar(nav) }
    )

    @Composable
    override fun Composable(backStackEntry: NavBackStackEntry, nav: NavHostController) {
        SettingsScreen(nav, preferencesRepo)
    }
}

@Composable
private fun SettingTitle(text: String) {
    Text(
        modifier = Modifier.padding(start = 16.dp),
        text = text,
        style = Typography.titleMedium
    )
}

@Composable
private fun SettingsScreen(nav: NavHostController, preferencesRepo: UserPreferencesRepository) {
    val scope = rememberCoroutineScope()
    Column {
        val currentSettings = LocalPreferences.current

        SettingTitle(text = stringResource(R.string.look_and_feel_title))
        ListSettingRow(
            title = stringResource(R.string.theme_color_title),
            subtitle = stringResource(R.string.theme_color_subtitle),
            options = stringArrayResource(R.array.theme_color_options),
            optionValues = stringArrayResource(R.array.theme_color_values),
            selectedOption = currentSettings.themeColor.name,
            onSelectedOptionChange = {
                scope.launch {
                    preferencesRepo.updateThemeColor(ThemeColor.valueOf(it))
                }
            }
        )
    }
}
