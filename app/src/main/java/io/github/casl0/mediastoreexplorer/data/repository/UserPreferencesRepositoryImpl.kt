package io.github.casl0.mediastoreexplorer.data.repository

import io.github.casl0.mediastoreexplorer.data.datasource.UserPreferencesDataSource
import io.github.casl0.mediastoreexplorer.data.model.ThemeMode
import io.github.casl0.mediastoreexplorer.data.model.UserPreferences
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class UserPreferencesRepositoryImpl
@Inject
constructor(private val dataSource: UserPreferencesDataSource) : UserPreferencesRepository {

    override val preferences: Flow<UserPreferences> = dataSource.preferences

    override suspend fun setThemeMode(mode: ThemeMode) = dataSource.setThemeMode(mode)

    override suspend fun setDynamicColor(enabled: Boolean) = dataSource.setDynamicColor(enabled)
}
