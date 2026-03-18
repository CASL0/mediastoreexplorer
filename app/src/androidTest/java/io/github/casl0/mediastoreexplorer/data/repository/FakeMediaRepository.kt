package io.github.casl0.mediastoreexplorer.data.repository

import io.github.casl0.mediastoreexplorer.data.model.AudioItem
import io.github.casl0.mediastoreexplorer.data.model.ImageItem
import io.github.casl0.mediastoreexplorer.data.model.VideoItem

/** UI テスト用の [MediaRepository] 実装。戻り値と例外をフィールドで制御できる。 */
class FakeMediaRepository : MediaRepository {

    var images: List<ImageItem> = emptyList()
    var videos: List<VideoItem> = emptyList()
    var audios: List<AudioItem> = emptyList()
    var shouldThrow: Throwable? = null

    override suspend fun getImages(): List<ImageItem> {
        shouldThrow?.let { throw it }
        return images
    }

    override suspend fun getVideos(): List<VideoItem> {
        shouldThrow?.let { throw it }
        return videos
    }

    override suspend fun getAudios(): List<AudioItem> {
        shouldThrow?.let { throw it }
        return audios
    }
}
