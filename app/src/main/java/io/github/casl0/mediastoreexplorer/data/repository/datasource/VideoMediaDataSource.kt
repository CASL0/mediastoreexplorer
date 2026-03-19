package io.github.casl0.mediastoreexplorer.data.repository.datasource

import android.content.ContentResolver
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import io.github.casl0.mediastoreexplorer.data.model.VideoItem
import io.github.casl0.mediastoreexplorer.data.repository.optDoubleCol
import io.github.casl0.mediastoreexplorer.data.repository.optIntCol
import io.github.casl0.mediastoreexplorer.data.repository.optIntColQ
import io.github.casl0.mediastoreexplorer.data.repository.optIntColR
import io.github.casl0.mediastoreexplorer.data.repository.optLongCol
import io.github.casl0.mediastoreexplorer.data.repository.optLongColR
import io.github.casl0.mediastoreexplorer.data.repository.optStringCol
import io.github.casl0.mediastoreexplorer.data.repository.optStringColQ
import io.github.casl0.mediastoreexplorer.data.repository.optStringColR
import javax.inject.Inject

class VideoMediaDataSource @Inject constructor(private val contentResolver: ContentResolver) {
    @Suppress("DEPRECATION")
    fun getVideos(): List<VideoItem> {
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
                projection(),
                null,
                null,
                "${MediaStore.Video.Media.DATE_MODIFIED} DESC",
            )
            ?.use { cursor -> result.addAll(cursor.toItems()) }
        return result
    }

    @Suppress("DEPRECATION")
    private fun projection(): Array<String> =
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

    private fun Cursor.toItems(): List<VideoItem> =
        generateSequence { if (moveToNext()) toItem() else null }.toList()

    @Suppress("DEPRECATION")
    private fun Cursor.toItem(): VideoItem =
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
}
