package io.github.casl0.mediastoreexplorer.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import kotlinx.coroutines.launch

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
    val tabs =
        listOf(
            stringResource(R.string.tab_images),
            stringResource(R.string.tab_videos),
            stringResource(R.string.tab_audios),
            stringResource(R.string.tab_downloads),
            stringResource(R.string.tab_files),
        )
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

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
        Column(modifier = Modifier.padding(innerPadding)) {
            TabRow(selectedTabIndex = pagerState.currentPage) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        },
                        text = { Text(title) },
                    )
                }
            }

            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                when (page) {
                    0 -> ImagesScreen(viewModel = imagesViewModel)
                    1 -> VideosScreen(viewModel = videosViewModel)
                    2 -> AudiosScreen(viewModel = audiosViewModel)
                    3 -> DownloadsScreen(viewModel = downloadsViewModel)
                    4 -> FilesScreen(viewModel = filesViewModel)
                }
            }
        }
    }
}
