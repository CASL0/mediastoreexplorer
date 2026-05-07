package io.github.casl0.mediastoreexplorer.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import io.github.casl0.mediastoreexplorer.ui.audios.AudiosViewModel
import io.github.casl0.mediastoreexplorer.ui.downloads.DownloadsViewModel
import io.github.casl0.mediastoreexplorer.ui.files.FilesViewModel
import io.github.casl0.mediastoreexplorer.ui.images.ImagesViewModel
import io.github.casl0.mediastoreexplorer.ui.videos.VideosViewModel

/**
 * アプリ全体のトップレベル画面ルーティング。
 *
 * 既存のタブ画面 (`MainTabsScreen`) を `AppRoute.Main` に紐づけ、設定アイコンタップで `AppRoute.Settings` を push
 * する。Settings / Licenses 画面の中身は後続コミットで 実装するため、ここではプレースホルダー Composable を入れておく。
 */
@Composable
fun AppNavGraph(
    imagesViewModel: ImagesViewModel,
    videosViewModel: VideosViewModel,
    audiosViewModel: AudiosViewModel,
    downloadsViewModel: DownloadsViewModel,
    filesViewModel: FilesViewModel,
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
                entry<AppRoute.Settings> { Text("Settings (placeholder)") }
                entry<AppRoute.Licenses> { Text("Licenses (placeholder)") }
            },
    )
}
