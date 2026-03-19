package io.github.casl0.mediastoreexplorer.data.repository.datasource

import android.content.ContentResolver
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import io.github.casl0.mediastoreexplorer.data.model.ImageItem
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

class ImageMediaDataSource @Inject constructor(private val contentResolver: ContentResolver) {
    @Suppress("DEPRECATION")
    fun getImages(): List<ImageItem> {
        val queryUri =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        val result = mutableListOf<ImageItem>()
        contentResolver
            .query(
                queryUri,
                projection(),
                null,
                null,
                "${MediaStore.Images.Media.DATE_MODIFIED} DESC",
            )
            ?.use { cursor -> result.addAll(cursor.toItems()) }
        return result
    }

    @Suppress("DEPRECATION")
    private fun projection(): Array<String> =
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

    private fun Cursor.toItems(): List<ImageItem> =
        generateSequence { if (moveToNext()) toItem() else null }.toList()

    @Suppress("DEPRECATION")
    private fun Cursor.toItem(): ImageItem =
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
}
