package io.github.casl0.mediastoreexplorer.ui.navigation

import android.content.res.Configuration
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
import androidx.compose.ui.tooling.preview.Preview
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
import io.github.casl0.mediastoreexplorer.ui.theme.MediaStoreExplorerTheme
import io.github.casl0.mediastoreexplorer.ui.videos.VideosScreen
import io.github.casl0.mediastoreexplorer.ui.videos.VideosViewModel

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
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.IMAGES) }

    MainTabsScaffold(
        currentDestination = currentDestination,
        onDestinationChange = { currentDestination = it },
        onSettingsClick = onSettingsClick,
    ) {
        when (currentDestination) {
            AppDestinations.IMAGES -> ImagesScreen(viewModel = imagesViewModel)
            AppDestinations.VIDEOS -> VideosScreen(viewModel = videosViewModel)
            AppDestinations.AUDIOS -> AudiosScreen(viewModel = audiosViewModel)
            AppDestinations.DOWNLOADS -> DownloadsScreen(viewModel = downloadsViewModel)
            AppDestinations.FILES -> FilesScreen(viewModel = filesViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTabsScaffold(
    currentDestination: AppDestinations,
    onDestinationChange: (AppDestinations) -> Unit,
    onSettingsClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    // expanded 幅 (>= 840dp) のときだけ label を表示。それ以外（NavigationBar / NavigationRail）はアイコンのみ
    val showLabel =
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

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
                    onClick = { onDestinationChange(destination) },
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
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) { content() }
        }
    }
}

@Preview(name = "Compact (phone portrait)", showBackground = true, widthDp = 360, heightDp = 640)
@Preview(name = "Medium (tablet portrait)", showBackground = true, widthDp = 700, heightDp = 1000)
@Preview(
    name = "Expanded (tablet landscape)",
    showBackground = true,
    widthDp = 1000,
    heightDp = 700,
)
@Preview(
    name = "Expanded dark",
    showBackground = true,
    widthDp = 1000,
    heightDp = 700,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun MainTabsScaffoldPreview() {
    MediaStoreExplorerTheme {
        MainTabsScaffold(
            currentDestination = AppDestinations.IMAGES,
            onDestinationChange = {},
            onSettingsClick = {},
        ) {
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}
