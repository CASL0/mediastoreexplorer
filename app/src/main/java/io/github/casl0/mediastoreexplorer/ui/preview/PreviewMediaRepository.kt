package io.github.casl0.mediastoreexplorer.ui.preview

import io.github.casl0.mediastoreexplorer.data.model.AudioItem
import io.github.casl0.mediastoreexplorer.data.model.DownloadItem
import io.github.casl0.mediastoreexplorer.data.model.ImageItem
import io.github.casl0.mediastoreexplorer.data.model.VideoItem
import io.github.casl0.mediastoreexplorer.data.repository.MediaRepository

/**
 * Compose @Preview 専用の [MediaRepository] 実装。
 *
 * IO を一切行わず空リストを返す。リリースビルドでは R8 によってすべての @Preview 関数と ともに削除される。
 */
internal class PreviewMediaRepository : MediaRepository {
    override suspend fun getImages(): List<ImageItem> = emptyList()

    override suspend fun getVideos(): List<VideoItem> = emptyList()

    override suspend fun getAudios(): List<AudioItem> = emptyList()

    override suspend fun getDownloads(): List<DownloadItem> = emptyList()
}
