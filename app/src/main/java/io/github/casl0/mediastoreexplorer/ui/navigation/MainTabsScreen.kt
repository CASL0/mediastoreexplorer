package io.github.casl0.mediastoreexplorer.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.window.core.layout.WindowSizeClass
import io.github.casl0.mediastoreexplorer.R
import io.github.casl0.mediastoreexplorer.ui.audios.AudiosScreen
import io.github.casl0.mediastoreexplorer.ui.audios.AudiosViewModel
import io.github.casl0.mediastoreexplorer.ui.downloads.DownloadsScreen
import io.github.casl0.mediastoreexplorer.ui.downloads.DownloadsViewModel
import io.github.casl0.mediastoreexplorer.ui.files.FilesScreen
import io.github.casl0.mediastoreexplorer.ui.files.FilesViewModel
import io.github.casl0.mediastoreexplorer.ui.images.ImagesScreen
import io.github.casl0.mediastoreexplorer.ui.images.ImagesViewModel
import io.github.casl0.mediastoreexplorer.ui.videos.VideosScreen
import io.github.casl0.mediastoreexplorer.ui.videos.VideosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("LongParameterList") // 5 種類のメディアタブ ViewModel と設定遷移コールバックを集約するため
fun MainTabsScreen(
    imagesViewModel: ImagesViewModel,
    videosViewModel: VideosViewModel,
    audiosViewModel: AudiosViewModel,
    downloadsViewModel: DownloadsViewModel,
    filesViewModel: FilesViewModel,
    onSettingsClick: () -> Unit,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    // expanded 幅 (>= 840dp) のときだけ label を表示。それ以外（NavigationBar / NavigationRail）はアイコンのみ
    val showLabel =
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.IMAGES) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    icon = {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = stringResource(destination.contentDescription),
                        )
                    },
                    label =
                        if (showLabel) {
                            { Text(stringResource(destination.label)) }
                        } else {
                            null
                        },
                    selected = destination == currentDestination,
                    onClick = { currentDestination = destination },
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    actions = {
                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = stringResource(R.string.action_settings),
                            )
                        }
                    },
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                when (currentDestination) {
                    AppDestinations.IMAGES -> ImagesScreen(viewModel = imagesViewModel)
                    AppDestinations.VIDEOS -> VideosScreen(viewModel = videosViewModel)
                    AppDestinations.AUDIOS -> AudiosScreen(viewModel = audiosViewModel)
                    AppDestinations.DOWNLOADS -> DownloadsScreen(viewModel = downloadsViewModel)
                    AppDestinations.FILES -> FilesScreen(viewModel = filesViewModel)
                }
            }
        }
    }
}
