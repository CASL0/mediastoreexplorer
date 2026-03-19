package io.github.casl0.mediastoreexplorer.data.model

data class DownloadItem(
    val id: Long,
    val displayName: String?,
    val size: Long?,
    val mimeType: String?,
    /** seconds since Unix epoch */
    val dateAdded: Long?,
    /** seconds since Unix epoch */
    val dateModified: Long?,
    @Deprecated("Deprecated in API 29") val data: String?,
    /** API 29+ */
    val relativePath: String?,
    /** API 29+ */
    val volumeName: String?,
    /** API 29+: 1 if pending, 0 otherwise */
    val isPending: Int?,
    /** API 29+: URI of the original downloadable resource */
    val downloadUri: String?,
    /** API 29+: URI from which the resource was downloaded */
    val refererUri: String?,
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
    /** API 34+: 1 if DRM-protected, 0 otherwise */
    val isDrm: Int?,
)
