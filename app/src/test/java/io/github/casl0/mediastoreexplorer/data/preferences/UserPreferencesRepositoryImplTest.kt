package io.github.casl0.mediastoreexplorer.data.preferences

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class UserPreferencesRepositoryImplTest {

    @get:Rule val tempFolder = TemporaryFolder()

    private val ioDispatcher = UnconfinedTestDispatcher()
    private val testScope = CoroutineScope(ioDispatcher + SupervisorJob())
    private lateinit var dataStoreFile: File
    private lateinit var repository: UserPreferencesRepositoryImpl

    @Before
    fun setUp() {
        dataStoreFile = tempFolder.newFile("user_preferences.preferences_pb")
        dataStoreFile.delete()
        val dataStore =
            PreferenceDataStoreFactory.create(scope = testScope, produceFile = { dataStoreFile })
        repository = UserPreferencesRepositoryImpl(dataStore, ioDispatcher)
    }

    @After
    fun tearDown() {
        testScope.cancel()
        if (dataStoreFile.exists()) dataStoreFile.delete()
    }

    // region 初期状態

    @Test
    fun 初期状態は_System_と_dynamicColor_true_と_appLanguage_null() = runTest {
        val state = repository.preferences.first()
        assertEquals(ThemeMode.System, state.themeMode)
        assertEquals(true, state.dynamicColor)
        assertNull(state.appLanguage)
    }

    // endregion

    // region setThemeMode

    @Test
    fun setThemeMode_Dark_を渡すと_themeMode_に保存される() = runTest {
        repository.setThemeMode(ThemeMode.Dark)
        assertEquals(ThemeMode.Dark, repository.preferences.first().themeMode)
    }

    // endregion

    // region setDynamicColor

    @Test
    fun setDynamicColor_false_を渡すと_dynamicColor_に保存される() = runTest {
        repository.setDynamicColor(false)
        assertEquals(false, repository.preferences.first().dynamicColor)
    }

    // endregion

    // region setAppLanguage

    @Test
    fun setAppLanguage_ja_を渡すと_appLanguage_に保存される() = runTest {
        repository.setAppLanguage("ja")
        assertEquals("ja", repository.preferences.first().appLanguage)
    }

    @Test
    fun setAppLanguage_null_を渡すと_appLanguage_がクリアされる() = runTest {
        repository.setAppLanguage("ja")
        repository.setAppLanguage(null)
        assertNull(repository.preferences.first().appLanguage)
    }

    @Test
    fun setAppLanguage_空文字を渡すと_appLanguage_がクリアされる() = runTest {
        repository.setAppLanguage("ja")
        repository.setAppLanguage("")
        assertNull(repository.preferences.first().appLanguage)
    }

    // endregion
}
