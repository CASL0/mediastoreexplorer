package io.github.casl0.mediastoreexplorer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import io.github.casl0.mediastoreexplorer.ui.audios.AudiosViewModel
import io.github.casl0.mediastoreexplorer.ui.downloads.DownloadsViewModel
import io.github.casl0.mediastoreexplorer.ui.files.FilesViewModel
import io.github.casl0.mediastoreexplorer.ui.images.ImagesViewModel
import io.github.casl0.mediastoreexplorer.ui.licenses.LicensesScreen
import io.github.casl0.mediastoreexplorer.ui.settings.SettingsScreen
import io.github.casl0.mediastoreexplorer.ui.settings.SettingsViewModel
import io.github.casl0.mediastoreexplorer.ui.videos.VideosViewModel

/**
 * アプリ全体のトップレベル画面ルーティング。既存のタブ画面 (`MainTabsScreen`) を `AppRoute.Main` に紐づけ、設定アイコン タップで
 * `AppRoute.Settings` / `AppRoute.Licenses` を push する。
 */
@Composable
@Suppress("LongParameterList") // メディア種別ごとの ViewModel と設定 ViewModel を集約して受け取るため
fun AppNavGraph(
    imagesViewModel: ImagesViewModel,
    videosViewModel: VideosViewModel,
    audiosViewModel: AudiosViewModel,
    downloadsViewModel: DownloadsViewModel,
    filesViewModel: FilesViewModel,
    settingsViewModel: SettingsViewModel,
) {
    val backStack = rememberNavBackStack(AppRoute.Main)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider =
            entryProvider {
                entry<AppRoute.Main> {
                    MainTabsScreen(
                        imagesViewModel = imagesViewModel,
                        videosViewModel = videosViewModel,
                        audiosViewModel = audiosViewModel,
                        downloadsViewModel = downloadsViewModel,
                        filesViewModel = filesViewModel,
                        onSettingsClick = { backStack.add(AppRoute.Settings) },
                    )
                }
                entry<AppRoute.Settings> {
                    SettingsScreen(
                        viewModel = settingsViewModel,
                        onBackClick = { backStack.removeLastOrNull() },
                        onLicensesClick = { backStack.add(AppRoute.Licenses) },
                    )
                }
                entry<AppRoute.Licenses> {
                    LicensesScreen(onBackClick = { backStack.removeLastOrNull() })
                }
            },
    )
}
