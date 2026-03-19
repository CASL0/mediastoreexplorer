package io.github.casl0.mediastoreexplorer.data.repository.datasource

import android.content.ContentResolver
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import io.github.casl0.mediastoreexplorer.data.model.DownloadItem
import io.github.casl0.mediastoreexplorer.data.repository.optIntCol
import io.github.casl0.mediastoreexplorer.data.repository.optIntColR
import io.github.casl0.mediastoreexplorer.data.repository.optIntColU
import io.github.casl0.mediastoreexplorer.data.repository.optLongCol
import io.github.casl0.mediastoreexplorer.data.repository.optLongColR
import io.github.casl0.mediastoreexplorer.data.repository.optStringCol
import io.github.casl0.mediastoreexplorer.data.repository.optStringColR
import javax.inject.Inject

class DownloadMediaDataSource @Inject constructor(private val contentResolver: ContentResolver) {
    @Suppress("DEPRECATION")
    fun getDownloads(): List<DownloadItem> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return emptyList()
        val queryUri =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Downloads.EXTERNAL_CONTENT_URI
            }
        val result = mutableListOf<DownloadItem>()
        contentResolver
            .query(queryUri, projection(), null, null, "${MediaStore.Downloads.DATE_MODIFIED} DESC")
            ?.use { cursor -> result.addAll(cursor.toItems()) }
        return result
    }

    @Suppress("DEPRECATION", "InlinedApi")
    private fun projection(): Array<String> =
        buildList {
                add(MediaStore.Downloads._ID)
                add(MediaStore.Downloads.DISPLAY_NAME)
                add(MediaStore.Downloads.SIZE)
                add(MediaStore.Downloads.MIME_TYPE)
                add(MediaStore.Downloads.DATE_ADDED)
                add(MediaStore.Downloads.DATE_MODIFIED)
                add(MediaStore.Downloads.DATA)
                add(MediaStore.Downloads.RELATIVE_PATH)
                add(MediaStore.Downloads.VOLUME_NAME)
                add(MediaStore.Downloads.IS_PENDING)
                add(MediaStore.Downloads.DOWNLOAD_URI)
                add(MediaStore.Downloads.REFERER_URI)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    add(MediaStore.Downloads.IS_FAVORITE)
                    add(MediaStore.Downloads.IS_TRASHED)
                    add(MediaStore.Downloads.GENERATION_ADDED)
                    add(MediaStore.Downloads.GENERATION_MODIFIED)
                    add(MediaStore.Downloads.DOCUMENT_ID)
                    add(MediaStore.Downloads.ORIGINAL_DOCUMENT_ID)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    add(MediaStore.Downloads.IS_DRM)
                }
            }
            .toTypedArray()

    private fun Cursor.toItems(): List<DownloadItem> =
        generateSequence { if (moveToNext()) toItem() else null }.toList()

    @Suppress("DEPRECATION", "InlinedApi")
    private fun Cursor.toItem(): DownloadItem =
        DownloadItem(
            id = getLong(getColumnIndexOrThrow(MediaStore.Downloads._ID)),
            displayName = optStringCol(MediaStore.Downloads.DISPLAY_NAME),
            size = optLongCol(MediaStore.Downloads.SIZE),
            mimeType = optStringCol(MediaStore.Downloads.MIME_TYPE),
            dateAdded = optLongCol(MediaStore.Downloads.DATE_ADDED),
            dateModified = optLongCol(MediaStore.Downloads.DATE_MODIFIED),
            data = optStringCol(MediaStore.Downloads.DATA),
            relativePath = optStringCol(MediaStore.Downloads.RELATIVE_PATH),
            volumeName = optStringCol(MediaStore.Downloads.VOLUME_NAME),
            isPending = optIntCol(MediaStore.Downloads.IS_PENDING),
            downloadUri = optStringCol(MediaStore.Downloads.DOWNLOAD_URI),
            refererUri = optStringCol(MediaStore.Downloads.REFERER_URI),
            isFavorite = optIntColR(MediaStore.Downloads.IS_FAVORITE),
            isTrashed = optIntColR(MediaStore.Downloads.IS_TRASHED),
            generationAdded = optLongColR(MediaStore.Downloads.GENERATION_ADDED),
            generationModified = optLongColR(MediaStore.Downloads.GENERATION_MODIFIED),
            documentId = optStringColR(MediaStore.Downloads.DOCUMENT_ID),
            originalDocumentId = optStringColR(MediaStore.Downloads.ORIGINAL_DOCUMENT_ID),
            isDrm = optIntColU(MediaStore.Downloads.IS_DRM),
        )
}
