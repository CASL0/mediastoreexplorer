package io.github.casl0.mediastoreexplorer.data.model

data class FileItem(
    val id: Long,
    val displayName: String?,
    val size: Long?,
    val mimeType: String?,
    /** seconds since Unix epoch */
    val dateAdded: Long?,
    /** seconds since Unix epoch */
    val dateModified: Long?,
    @Deprecated("Deprecated in API 29") val data: String?,
    /** 0=none, 1=image, 2=audio, 3=video, 4=playlist, 5=subtitle, 6=document */
    val mediaType: Int?,
    @Deprecated("Deprecated in API 30") val title: String?,
    /** parent directory ID */
    @Deprecated("Deprecated in API 30") val parent: Long?,
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
