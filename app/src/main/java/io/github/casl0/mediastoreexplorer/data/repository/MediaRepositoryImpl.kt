package io.github.casl0.mediastoreexplorer.data.repository

import android.content.ContentResolver
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import io.github.casl0.mediastoreexplorer.data.model.AudioItem
import io.github.casl0.mediastoreexplorer.data.model.ImageItem
import io.github.casl0.mediastoreexplorer.data.model.VideoItem
import io.github.casl0.mediastoreexplorer.di.IoDispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class MediaRepositoryImpl
@Inject
constructor(
    private val contentResolver: ContentResolver,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : MediaRepository {

    @Suppress("DEPRECATION")
    override suspend fun getImages(): List<ImageItem> =
        withContext(ioDispatcher) {
            val projection =
                buildList {
                        add(MediaStore.Images.Media._ID)
                        add(MediaStore.Images.Media.DISPLAY_NAME)
                        add(MediaStore.Images.Media.SIZE)
                        add(MediaStore.Images.Media.MIME_TYPE)
                        add(MediaStore.Images.Media.DATE_ADDED)
                        add(MediaStore.Images.Media.DATE_MODIFIED)
                        add(MediaStore.Images.Media.DATE_TAKEN)
                        add(MediaStore.Images.Media.WIDTH)
                        add(MediaStore.Images.Media.HEIGHT)
                        add(MediaStore.Images.Media.ORIENTATION)
                        add(MediaStore.Images.Media.BUCKET_ID)
                        add(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                        add(MediaStore.Images.Media.DESCRIPTION)
                        add(MediaStore.Images.Media.IS_PRIVATE)
                        add(MediaStore.Images.Media.LATITUDE)
                        add(MediaStore.Images.Media.LONGITUDE)
                        add(MediaStore.Images.Media.DATA)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            add(MediaStore.Images.Media.RELATIVE_PATH)
                            add(MediaStore.Images.Media.VOLUME_NAME)
                            add(MediaStore.Images.Media.IS_PENDING)
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            add(MediaStore.Images.Media.IS_FAVORITE)
                            add(MediaStore.Images.Media.IS_TRASHED)
                            add(MediaStore.Images.Media.GENERATION_ADDED)
                            add(MediaStore.Images.Media.GENERATION_MODIFIED)
                            add(MediaStore.Images.Media.DOCUMENT_ID)
                            add(MediaStore.Images.Media.ORIGINAL_DOCUMENT_ID)
                        }
                    }
                    .toTypedArray()

            val result = mutableListOf<ImageItem>()

            val queryUri =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

            contentResolver
                .query(
                    queryUri,
                    projection,
                    null,
                    null,
                    "${MediaStore.Images.Media.DATE_MODIFIED} DESC",
                )
                ?.use { cursor -> result.addAll(cursor.toImageItems()) }

            result
        }

    @Suppress("DEPRECATION")
    override suspend fun getVideos(): List<VideoItem> =
        withContext(ioDispatcher) {
            val queryUri =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                } else {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }
            val result = mutableListOf<VideoItem>()
            contentResolver
                .query(
                    queryUri,
                    videoProjection(),
                    null,
                    null,
                    "${MediaStore.Video.Media.DATE_MODIFIED} DESC",
                )
                ?.use { cursor -> result.addAll(cursor.toVideoItems()) }
            result
        }

    @Suppress("DEPRECATION")
    override suspend fun getAudios(): List<AudioItem> =
        withContext(ioDispatcher) {
            val queryUri =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                } else {
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
            val result = mutableListOf<AudioItem>()
            contentResolver
                .query(
                    queryUri,
                    audioProjection(),
                    null,
                    null,
                    "${MediaStore.Audio.Media.DATE_MODIFIED} DESC",
                )
                ?.use { cursor -> result.addAll(cursor.toAudioItems()) }
            result
        }
}

@Suppress("DEPRECATION")
private fun Cursor.toImageItems(): List<ImageItem> =
    generateSequence { if (moveToNext()) toImageItem() else null }.toList()

@Suppress("DEPRECATION")
private fun Cursor.toImageItem(): ImageItem =
    ImageItem(
        id = getLong(getColumnIndexOrThrow(MediaStore.Images.Media._ID)),
        displayName = optStringCol(MediaStore.Images.Media.DISPLAY_NAME),
        size = optLongCol(MediaStore.Images.Media.SIZE),
        mimeType = optStringCol(MediaStore.Images.Media.MIME_TYPE),
        dateAdded = optLongCol(MediaStore.Images.Media.DATE_ADDED),
        dateModified = optLongCol(MediaStore.Images.Media.DATE_MODIFIED),
        dateTaken = optLongCol(MediaStore.Images.Media.DATE_TAKEN),
        width = optIntCol(MediaStore.Images.Media.WIDTH),
        height = optIntCol(MediaStore.Images.Media.HEIGHT),
        orientation = optIntCol(MediaStore.Images.Media.ORIENTATION),
        bucketId = optStringCol(MediaStore.Images.Media.BUCKET_ID),
        bucketDisplayName = optStringCol(MediaStore.Images.Media.BUCKET_DISPLAY_NAME),
        description = optStringCol(MediaStore.Images.Media.DESCRIPTION),
        isPrivate = optIntCol(MediaStore.Images.Media.IS_PRIVATE),
        latitude = optDoubleCol(MediaStore.Images.Media.LATITUDE),
        longitude = optDoubleCol(MediaStore.Images.Media.LONGITUDE),
        data = optStringCol(MediaStore.Images.Media.DATA),
        relativePath = optStringColQ(MediaStore.Images.Media.RELATIVE_PATH),
        volumeName = optStringColQ(MediaStore.Images.Media.VOLUME_NAME),
        isPending = optIntColQ(MediaStore.Images.Media.IS_PENDING),
        isFavorite = optIntColR(MediaStore.Images.Media.IS_FAVORITE),
        isTrashed = optIntColR(MediaStore.Images.Media.IS_TRASHED),
        generationAdded = optLongColR(MediaStore.Images.Media.GENERATION_ADDED),
        generationModified = optLongColR(MediaStore.Images.Media.GENERATION_MODIFIED),
        documentId = optStringColR(MediaStore.Images.Media.DOCUMENT_ID),
        originalDocumentId = optStringColR(MediaStore.Images.Media.ORIGINAL_DOCUMENT_ID),
    )

@Suppress("DEPRECATION")
private fun Cursor.toVideoItems(): List<VideoItem> =
    generateSequence { if (moveToNext()) toVideoItem() else null }.toList()

@Suppress("DEPRECATION")
private fun Cursor.toVideoItem(): VideoItem =
    VideoItem(
        id = getLong(getColumnIndexOrThrow(MediaStore.Video.Media._ID)),
        displayName = optStringCol(MediaStore.Video.Media.DISPLAY_NAME),
        size = optLongCol(MediaStore.Video.Media.SIZE),
        mimeType = optStringCol(MediaStore.Video.Media.MIME_TYPE),
        dateAdded = optLongCol(MediaStore.Video.Media.DATE_ADDED),
        dateModified = optLongCol(MediaStore.Video.Media.DATE_MODIFIED),
        dateTaken = optLongCol(MediaStore.Video.Media.DATE_TAKEN),
        width = optIntCol(MediaStore.Video.Media.WIDTH),
        height = optIntCol(MediaStore.Video.Media.HEIGHT),
        bucketId = optStringCol(MediaStore.Video.Media.BUCKET_ID),
        bucketDisplayName = optStringCol(MediaStore.Video.Media.BUCKET_DISPLAY_NAME),
        description = optStringCol(MediaStore.Video.Media.DESCRIPTION),
        category = optStringCol(MediaStore.Video.Media.CATEGORY),
        language = optStringCol(MediaStore.Video.Media.LANGUAGE),
        artist = optStringCol(MediaStore.Video.Media.ARTIST),
        album = optStringCol(MediaStore.Video.Media.ALBUM),
        tags = optStringCol(MediaStore.Video.Media.TAGS),
        duration = optLongCol(MediaStore.Video.Media.DURATION),
        resolution = optStringCol(MediaStore.Video.Media.RESOLUTION),
        bookmark = optLongCol(MediaStore.Video.Media.BOOKMARK),
        isPrivate = optIntCol(MediaStore.Video.Media.IS_PRIVATE),
        latitude = optDoubleCol(MediaStore.Video.Media.LATITUDE),
        longitude = optDoubleCol(MediaStore.Video.Media.LONGITUDE),
        data = optStringCol(MediaStore.Video.Media.DATA),
        relativePath = optStringColQ(MediaStore.Video.Media.RELATIVE_PATH),
        volumeName = optStringColQ(MediaStore.Video.Media.VOLUME_NAME),
        isPending = optIntColQ(MediaStore.Video.Media.IS_PENDING),
        isFavorite = optIntColR(MediaStore.Video.Media.IS_FAVORITE),
        isTrashed = optIntColR(MediaStore.Video.Media.IS_TRASHED),
        generationAdded = optLongColR(MediaStore.Video.Media.GENERATION_ADDED),
        generationModified = optLongColR(MediaStore.Video.Media.GENERATION_MODIFIED),
        documentId = optStringColR(MediaStore.Video.Media.DOCUMENT_ID),
        originalDocumentId = optStringColR(MediaStore.Video.Media.ORIGINAL_DOCUMENT_ID),
    )

@Suppress("DEPRECATION")
private fun videoProjection(): Array<String> =
    buildList {
            add(MediaStore.Video.Media._ID)
            add(MediaStore.Video.Media.DISPLAY_NAME)
            add(MediaStore.Video.Media.SIZE)
            add(MediaStore.Video.Media.MIME_TYPE)
            add(MediaStore.Video.Media.DATE_ADDED)
            add(MediaStore.Video.Media.DATE_MODIFIED)
            add(MediaStore.Video.Media.DATE_TAKEN)
            add(MediaStore.Video.Media.WIDTH)
            add(MediaStore.Video.Media.HEIGHT)
            add(MediaStore.Video.Media.BUCKET_ID)
            add(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
            add(MediaStore.Video.Media.DESCRIPTION)
            add(MediaStore.Video.Media.CATEGORY)
            add(MediaStore.Video.Media.LANGUAGE)
            add(MediaStore.Video.Media.ARTIST)
            add(MediaStore.Video.Media.ALBUM)
            add(MediaStore.Video.Media.TAGS)
            add(MediaStore.Video.Media.DURATION)
            add(MediaStore.Video.Media.RESOLUTION)
            add(MediaStore.Video.Media.BOOKMARK)
            add(MediaStore.Video.Media.IS_PRIVATE)
            add(MediaStore.Video.Media.LATITUDE)
            add(MediaStore.Video.Media.LONGITUDE)
            add(MediaStore.Video.Media.DATA)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                add(MediaStore.Video.Media.RELATIVE_PATH)
                add(MediaStore.Video.Media.VOLUME_NAME)
                add(MediaStore.Video.Media.IS_PENDING)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                add(MediaStore.Video.Media.IS_FAVORITE)
                add(MediaStore.Video.Media.IS_TRASHED)
                add(MediaStore.Video.Media.GENERATION_ADDED)
                add(MediaStore.Video.Media.GENERATION_MODIFIED)
                add(MediaStore.Video.Media.DOCUMENT_ID)
                add(MediaStore.Video.Media.ORIGINAL_DOCUMENT_ID)
            }
        }
        .toTypedArray()

@Suppress("DEPRECATION")
private fun audioProjection(): Array<String> =
    buildList {
            add(MediaStore.Audio.Media._ID)
            add(MediaStore.Audio.Media.DISPLAY_NAME)
            add(MediaStore.Audio.Media.SIZE)
            add(MediaStore.Audio.Media.MIME_TYPE)
            add(MediaStore.Audio.Media.DATE_ADDED)
            add(MediaStore.Audio.Media.DATE_MODIFIED)
            add(MediaStore.Audio.Media.TITLE)
            add(MediaStore.Audio.Media.ALBUM)
            add(MediaStore.Audio.Media.ALBUM_ID)
            add(MediaStore.Audio.Media.ARTIST)
            add(MediaStore.Audio.Media.ARTIST_ID)
            add(MediaStore.Audio.Media.COMPOSER)
            add(MediaStore.Audio.Media.TRACK)
            add(MediaStore.Audio.Media.YEAR)
            add(MediaStore.Audio.Media.DURATION)
            add(MediaStore.Audio.Media.BOOKMARK)
            add(MediaStore.Audio.Media.IS_MUSIC)
            add(MediaStore.Audio.Media.IS_PODCAST)
            add(MediaStore.Audio.Media.IS_RINGTONE)
            add(MediaStore.Audio.Media.IS_ALARM)
            add(MediaStore.Audio.Media.IS_NOTIFICATION)
            add(MediaStore.Audio.Media.BUCKET_ID)
            add(MediaStore.Audio.Media.BUCKET_DISPLAY_NAME)
            add(MediaStore.Audio.Media.DATA)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                add(MediaStore.Audio.Media.RELATIVE_PATH)
                add(MediaStore.Audio.Media.VOLUME_NAME)
                add(MediaStore.Audio.Media.IS_PENDING)
                add(MediaStore.Audio.Media.IS_AUDIOBOOK)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                add(MediaStore.Audio.Media.IS_FAVORITE)
                add(MediaStore.Audio.Media.IS_TRASHED)
                add(MediaStore.Audio.Media.GENERATION_ADDED)
                add(MediaStore.Audio.Media.GENERATION_MODIFIED)
                add(MediaStore.Audio.Media.DOCUMENT_ID)
                add(MediaStore.Audio.Media.ORIGINAL_DOCUMENT_ID)
            }
        }
        .toTypedArray()

@Suppress("DEPRECATION")
private fun Cursor.toAudioItems(): List<AudioItem> =
    generateSequence { if (moveToNext()) toAudioItem() else null }.toList()

@Suppress("DEPRECATION")
private fun Cursor.toAudioItem(): AudioItem =
    AudioItem(
        id = getLong(getColumnIndexOrThrow(MediaStore.Audio.Media._ID)),
        displayName = optStringCol(MediaStore.Audio.Media.DISPLAY_NAME),
        size = optLongCol(MediaStore.Audio.Media.SIZE),
        mimeType = optStringCol(MediaStore.Audio.Media.MIME_TYPE),
        dateAdded = optLongCol(MediaStore.Audio.Media.DATE_ADDED),
        dateModified = optLongCol(MediaStore.Audio.Media.DATE_MODIFIED),
        title = optStringCol(MediaStore.Audio.Media.TITLE),
        album = optStringCol(MediaStore.Audio.Media.ALBUM),
        albumId = optLongCol(MediaStore.Audio.Media.ALBUM_ID),
        artist = optStringCol(MediaStore.Audio.Media.ARTIST),
        artistId = optLongCol(MediaStore.Audio.Media.ARTIST_ID),
        composer = optStringCol(MediaStore.Audio.Media.COMPOSER),
        track = optIntCol(MediaStore.Audio.Media.TRACK),
        year = optIntCol(MediaStore.Audio.Media.YEAR),
        duration = optLongCol(MediaStore.Audio.Media.DURATION),
        bookmark = optLongCol(MediaStore.Audio.Media.BOOKMARK),
        isMusic = optIntCol(MediaStore.Audio.Media.IS_MUSIC),
        isPodcast = optIntCol(MediaStore.Audio.Media.IS_PODCAST),
        isRingtone = optIntCol(MediaStore.Audio.Media.IS_RINGTONE),
        isAlarm = optIntCol(MediaStore.Audio.Media.IS_ALARM),
        isNotification = optIntCol(MediaStore.Audio.Media.IS_NOTIFICATION),
        bucketId = optStringCol(MediaStore.Audio.Media.BUCKET_ID),
        bucketDisplayName = optStringCol(MediaStore.Audio.Media.BUCKET_DISPLAY_NAME),
        data = optStringCol(MediaStore.Audio.Media.DATA),
        relativePath = optStringColQ(MediaStore.Audio.Media.RELATIVE_PATH),
        volumeName = optStringColQ(MediaStore.Audio.Media.VOLUME_NAME),
        isPending = optIntColQ(MediaStore.Audio.Media.IS_PENDING),
        isAudiobook = optIntColQ(MediaStore.Audio.Media.IS_AUDIOBOOK),
        isFavorite = optIntColR(MediaStore.Audio.Media.IS_FAVORITE),
        isTrashed = optIntColR(MediaStore.Audio.Media.IS_TRASHED),
        generationAdded = optLongColR(MediaStore.Audio.Media.GENERATION_ADDED),
        generationModified = optLongColR(MediaStore.Audio.Media.GENERATION_MODIFIED),
        documentId = optStringColR(MediaStore.Audio.Media.DOCUMENT_ID),
        originalDocumentId = optStringColR(MediaStore.Audio.Media.ORIGINAL_DOCUMENT_ID),
    )
