package io.github.casl0.mediastoreexplorer.data.repository

import io.github.casl0.mediastoreexplorer.data.model.ImageItem
import io.github.casl0.mediastoreexplorer.data.model.VideoItem

interface MediaRepository {
    suspend fun getImages(): List<ImageItem>
    suspend fun getVideos(): List<VideoItem>
}
