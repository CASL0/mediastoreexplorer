package io.github.casl0.mediastoreexplorer.data.model

data class AudioItem(
    val id: Long,
    val displayName: String?,
    val size: Long?,
    val mimeType: String?,
    /** seconds since Unix epoch */
    val dateAdded: Long?,
    /** seconds since Unix epoch */
    val dateModified: Long?,
    val title: String?,
    val album: String?,
    val albumId: Long?,
    val artist: String?,
    val artistId: Long?,
    val composer: String?,
    /** track number within the album */
    val track: Int?,
    val year: Int?,
    /** milliseconds */
    val duration: Long?,
    /** milliseconds */
    val bookmark: Long?,
    val isMusic: Int?,
    val isPodcast: Int?,
    val isRingtone: Int?,
    val isAlarm: Int?,
    val isNotification: Int?,
    val bucketId: String?,
    val bucketDisplayName: String?,
    @Deprecated("Deprecated in API 29") val data: String?,
    /** API 29+ */
    val relativePath: String?,
    /** API 29+ */
    val volumeName: String?,
    /** API 29+: 1 if pending, 0 otherwise */
    val isPending: Int?,
    /** API 29+: 1 if audiobook, 0 otherwise */
    val isAudiobook: Int?,
    /** API 30+: 1 if favorite, 0 otherwise */
    val isFavorite: Int?,
    /** API 30+: 1 if trashed, 0 otherwise */
    val isTrashed: Int?,
    /** API 30+ */
    val generationAdded: Long?,
    /** API 30+ */
    val generationModified: Long?,
    /** API 30+ */
    val documentId: String?,
    /** API 30+ */
    val originalDocumentId: String?,
)
