package io.github.casl0.mediastoreexplorer.data.repository

import io.github.casl0.mediastoreexplorer.data.model.AudioItem
import io.github.casl0.mediastoreexplorer.data.model.DownloadItem
import io.github.casl0.mediastoreexplorer.data.model.ImageItem
import io.github.casl0.mediastoreexplorer.data.model.VideoItem

interface MediaRepository {
    /** MediaStore から端末内の画像一覧を取得する。 */
    suspend fun getImages(): List<ImageItem>

    /** MediaStore から端末内の動画一覧を取得する。 */
    suspend fun getVideos(): List<VideoItem>

    /** MediaStore から端末内の音声一覧を取得する。 */
    suspend fun getAudios(): List<AudioItem>

    /** MediaStore から端末内のダウンロードファイル一覧を取得する。API 29 未満は空リストを返す。 */
    suspend fun getDownloads(): List<DownloadItem>
}
