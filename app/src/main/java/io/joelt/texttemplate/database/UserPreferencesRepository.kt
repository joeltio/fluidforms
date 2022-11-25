package io.joelt.texttemplate.database

import androidx.compose.runtime.compositionLocalOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

enum class ThemeColor {
    SYSTEM,
    DARK,
    LIGHT
}

data class UserPreferences(val themeColor: ThemeColor = ThemeColor.SYSTEM)

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    private object PreferencesKeys {
        val THEME_COLOR = stringPreferencesKey("theme_color")
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

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        val themeColor = ThemeColor.valueOf(
            preferences[PreferencesKeys.THEME_COLOR] ?: ThemeColor.SYSTEM.name
        )

        return UserPreferences(themeColor)
    }
}

val LocalPreferences = compositionLocalOf { UserPreferences() }
