package io.github.casl0.mediastoreexplorer.data.repository.datasource

import android.content.ContentResolver
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import io.github.casl0.mediastoreexplorer.data.model.AudioItem
import io.github.casl0.mediastoreexplorer.data.repository.optIntCol
import io.github.casl0.mediastoreexplorer.data.repository.optIntColQ
import io.github.casl0.mediastoreexplorer.data.repository.optIntColR
import io.github.casl0.mediastoreexplorer.data.repository.optLongCol
import io.github.casl0.mediastoreexplorer.data.repository.optLongColR
import io.github.casl0.mediastoreexplorer.data.repository.optStringCol
import io.github.casl0.mediastoreexplorer.data.repository.optStringColQ
import io.github.casl0.mediastoreexplorer.data.repository.optStringColR
import javax.inject.Inject

class AudioMediaDataSource @Inject constructor(private val contentResolver: ContentResolver) {
    @Suppress("DEPRECATION")
    fun getAudios(): List<AudioItem> {
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
                projection(),
                null,
                null,
                "${MediaStore.Audio.Media.DATE_MODIFIED} DESC",
            )
            ?.use { cursor -> result.addAll(cursor.toItems()) }
        return result
    }

    @Suppress("DEPRECATION")
    private fun projection(): Array<String> =
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

    private fun Cursor.toItems(): List<AudioItem> =
        generateSequence { if (moveToNext()) toItem() else null }.toList()

    @Suppress("DEPRECATION")
    private fun Cursor.toItem(): AudioItem =
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
}
