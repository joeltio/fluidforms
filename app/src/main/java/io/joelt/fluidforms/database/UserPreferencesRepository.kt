package io.joelt.fluidforms.database

import android.content.Context
import androidx.compose.runtime.compositionLocalOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

enum class ThemeColor {
    SYSTEM,
    DARK,
    LIGHT
}

enum class HourFormat {
    HOUR_12,
    HOUR_24
}

data class UserPreferences(
    val themeColor: ThemeColor = ThemeColor.SYSTEM,
    val hourFormat: HourFormat = HourFormat.HOUR_12
)

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    private object PreferencesKeys {
        val THEME_COLOR = stringPreferencesKey("theme_color")
        val HOUR_FORMAT = stringPreferencesKey("hour_format")
    }

    val flow: Flow<UserPreferences> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            mapUserPreferences(preferences)
        }

    suspend fun updateThemeColor(themeColor: ThemeColor) {
        dataStore.edit {
            it[PreferencesKeys.THEME_COLOR] = themeColor.name
        }
    }

    suspend fun updateHourFormat(hourFormat: HourFormat) {
        dataStore.edit {
            it[PreferencesKeys.HOUR_FORMAT] = hourFormat.name
        }
    }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        val themeColor = ThemeColor.valueOf(
            preferences[PreferencesKeys.THEME_COLOR] ?: ThemeColor.SYSTEM.name
        )
        val hourFormat = HourFormat.valueOf(
            preferences[PreferencesKeys.HOUR_FORMAT] ?: HourFormat.HOUR_12.name
        )

        return UserPreferences(themeColor, hourFormat)
    }
}

val LocalPreferences = compositionLocalOf { UserPreferences() }

val Context.dataStore by preferencesDataStore(
    name = "user_preferences"
)
