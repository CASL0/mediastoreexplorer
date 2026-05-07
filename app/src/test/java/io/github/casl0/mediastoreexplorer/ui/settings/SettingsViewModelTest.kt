package io.github.casl0.mediastoreexplorer.ui.settings

import io.github.casl0.mediastoreexplorer.MainDispatcherRule
import io.github.casl0.mediastoreexplorer.data.preferences.ThemeMode
import io.github.casl0.mediastoreexplorer.data.preferences.UserPreferences
import io.github.casl0.mediastoreexplorer.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeUserPreferencesRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        fakeRepository = FakeUserPreferencesRepository()
        viewModel = SettingsViewModel(fakeRepository)
    }

    // region 初期状態

    @Test
    fun 初期状態は_Repository_が公開する_UserPreferences_と一致する() = runTest {
        val state = viewModel.uiState.first()
        assertEquals(UserPreferences(), state)
    }

    // endregion

    // region setThemeMode

    @Test
    fun setThemeMode_を呼ぶと_Repository_に書き込まれる() = runTest {
        viewModel.setThemeMode(ThemeMode.Dark)
        assertEquals(ThemeMode.Dark, fakeRepository.current.themeMode)
    }

    // endregion

    // region setDynamicColor

    @Test
    fun setDynamicColor_を呼ぶと_Repository_に書き込まれる() = runTest {
        viewModel.setDynamicColor(false)
        assertEquals(false, fakeRepository.current.dynamicColor)
    }

    // endregion

    // region setAppLanguage

    @Test
    fun setAppLanguage_を呼ぶと_Repository_に書き込まれる() = runTest {
        viewModel.setAppLanguage("ja")
        assertEquals("ja", fakeRepository.current.appLanguage)
    }

    @Test
    fun setAppLanguage_null_を渡すと_Repository_の値がクリアされる() = runTest {
        fakeRepository.update { it.copy(appLanguage = "ja") }
        viewModel.setAppLanguage(null)
        assertEquals(null, fakeRepository.current.appLanguage)
    }

    // endregion
}

private class FakeUserPreferencesRepository : UserPreferencesRepository {
    private val state = MutableStateFlow(UserPreferences())
    val current: UserPreferences
        get() = state.value

    override val preferences: Flow<UserPreferences> = state

    override suspend fun setThemeMode(mode: ThemeMode) {
        state.update { it.copy(themeMode = mode) }
    }

    override suspend fun setDynamicColor(enabled: Boolean) {
        state.update { it.copy(dynamicColor = enabled) }
    }

    override suspend fun setAppLanguage(languageTag: String?) {
        state.update { it.copy(appLanguage = languageTag?.takeIf(String::isNotBlank)) }
    }

    fun update(transform: (UserPreferences) -> UserPreferences) {
        state.update(transform)
    }
}
