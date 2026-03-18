package io.github.casl0.mediastoreexplorer.ui.audios

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.casl0.mediastoreexplorer.R
import io.github.casl0.mediastoreexplorer.data.model.AudioItem
import io.github.casl0.mediastoreexplorer.ui.common.MediaTable
import io.github.casl0.mediastoreexplorer.ui.common.PermissionRequiredScreen
import io.github.casl0.mediastoreexplorer.ui.common.TableColumn
import io.github.casl0.mediastoreexplorer.ui.common.formatBool
import io.github.casl0.mediastoreexplorer.ui.common.formatDateSec
import io.github.casl0.mediastoreexplorer.ui.common.formatDuration
import io.github.casl0.mediastoreexplorer.ui.common.formatInt
import io.github.casl0.mediastoreexplorer.ui.common.formatLong
import io.github.casl0.mediastoreexplorer.ui.common.formatSize
import io.github.casl0.mediastoreexplorer.ui.common.formatString

/**
 * 端末内の音声ファイルをテーブル形式で表示する画面。
 *
 * 権限が未付与の場合は [PermissionRequiredScreen] を表示し、 付与後に自動で音声を読み込む。
 *
 * @param viewModel 音声データと UI 状態を管理する [AudiosViewModel]
 * @param modifier レイアウト調整用の [Modifier]
 */
@Composable
fun AudiosScreen(
    viewModel: AudiosViewModel,
    modifier: Modifier = Modifier,
    initialPermissionsGranted: Boolean? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val requiredPermissions =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

    var permissionsGranted by remember {
        mutableStateOf(
            initialPermissionsGranted
                ?: requiredPermissions.all {
                    ContextCompat.checkSelfPermission(context, it) ==
                        PackageManager.PERMISSION_GRANTED
                }
        )
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            permissionsGranted = results.values.all { it }
        }

    LaunchedEffect(permissionsGranted) {
        if (permissionsGranted) {
            viewModel.loadAudios()
        }
    }

    if (!permissionsGranted) {
        PermissionRequiredScreen(
            message = stringResource(R.string.permission_audios_message),
            onRequestPermission = { permissionLauncher.launch(requiredPermissions) },
            modifier = modifier,
        )
    } else {
        val yes = stringResource(R.string.bool_yes)
        val no = stringResource(R.string.bool_no)
        val columns: List<TableColumn<AudioItem>> =
            listOf(
                TableColumn(stringResource(R.string.col_id), 80.dp) { it.id.toString() },
                TableColumn(stringResource(R.string.col_display_name), 200.dp) {
                    formatString(it.displayName)
                },
                TableColumn(stringResource(R.string.col_title), 200.dp) { formatString(it.title) },
                TableColumn(stringResource(R.string.col_size), 100.dp) { formatSize(it.size) },
                TableColumn(stringResource(R.string.col_mime_type), 160.dp) {
                    formatString(it.mimeType)
                },
                TableColumn(stringResource(R.string.col_date_added), 160.dp) {
                    formatDateSec(it.dateAdded)
                },
                TableColumn(stringResource(R.string.col_date_modified), 160.dp) {
                    formatDateSec(it.dateModified)
                },
                TableColumn(stringResource(R.string.col_duration), 100.dp) {
                    formatDuration(it.duration)
                },
                TableColumn(stringResource(R.string.col_artist), 160.dp) {
                    formatString(it.artist)
                },
                TableColumn(stringResource(R.string.col_artist_id), 100.dp) {
                    formatLong(it.artistId)
                },
                TableColumn(stringResource(R.string.col_album), 160.dp) { formatString(it.album) },
                TableColumn(stringResource(R.string.col_album_id), 100.dp) {
                    formatLong(it.albumId)
                },
                TableColumn(stringResource(R.string.col_composer), 160.dp) {
                    formatString(it.composer)
                },
                TableColumn(stringResource(R.string.col_track), 80.dp) { formatInt(it.track) },
                TableColumn(stringResource(R.string.col_year), 80.dp) { formatInt(it.year) },
                TableColumn(stringResource(R.string.col_bookmark), 120.dp) {
                    formatLong(it.bookmark)
                },
                TableColumn(stringResource(R.string.col_is_music), 80.dp) {
                    formatBool(it.isMusic, yes, no)
                },
                TableColumn(stringResource(R.string.col_is_podcast), 100.dp) {
                    formatBool(it.isPodcast, yes, no)
                },
                TableColumn(stringResource(R.string.col_is_ringtone), 100.dp) {
                    formatBool(it.isRingtone, yes, no)
                },
                TableColumn(stringResource(R.string.col_is_alarm), 80.dp) {
                    formatBool(it.isAlarm, yes, no)
                },
                TableColumn(stringResource(R.string.col_is_notification), 100.dp) {
                    formatBool(it.isNotification, yes, no)
                },
                TableColumn(stringResource(R.string.col_bucket_id), 140.dp) {
                    formatString(it.bucketId)
                },
                TableColumn(stringResource(R.string.col_bucket_name), 180.dp) {
                    formatString(it.bucketDisplayName)
                },
                TableColumn(stringResource(R.string.col_data), 300.dp) { formatString(it.data) },
                TableColumn(stringResource(R.string.col_relative_path), 220.dp) {
                    formatString(it.relativePath)
                },
                TableColumn(stringResource(R.string.col_volume_name), 140.dp) {
                    formatString(it.volumeName)
                },
                TableColumn(stringResource(R.string.col_is_pending), 80.dp) {
                    formatBool(it.isPending, yes, no)
                },
                TableColumn(stringResource(R.string.col_is_audiobook), 120.dp) {
                    formatBool(it.isAudiobook, yes, no)
                },
                TableColumn(stringResource(R.string.col_is_favorite), 100.dp) {
                    formatBool(it.isFavorite, yes, no)
                },
                TableColumn(stringResource(R.string.col_is_trashed), 80.dp) {
                    formatBool(it.isTrashed, yes, no)
                },
                TableColumn(stringResource(R.string.col_generation_added), 120.dp) {
                    formatLong(it.generationAdded)
                },
                TableColumn(stringResource(R.string.col_generation_modified), 120.dp) {
                    formatLong(it.generationModified)
                },
                TableColumn(stringResource(R.string.col_document_id), 220.dp) {
                    formatString(it.documentId)
                },
                TableColumn(stringResource(R.string.col_original_document_id), 220.dp) {
                    formatString(it.originalDocumentId)
                },
            )
        MediaTable(
            items = uiState.audios,
            columns = columns,
            isLoading = uiState.isLoading,
            error = uiState.error,
            modifier = modifier,
        )
    }
}
