package io.github.casl0.mediastoreexplorer.data.model

data class ImageItem(
    val id: Long,
    val displayName: String?,
    val size: Long?,
    val mimeType: String?,
    /** seconds since Unix epoch */
    val dateAdded: Long?,
    /** seconds since Unix epoch */
    val dateModified: Long?,
    /** milliseconds since Unix epoch */
    val dateTaken: Long?,
    val width: Int?,
    val height: Int?,
    val orientation: Int?,
    val bucketId: String?,
    val bucketDisplayName: String?,
    val description: String?,
    @Deprecated("Deprecated in API 22") val isPrivate: Int?,
    @Deprecated("Deprecated in API 29") val latitude: Double?,
    @Deprecated("Deprecated in API 29") val longitude: Double?,
    @Deprecated("Deprecated in API 29") val data: String?,
    /** API 29+ */
    val relativePath: String?,
    /** API 29+ */
    val volumeName: String?,
    /** API 29+: 1 if pending, 0 otherwise */
    val isPending: Int?,
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
