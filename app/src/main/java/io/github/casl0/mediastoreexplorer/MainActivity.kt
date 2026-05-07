package io.github.casl0.mediastoreexplorer

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import io.github.casl0.mediastoreexplorer.ui.audios.AudiosViewModel
import io.github.casl0.mediastoreexplorer.ui.downloads.DownloadsViewModel
import io.github.casl0.mediastoreexplorer.ui.files.FilesViewModel
import io.github.casl0.mediastoreexplorer.ui.images.ImagesViewModel
import io.github.casl0.mediastoreexplorer.ui.navigation.AppNavGraph
import io.github.casl0.mediastoreexplorer.ui.settings.SettingsViewModel
import io.github.casl0.mediastoreexplorer.ui.theme.MediaStoreExplorerTheme
import io.github.casl0.mediastoreexplorer.ui.videos.VideosViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val imagesViewModel: ImagesViewModel by viewModels()
    private val videosViewModel: VideosViewModel by viewModels()
    private val audiosViewModel: AudiosViewModel by viewModels()
    private val downloadsViewModel: DownloadsViewModel by viewModels()
    private val filesViewModel: FilesViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        enableEdgeToEdge()
        setContent {
            val preferences by settingsViewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(preferences.appLanguage) { applyAppLanguage(preferences.appLanguage) }
            MediaStoreExplorerTheme(
                themeMode = preferences.themeMode,
                dynamicColor = preferences.dynamicColor,
            ) {
                AppNavGraph(
                    imagesViewModel = imagesViewModel,
                    videosViewModel = videosViewModel,
                    audiosViewModel = audiosViewModel,
                    downloadsViewModel = downloadsViewModel,
                    filesViewModel = filesViewModel,
                    settingsViewModel = settingsViewModel,
                )
            }
        }
    }

    private fun applyAppLanguage(languageTag: String?) {
        val target =
            if (languageTag.isNullOrBlank()) {
                LocaleListCompat.getEmptyLocaleList()
            } else {
                LocaleListCompat.forLanguageTags(languageTag)
            }
        if (AppCompatDelegate.getApplicationLocales() != target) {
            AppCompatDelegate.setApplicationLocales(target)
        }
    }
}
