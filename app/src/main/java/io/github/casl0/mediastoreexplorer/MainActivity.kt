package io.github.casl0.mediastoreexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import io.github.casl0.mediastoreexplorer.ui.images.ImagesScreen
import io.github.casl0.mediastoreexplorer.ui.images.ImagesViewModel
import io.github.casl0.mediastoreexplorer.ui.theme.MediaStoreExplorerTheme
import io.github.casl0.mediastoreexplorer.ui.videos.VideosScreen
import io.github.casl0.mediastoreexplorer.ui.videos.VideosViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val imagesViewModel: ImagesViewModel by viewModels()
    private val videosViewModel: VideosViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MediaStoreExplorerTheme {
                MainScreen(
                    imagesViewModel = imagesViewModel,
                    videosViewModel = videosViewModel,
                )
            }
        }
    }
}

private val tabs = listOf("画像", "動画")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    imagesViewModel: ImagesViewModel,
    videosViewModel: VideosViewModel,
) {
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("MediaStore Explorer") })
        },
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

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                when (page) {
                    0 -> ImagesScreen(viewModel = imagesViewModel)
                    1 -> VideosScreen(viewModel = videosViewModel)
                }
            }
        }
    }
}
