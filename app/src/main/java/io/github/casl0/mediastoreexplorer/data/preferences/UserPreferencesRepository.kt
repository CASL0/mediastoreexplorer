package io.github.casl0.mediastoreexplorer.data.preferences

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val preferences: Flow<UserPreferences>

    suspend fun setThemeMode(mode: ThemeMode)

    suspend fun setDynamicColor(enabled: Boolean)

    /** `null` を渡すと端末設定追従に戻す。 */
    suspend fun setAppLanguage(languageTag: String?)
}
