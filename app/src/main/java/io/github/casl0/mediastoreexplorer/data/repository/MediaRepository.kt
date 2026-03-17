package io.github.casl0.mediastoreexplorer.data.repository

import io.github.casl0.mediastoreexplorer.data.model.ImageItem
import io.github.casl0.mediastoreexplorer.data.model.VideoItem

interface MediaRepository {
    /** MediaStore から端末内の画像一覧を取得する。 */
    suspend fun getImages(): List<ImageItem>

    /** MediaStore から端末内の動画一覧を取得する。 */
    suspend fun getVideos(): List<VideoItem>
}
