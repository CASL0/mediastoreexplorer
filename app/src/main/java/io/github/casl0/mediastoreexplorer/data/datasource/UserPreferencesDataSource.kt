package io.github.casl0.mediastoreexplorer.data.datasource

import io.github.casl0.mediastoreexplorer.data.model.ThemeMode
import io.github.casl0.mediastoreexplorer.data.model.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * `UserPreferences` の永続化を担う DataSource。Repository から呼ばれる薄い抽象で、 実装は DataStore<Preferences>
 * をラップする。テストでは Fake 実装に差し替えることで Repository を実 IO なしで検証できる。
 */
interface UserPreferencesDataSource {
    val preferences: Flow<UserPreferences>

    suspend fun setThemeMode(mode: ThemeMode)

    suspend fun setDynamicColor(enabled: Boolean)
}
