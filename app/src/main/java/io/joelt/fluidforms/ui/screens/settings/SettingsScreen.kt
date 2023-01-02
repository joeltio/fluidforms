package io.joelt.fluidforms.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.joelt.fluidforms.R
import io.joelt.fluidforms.database.HourFormat
import io.joelt.fluidforms.database.LocalPreferences
import io.joelt.fluidforms.database.ThemeColor
import io.joelt.fluidforms.database.UserPreferencesRepository
import io.joelt.fluidforms.navigation.*
import io.joelt.fluidforms.ui.components.ListSettingRow
import io.joelt.fluidforms.ui.theme.Typography
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
    onBack: () -> Unit,
    onUpdateThemeColor: (ThemeColor) -> Unit,
    onUpdateHourFormat: (HourFormat) -> Unit,
) = buildScreenContent {
    scaffoldOptions {
        topBar = { SettingsTopNavBar(onBack = onBack) }
    }

    content {
        Column {
            val currentSettings = LocalPreferences.current

            SettingTitle(text = stringResource(R.string.settings_look_and_feel_title))
            ListSettingRow(
                title = stringResource(R.string.settings_theme_color_title),
                subtitle = stringResource(R.string.settings_theme_color_subtitle),
                options = stringArrayResource(R.array.settings_theme_color_options),
                optionValues = stringArrayResource(R.array.settings_theme_color_values),
                selectedOption = currentSettings.themeColor.name,
                onSelectedOptionChange = {
                    onUpdateThemeColor(ThemeColor.valueOf(it))
                }
            )

            SettingTitle(text = stringResource(R.string.settings_date_and_time_title))
            ListSettingRow(
                title = stringResource(R.string.settings_hour_format_title),
                subtitle = stringResource(R.string.settings_hour_format_subtitle),
                options = stringArrayResource(R.array.settings_hour_format_options),
                optionValues = stringArrayResource(R.array.settings_hour_format_values),
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
            onBack = { nav.popBackStack() },
            onUpdateThemeColor = { scope.launch { preferencesRepo.updateThemeColor(it) } },
            onUpdateHourFormat = { scope.launch { preferencesRepo.updateHourFormat(it) } })
    }
}
