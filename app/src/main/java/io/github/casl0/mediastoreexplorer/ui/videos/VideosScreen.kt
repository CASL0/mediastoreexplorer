package io.github.casl0.mediastoreexplorer.ui.videos

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import io.github.casl0.mediastoreexplorer.R
import io.github.casl0.mediastoreexplorer.data.model.VideoItem
import io.github.casl0.mediastoreexplorer.ui.common.MediaTable
import io.github.casl0.mediastoreexplorer.ui.common.PermissionRequiredScreen
import io.github.casl0.mediastoreexplorer.ui.common.TableColumn
import io.github.casl0.mediastoreexplorer.ui.common.formatBool
import io.github.casl0.mediastoreexplorer.ui.common.formatDateMs
import io.github.casl0.mediastoreexplorer.ui.common.formatDateSec
import io.github.casl0.mediastoreexplorer.ui.common.formatDouble
import io.github.casl0.mediastoreexplorer.ui.common.formatDuration
import io.github.casl0.mediastoreexplorer.ui.common.formatInt
import io.github.casl0.mediastoreexplorer.ui.common.formatLong
import io.github.casl0.mediastoreexplorer.ui.common.formatSize
import io.github.casl0.mediastoreexplorer.ui.common.formatString

/**
 * 端末内の動画をテーブル形式で表示する画面。
 *
 * 権限が未付与の場合は [PermissionRequiredScreen] を表示し、
 * 付与後に自動で動画を読み込む。
 *
 * @param viewModel 動画データと UI 状態を管理する [VideosViewModel]
 * @param modifier レイアウト調整用の [Modifier]
 */
@Composable
fun VideosScreen(
    viewModel: VideosViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_VIDEO)
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    var permissionsGranted by remember {
        mutableStateOf(
            requiredPermissions.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { results ->
        permissionsGranted = results.values.all { it }
    }

    LaunchedEffect(permissionsGranted) {
        if (permissionsGranted) {
            viewModel.loadVideos()
        }
    }

    if (!permissionsGranted) {
        PermissionRequiredScreen(
            message = stringResource(R.string.permission_videos_message),
            onRequestPermission = { permissionLauncher.launch(requiredPermissions) },
            modifier = modifier,
        )
    } else {
        val yes = stringResource(R.string.bool_yes)
        val no = stringResource(R.string.bool_no)
        val columns: List<TableColumn<VideoItem>> = listOf(
            TableColumn(
                header = stringResource(R.string.col_thumbnail),
                width = 72.dp,
                customContent = { item ->
                    val uri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, item.id
                    )
                    AsyncImage(
                        model = uri,
                        contentDescription = item.displayName,
                        modifier = Modifier.clip(RoundedCornerShape(4.dp)),
                        contentScale = ContentScale.Crop,
                    )
                },
            ),
            TableColumn(stringResource(R.string.col_id), 80.dp) { it.id.toString() },
            TableColumn(stringResource(R.string.col_display_name), 200.dp) { formatString(it.displayName) },
            TableColumn(stringResource(R.string.col_size), 100.dp) { formatSize(it.size) },
            TableColumn(stringResource(R.string.col_mime_type), 160.dp) { formatString(it.mimeType) },
            TableColumn(stringResource(R.string.col_date_added), 160.dp) { formatDateSec(it.dateAdded) },
            TableColumn(stringResource(R.string.col_date_modified), 160.dp) { formatDateSec(it.dateModified) },
            TableColumn(stringResource(R.string.col_date_taken), 160.dp) { formatDateMs(it.dateTaken) },
            TableColumn(stringResource(R.string.col_width), 80.dp) { formatInt(it.width) },
            TableColumn(stringResource(R.string.col_height), 80.dp) { formatInt(it.height) },
            TableColumn(stringResource(R.string.col_duration), 100.dp) { formatDuration(it.duration) },
            TableColumn(stringResource(R.string.col_resolution), 120.dp) { formatString(it.resolution) },
            TableColumn(stringResource(R.string.col_bucket_id), 140.dp) { formatString(it.bucketId) },
            TableColumn(stringResource(R.string.col_bucket_name), 180.dp) { formatString(it.bucketDisplayName) },
            TableColumn(stringResource(R.string.col_description), 200.dp) { formatString(it.description) },
            TableColumn(stringResource(R.string.col_category), 120.dp) { formatString(it.category) },
            TableColumn(stringResource(R.string.col_language), 80.dp) { formatString(it.language) },
            TableColumn(stringResource(R.string.col_artist), 160.dp) { formatString(it.artist) },
            TableColumn(stringResource(R.string.col_album), 160.dp) { formatString(it.album) },
            TableColumn(stringResource(R.string.col_tags), 160.dp) { formatString(it.tags) },
            TableColumn(stringResource(R.string.col_bookmark), 120.dp) { formatLong(it.bookmark) },
            TableColumn(stringResource(R.string.col_is_private), 80.dp) { formatBool(it.isPrivate, yes, no) },
            TableColumn(stringResource(R.string.col_latitude), 130.dp) { formatDouble(it.latitude) },
            TableColumn(stringResource(R.string.col_longitude), 130.dp) { formatDouble(it.longitude) },
            TableColumn(stringResource(R.string.col_data), 300.dp) { formatString(it.data) },
            TableColumn(stringResource(R.string.col_relative_path), 220.dp) { formatString(it.relativePath) },
            TableColumn(stringResource(R.string.col_volume_name), 140.dp) { formatString(it.volumeName) },
            TableColumn(stringResource(R.string.col_is_pending), 80.dp) { formatBool(it.isPending, yes, no) },
            TableColumn(stringResource(R.string.col_is_favorite), 100.dp) { formatBool(it.isFavorite, yes, no) },
            TableColumn(stringResource(R.string.col_is_trashed), 80.dp) { formatBool(it.isTrashed, yes, no) },
            TableColumn(stringResource(R.string.col_generation_added), 120.dp) { formatLong(it.generationAdded) },
            TableColumn(stringResource(R.string.col_generation_modified), 120.dp) { formatLong(it.generationModified) },
            TableColumn(stringResource(R.string.col_document_id), 220.dp) { formatString(it.documentId) },
            TableColumn(stringResource(R.string.col_original_document_id), 220.dp) { formatString(it.originalDocumentId) },
        )
        MediaTable(
            items = uiState.videos,
            columns = columns,
            isLoading = uiState.isLoading,
            error = uiState.error,
            modifier = modifier,
        )
    }
}
