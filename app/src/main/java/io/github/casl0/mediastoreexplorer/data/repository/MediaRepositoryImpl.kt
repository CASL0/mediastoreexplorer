package io.github.casl0.mediastoreexplorer.data.repository

import io.github.casl0.mediastoreexplorer.data.model.AudioItem
import io.github.casl0.mediastoreexplorer.data.model.DownloadItem
import io.github.casl0.mediastoreexplorer.data.model.FileItem
import io.github.casl0.mediastoreexplorer.data.model.ImageItem
import io.github.casl0.mediastoreexplorer.data.model.VideoItem
import io.github.casl0.mediastoreexplorer.data.repository.datasource.AudioMediaDataSource
import io.github.casl0.mediastoreexplorer.data.repository.datasource.DownloadMediaDataSource
import io.github.casl0.mediastoreexplorer.data.repository.datasource.FileMediaDataSource
import io.github.casl0.mediastoreexplorer.data.repository.datasource.ImageMediaDataSource
import io.github.casl0.mediastoreexplorer.data.repository.datasource.VideoMediaDataSource
import io.github.casl0.mediastoreexplorer.di.IoDispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class MediaRepositoryImpl
@Inject
constructor(
    private val imageDataSource: ImageMediaDataSource,
    private val videoDataSource: VideoMediaDataSource,
    private val audioDataSource: AudioMediaDataSource,
    private val downloadDataSource: DownloadMediaDataSource,
    private val fileDataSource: FileMediaDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : MediaRepository {

    override suspend fun getImages(): List<ImageItem> =
        withContext(ioDispatcher) { imageDataSource.getImages() }

    override suspend fun getVideos(): List<VideoItem> =
        withContext(ioDispatcher) { videoDataSource.getVideos() }

    override suspend fun getAudios(): List<AudioItem> =
        withContext(ioDispatcher) { audioDataSource.getAudios() }

    override suspend fun getDownloads(): List<DownloadItem> =
        withContext(ioDispatcher) { downloadDataSource.getDownloads() }

    override suspend fun getFiles(): List<FileItem> =
        withContext(ioDispatcher) { fileDataSource.getFiles() }
}
