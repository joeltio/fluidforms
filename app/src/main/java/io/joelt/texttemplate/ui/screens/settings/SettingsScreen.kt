package io.joelt.texttemplate.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.joelt.texttemplate.R
import io.joelt.texttemplate.database.HourFormat
import io.joelt.texttemplate.database.LocalPreferences
import io.joelt.texttemplate.database.ThemeColor
import io.joelt.texttemplate.database.UserPreferencesRepository
import io.joelt.texttemplate.navigation.*
import io.joelt.texttemplate.ui.components.ListSettingRow
import io.joelt.texttemplate.ui.theme.Typography
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

val Route.settings: String
    get() = "settings"

@Composable
private fun SettingTitle(text: String) {
    Text(
        modifier = Modifier.padding(start = 16.dp),
        text = text,
        color = MaterialTheme.colorScheme.primary,
        style = Typography.titleMedium
    )
}

@Composable
private fun settingsScreenContent(
    nav: NavHostController,
    onUpdateThemeColor: (ThemeColor) -> Unit,
    onUpdateHourFormat: (HourFormat) -> Unit,
) = buildScreenContent {
    scaffoldOptions {
        topBar = { SettingsTopNavBar(nav) }
    }

    content {
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
                    onUpdateThemeColor(ThemeColor.valueOf(it))
                }
            )

            SettingTitle(text = stringResource(R.string.date_and_time_title))
            ListSettingRow(
                title = stringResource(R.string.hour_format_title),
                subtitle = stringResource(R.string.hour_format_subtitle),
                options = stringArrayResource(R.array.hour_format_options),
                optionValues = stringArrayResource(R.array.hour_format_values),
                selectedOption = currentSettings.hourFormat.name,
                onSelectedOptionChange = {
                    onUpdateHourFormat(HourFormat.valueOf(it))
                }
            )
        }
    }
}

val SettingsScreen = buildScreen {
    route = Route.settings
    arguments = emptyList()

    contentFactory { _, nav ->
        val scope = rememberCoroutineScope()
        val preferencesRepo: UserPreferencesRepository = get()
        settingsScreenContent(
            nav = nav,
            onUpdateThemeColor = { scope.launch { preferencesRepo.updateThemeColor(it) } },
            onUpdateHourFormat = { scope.launch { preferencesRepo.updateHourFormat(it) } })
    }
}
