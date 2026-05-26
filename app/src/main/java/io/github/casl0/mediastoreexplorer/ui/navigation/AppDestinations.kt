package io.github.casl0.mediastoreexplorer.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.casl0.mediastoreexplorer.R

enum class AppDestinations(
    @StringRes val label: Int,
    val icon: ImageVector,
    @StringRes val contentDescription: Int,
) {
    IMAGES(R.string.tab_images, Icons.Filled.PhotoLibrary, R.string.tab_images),
    VIDEOS(R.string.tab_videos, Icons.Filled.VideoLibrary, R.string.tab_videos),
    AUDIOS(R.string.tab_audios, Icons.Filled.LibraryMusic, R.string.tab_audios),
    DOWNLOADS(R.string.tab_downloads, Icons.Filled.Download, R.string.tab_downloads),
    FILES(R.string.tab_files, Icons.Filled.Folder, R.string.tab_files),
}
