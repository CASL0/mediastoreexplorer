package io.github.casl0.mediastoreexplorer.data.repository

import android.content.ContentResolver
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import io.github.casl0.mediastoreexplorer.data.model.ImageItem
import io.github.casl0.mediastoreexplorer.data.model.VideoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver,
) : MediaRepository {

    @Suppress("DEPRECATION")
    override suspend fun getImages(): List<ImageItem> = withContext(Dispatchers.IO) {
        val projection = buildList {
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
        }.toTypedArray()

        val result = mutableListOf<ImageItem>()

        val queryUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        contentResolver.query(
            queryUri,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_MODIFIED} DESC",
        )?.use { cursor ->
            result.addAll(cursor.toImageItems())
        }

        result
    }

    @Suppress("DEPRECATION")
    private fun Cursor.toImageItems(): List<ImageItem> {
        val idCol = getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val displayNameCol = getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
        val sizeCol = getColumnIndex(MediaStore.Images.Media.SIZE)
        val mimeTypeCol = getColumnIndex(MediaStore.Images.Media.MIME_TYPE)
        val dateAddedCol = getColumnIndex(MediaStore.Images.Media.DATE_ADDED)
        val dateModifiedCol = getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)
        val dateTakenCol = getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)
        val widthCol = getColumnIndex(MediaStore.Images.Media.WIDTH)
        val heightCol = getColumnIndex(MediaStore.Images.Media.HEIGHT)
        val orientationCol = getColumnIndex(MediaStore.Images.Media.ORIENTATION)
        val bucketIdCol = getColumnIndex(MediaStore.Images.Media.BUCKET_ID)
        val bucketNameCol = getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        val descriptionCol = getColumnIndex(MediaStore.Images.Media.DESCRIPTION)
        val isPrivateCol = getColumnIndex(MediaStore.Images.Media.IS_PRIVATE)
        val latitudeCol = getColumnIndex(MediaStore.Images.Media.LATITUDE)
        val longitudeCol = getColumnIndex(MediaStore.Images.Media.LONGITUDE)
        val dataCol = getColumnIndex(MediaStore.Images.Media.DATA)
        val relativePathCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            getColumnIndex(MediaStore.Images.Media.RELATIVE_PATH) else -1
        val volumeNameCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            getColumnIndex(MediaStore.Images.Media.VOLUME_NAME) else -1
        val isPendingCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            getColumnIndex(MediaStore.Images.Media.IS_PENDING) else -1
        val isFavoriteCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            getColumnIndex(MediaStore.Images.Media.IS_FAVORITE) else -1
        val isTrashedCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            getColumnIndex(MediaStore.Images.Media.IS_TRASHED) else -1
        val generationAddedCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            getColumnIndex(MediaStore.Images.Media.GENERATION_ADDED) else -1
        val generationModifiedCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            getColumnIndex(MediaStore.Images.Media.GENERATION_MODIFIED) else -1
        val documentIdCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            getColumnIndex(MediaStore.Images.Media.DOCUMENT_ID) else -1
        val originalDocumentIdCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            getColumnIndex(MediaStore.Images.Media.ORIGINAL_DOCUMENT_ID) else -1

        val items = mutableListOf<ImageItem>()
        while (moveToNext()) {
            items.add(
                ImageItem(
                    id = getLong(idCol),
                    displayName = displayNameCol.takeIf { it >= 0 }?.let { getString(it) },
                    size = sizeCol.takeIf { it >= 0 }?.let { getLong(it) },
                    mimeType = mimeTypeCol.takeIf { it >= 0 }?.let { getString(it) },
                    dateAdded = dateAddedCol.takeIf { it >= 0 }?.let { getLong(it) },
                    dateModified = dateModifiedCol.takeIf { it >= 0 }?.let { getLong(it) },
                    dateTaken = dateTakenCol.takeIf { it >= 0 }?.let { getLong(it) },
                    width = widthCol.takeIf { it >= 0 }?.let { getInt(it) },
                    height = heightCol.takeIf { it >= 0 }?.let { getInt(it) },
                    orientation = orientationCol.takeIf { it >= 0 }?.let { getInt(it) },
                    bucketId = bucketIdCol.takeIf { it >= 0 }?.let { getString(it) },
                    bucketDisplayName = bucketNameCol.takeIf { it >= 0 }?.let { getString(it) },
                    description = descriptionCol.takeIf { it >= 0 }?.let { getString(it) },
                    isPrivate = isPrivateCol.takeIf { it >= 0 }?.let { getInt(it) },
                    latitude = latitudeCol.takeIf { it >= 0 }?.let { getDouble(it) },
                    longitude = longitudeCol.takeIf { it >= 0 }?.let { getDouble(it) },
                    data = dataCol.takeIf { it >= 0 }?.let { getString(it) },
                    relativePath = relativePathCol.takeIf { it >= 0 }?.let { getString(it) },
                    volumeName = volumeNameCol.takeIf { it >= 0 }?.let { getString(it) },
                    isPending = isPendingCol.takeIf { it >= 0 }?.let { getInt(it) },
                    isFavorite = isFavoriteCol.takeIf { it >= 0 }?.let { getInt(it) },
                    isTrashed = isTrashedCol.takeIf { it >= 0 }?.let { getInt(it) },
                    generationAdded = generationAddedCol.takeIf { it >= 0 }?.let { getLong(it) },
                    generationModified = generationModifiedCol.takeIf { it >= 0 }?.let { getLong(it) },
                    documentId = documentIdCol.takeIf { it >= 0 }?.let { getString(it) },
                    originalDocumentId = originalDocumentIdCol.takeIf { it >= 0 }?.let { getString(it) },
                )
            )
        }
        items
    }

    @Suppress("DEPRECATION")
    override suspend fun getVideos(): List<VideoItem> = withContext(Dispatchers.IO) {
        val projection = buildList {
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
        }.toTypedArray()

        val result = mutableListOf<VideoItem>()

        val queryUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        contentResolver.query(
            queryUri,
            projection,
            null,
            null,
            "${MediaStore.Video.Media.DATE_MODIFIED} DESC",
        )?.use { cursor ->
            result.addAll(cursor.toVideoItems())
        }

        result
    }

    @Suppress("DEPRECATION")
    private fun Cursor.toVideoItems(): List<VideoItem> {
        val idCol = getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        val displayNameCol = getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)
        val sizeCol = getColumnIndex(MediaStore.Video.Media.SIZE)
        val mimeTypeCol = getColumnIndex(MediaStore.Video.Media.MIME_TYPE)
        val dateAddedCol = getColumnIndex(MediaStore.Video.Media.DATE_ADDED)
        val dateModifiedCol = getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED)
        val dateTakenCol = getColumnIndex(MediaStore.Video.Media.DATE_TAKEN)
        val widthCol = getColumnIndex(MediaStore.Video.Media.WIDTH)
        val heightCol = getColumnIndex(MediaStore.Video.Media.HEIGHT)
        val bucketIdCol = getColumnIndex(MediaStore.Video.Media.BUCKET_ID)
        val bucketNameCol = getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
        val descriptionCol = getColumnIndex(MediaStore.Video.Media.DESCRIPTION)
        val categoryCol = getColumnIndex(MediaStore.Video.Media.CATEGORY)
        val languageCol = getColumnIndex(MediaStore.Video.Media.LANGUAGE)
        val artistCol = getColumnIndex(MediaStore.Video.Media.ARTIST)
        val albumCol = getColumnIndex(MediaStore.Video.Media.ALBUM)
        val tagsCol = getColumnIndex(MediaStore.Video.Media.TAGS)
        val durationCol = getColumnIndex(MediaStore.Video.Media.DURATION)
        val resolutionCol = getColumnIndex(MediaStore.Video.Media.RESOLUTION)
        val bookmarkCol = getColumnIndex(MediaStore.Video.Media.BOOKMARK)
        val isPrivateCol = getColumnIndex(MediaStore.Video.Media.IS_PRIVATE)
        val latitudeCol = getColumnIndex(MediaStore.Video.Media.LATITUDE)
        val longitudeCol = getColumnIndex(MediaStore.Video.Media.LONGITUDE)
        val dataCol = getColumnIndex(MediaStore.Video.Media.DATA)
        val relativePathCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            getColumnIndex(MediaStore.Video.Media.RELATIVE_PATH) else -1
        val volumeNameCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            getColumnIndex(MediaStore.Video.Media.VOLUME_NAME) else -1
        val isPendingCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            getColumnIndex(MediaStore.Video.Media.IS_PENDING) else -1
        val isFavoriteCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            getColumnIndex(MediaStore.Video.Media.IS_FAVORITE) else -1
        val isTrashedCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            getColumnIndex(MediaStore.Video.Media.IS_TRASHED) else -1
        val generationAddedCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            getColumnIndex(MediaStore.Video.Media.GENERATION_ADDED) else -1
        val generationModifiedCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            getColumnIndex(MediaStore.Video.Media.GENERATION_MODIFIED) else -1
        val documentIdCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            getColumnIndex(MediaStore.Video.Media.DOCUMENT_ID) else -1
        val originalDocumentIdCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            getColumnIndex(MediaStore.Video.Media.ORIGINAL_DOCUMENT_ID) else -1

        val items = mutableListOf<VideoItem>()
        while (moveToNext()) {
            items.add(
                VideoItem(
                    id = getLong(idCol),
                    displayName = displayNameCol.takeIf { it >= 0 }?.let { getString(it) },
                    size = sizeCol.takeIf { it >= 0 }?.let { getLong(it) },
                    mimeType = mimeTypeCol.takeIf { it >= 0 }?.let { getString(it) },
                    dateAdded = dateAddedCol.takeIf { it >= 0 }?.let { getLong(it) },
                    dateModified = dateModifiedCol.takeIf { it >= 0 }?.let { getLong(it) },
                    dateTaken = dateTakenCol.takeIf { it >= 0 }?.let { getLong(it) },
                    width = widthCol.takeIf { it >= 0 }?.let { getInt(it) },
                    height = heightCol.takeIf { it >= 0 }?.let { getInt(it) },
                    bucketId = bucketIdCol.takeIf { it >= 0 }?.let { getString(it) },
                    bucketDisplayName = bucketNameCol.takeIf { it >= 0 }?.let { getString(it) },
                    description = descriptionCol.takeIf { it >= 0 }?.let { getString(it) },
                    category = categoryCol.takeIf { it >= 0 }?.let { getString(it) },
                    language = languageCol.takeIf { it >= 0 }?.let { getString(it) },
                    artist = artistCol.takeIf { it >= 0 }?.let { getString(it) },
                    album = albumCol.takeIf { it >= 0 }?.let { getString(it) },
                    tags = tagsCol.takeIf { it >= 0 }?.let { getString(it) },
                    duration = durationCol.takeIf { it >= 0 }?.let { getLong(it) },
                    resolution = resolutionCol.takeIf { it >= 0 }?.let { getString(it) },
                    bookmark = bookmarkCol.takeIf { it >= 0 }?.let { getLong(it) },
                    isPrivate = isPrivateCol.takeIf { it >= 0 }?.let { getInt(it) },
                    latitude = latitudeCol.takeIf { it >= 0 }?.let { getDouble(it) },
                    longitude = longitudeCol.takeIf { it >= 0 }?.let { getDouble(it) },
                    data = dataCol.takeIf { it >= 0 }?.let { getString(it) },
                    relativePath = relativePathCol.takeIf { it >= 0 }?.let { getString(it) },
                    volumeName = volumeNameCol.takeIf { it >= 0 }?.let { getString(it) },
                    isPending = isPendingCol.takeIf { it >= 0 }?.let { getInt(it) },
                    isFavorite = isFavoriteCol.takeIf { it >= 0 }?.let { getInt(it) },
                    isTrashed = isTrashedCol.takeIf { it >= 0 }?.let { getInt(it) },
                    generationAdded = generationAddedCol.takeIf { it >= 0 }?.let { getLong(it) },
                    generationModified = generationModifiedCol.takeIf { it >= 0 }?.let { getLong(it) },
                    documentId = documentIdCol.takeIf { it >= 0 }?.let { getString(it) },
                    originalDocumentId = originalDocumentIdCol.takeIf { it >= 0 }?.let { getString(it) },
                )
            )
        }
        items
    }
}
