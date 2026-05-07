package io.github.casl0.mediastoreexplorer.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.casl0.mediastoreexplorer.di.IoDispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class UserPreferencesRepositoryImpl
@Inject
constructor(
    private val dataStore: DataStore<Preferences>,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : UserPreferencesRepository {

    override val preferences: Flow<UserPreferences> =
        dataStore.data.map { prefs ->
            UserPreferences(
                themeMode = prefs[KEY_THEME_MODE]?.toThemeModeOrNull() ?: ThemeMode.System,
                dynamicColor = prefs[KEY_DYNAMIC_COLOR] ?: true,
                appLanguage = prefs[KEY_APP_LANGUAGE]?.takeIf(String::isNotBlank),
            )
        }

    override suspend fun setThemeMode(mode: ThemeMode) {
        withContext(ioDispatcher) { dataStore.edit { it[KEY_THEME_MODE] = mode.name } }
    }

    override suspend fun setDynamicColor(enabled: Boolean) {
        withContext(ioDispatcher) { dataStore.edit { it[KEY_DYNAMIC_COLOR] = enabled } }
    }

    override suspend fun setAppLanguage(languageTag: String?) {
        withContext(ioDispatcher) {
            dataStore.edit { prefs ->
                if (languageTag.isNullOrBlank()) {
                    prefs.remove(KEY_APP_LANGUAGE)
                } else {
                    prefs[KEY_APP_LANGUAGE] = languageTag
                }
            }
        }
    }

    private companion object {
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        val KEY_DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val KEY_APP_LANGUAGE = stringPreferencesKey("app_language")

        fun String.toThemeModeOrNull(): ThemeMode? =
            ThemeMode.entries.firstOrNull { it.name == this }
    }
}
