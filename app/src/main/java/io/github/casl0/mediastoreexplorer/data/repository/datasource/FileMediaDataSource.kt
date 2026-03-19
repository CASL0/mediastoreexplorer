package io.github.casl0.mediastoreexplorer.data.repository.datasource

import android.content.ContentResolver
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import io.github.casl0.mediastoreexplorer.data.model.FileItem
import io.github.casl0.mediastoreexplorer.data.repository.optIntCol
import io.github.casl0.mediastoreexplorer.data.repository.optIntColQ
import io.github.casl0.mediastoreexplorer.data.repository.optIntColR
import io.github.casl0.mediastoreexplorer.data.repository.optLongCol
import io.github.casl0.mediastoreexplorer.data.repository.optLongColR
import io.github.casl0.mediastoreexplorer.data.repository.optStringCol
import io.github.casl0.mediastoreexplorer.data.repository.optStringColQ
import io.github.casl0.mediastoreexplorer.data.repository.optStringColR
import javax.inject.Inject

class FileMediaDataSource @Inject constructor(private val contentResolver: ContentResolver) {
    fun getFiles(): List<FileItem> {
        val queryUri =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Files.getContentUri("external")
            }
        val result = mutableListOf<FileItem>()
        contentResolver
            .query(
                queryUri,
                projection(),
                null,
                null,
                "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC",
            )
            ?.use { cursor -> result.addAll(cursor.toItems()) }
        return result
    }

    @Suppress("DEPRECATION")
    private fun projection(): Array<String> =
        buildList {
                add(MediaStore.Files.FileColumns._ID)
                add(MediaStore.Files.FileColumns.DISPLAY_NAME)
                add(MediaStore.Files.FileColumns.SIZE)
                add(MediaStore.Files.FileColumns.MIME_TYPE)
                add(MediaStore.Files.FileColumns.DATE_ADDED)
                add(MediaStore.Files.FileColumns.DATE_MODIFIED)
                add(MediaStore.Files.FileColumns.DATA)
                add(MediaStore.Files.FileColumns.MEDIA_TYPE)
                add(MediaStore.Files.FileColumns.TITLE)
                add(MediaStore.Files.FileColumns.PARENT)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    add(MediaStore.Files.FileColumns.RELATIVE_PATH)
                    add(MediaStore.Files.FileColumns.VOLUME_NAME)
                    add(MediaStore.Files.FileColumns.IS_PENDING)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    add(MediaStore.Files.FileColumns.IS_FAVORITE)
                    add(MediaStore.Files.FileColumns.IS_TRASHED)
                    add(MediaStore.Files.FileColumns.GENERATION_ADDED)
                    add(MediaStore.Files.FileColumns.GENERATION_MODIFIED)
                    add(MediaStore.Files.FileColumns.DOCUMENT_ID)
                    add(MediaStore.Files.FileColumns.ORIGINAL_DOCUMENT_ID)
                }
            }
            .toTypedArray()

    private fun Cursor.toItems(): List<FileItem> =
        generateSequence { if (moveToNext()) toItem() else null }.toList()

    @Suppress("DEPRECATION")
    private fun Cursor.toItem(): FileItem =
        FileItem(
            id = getLong(getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)),
            displayName = optStringCol(MediaStore.Files.FileColumns.DISPLAY_NAME),
            size = optLongCol(MediaStore.Files.FileColumns.SIZE),
            mimeType = optStringCol(MediaStore.Files.FileColumns.MIME_TYPE),
            dateAdded = optLongCol(MediaStore.Files.FileColumns.DATE_ADDED),
            dateModified = optLongCol(MediaStore.Files.FileColumns.DATE_MODIFIED),
            data = optStringCol(MediaStore.Files.FileColumns.DATA),
            mediaType = optIntCol(MediaStore.Files.FileColumns.MEDIA_TYPE),
            title = optStringCol(MediaStore.Files.FileColumns.TITLE),
            parent = optLongCol(MediaStore.Files.FileColumns.PARENT),
            relativePath = optStringColQ(MediaStore.Files.FileColumns.RELATIVE_PATH),
            volumeName = optStringColQ(MediaStore.Files.FileColumns.VOLUME_NAME),
            isPending = optIntColQ(MediaStore.Files.FileColumns.IS_PENDING),
            isFavorite = optIntColR(MediaStore.Files.FileColumns.IS_FAVORITE),
            isTrashed = optIntColR(MediaStore.Files.FileColumns.IS_TRASHED),
            generationAdded = optLongColR(MediaStore.Files.FileColumns.GENERATION_ADDED),
            generationModified = optLongColR(MediaStore.Files.FileColumns.GENERATION_MODIFIED),
            documentId = optStringColR(MediaStore.Files.FileColumns.DOCUMENT_ID),
            originalDocumentId = optStringColR(MediaStore.Files.FileColumns.ORIGINAL_DOCUMENT_ID),
        )
}
