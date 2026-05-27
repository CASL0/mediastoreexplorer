package io.github.casl0.mediastoreexplorer.ui.audios

import android.Manifest
import android.content.res.Configuration
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.casl0.mediastoreexplorer.R
import io.github.casl0.mediastoreexplorer.data.model.AudioItem
import io.github.casl0.mediastoreexplorer.ui.common.MediaTable
import io.github.casl0.mediastoreexplorer.ui.common.PermissionGate
import io.github.casl0.mediastoreexplorer.ui.common.PermissionRequiredScreen
import io.github.casl0.mediastoreexplorer.ui.common.TableColumn
import io.github.casl0.mediastoreexplorer.ui.common.formatBool
import io.github.casl0.mediastoreexplorer.ui.common.formatDateSec
import io.github.casl0.mediastoreexplorer.ui.common.formatDuration
import io.github.casl0.mediastoreexplorer.ui.common.formatInt
import io.github.casl0.mediastoreexplorer.ui.common.formatLong
import io.github.casl0.mediastoreexplorer.ui.common.formatSize
import io.github.casl0.mediastoreexplorer.ui.common.formatString
import io.github.casl0.mediastoreexplorer.ui.theme.MediaStoreExplorerTheme

/**
 * 端末内の音声ファイルをテーブル形式で表示する画面。
 *
 * 権限が未付与の場合は [PermissionGate] が [PermissionRequiredScreen] を表示し、 付与後に自動で音声を読み込む。
 * [initialPermissionsGranted] が指定された場合はプレビュー/テスト目的で PermissionGate を経由せず直接 [AudiosContent] を表示する。
 *
 * @param viewModel 音声データと UI 状態を管理する [AudiosViewModel]
 * @param modifier レイアウト調整用の [Modifier]
 * @param initialPermissionsGranted プレビュー/テスト用の権限状態オーバーライド
 */
@Composable
fun AudiosScreen(
    viewModel: AudiosViewModel,
    modifier: Modifier = Modifier,
    initialPermissionsGranted: Boolean? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (initialPermissionsGranted != null) {
        AudiosContent(
            uiState = uiState,
            permissionsGranted = initialPermissionsGranted,
            onRequestPermission = {},
            modifier = modifier,
        )
        return
    }
    PermissionGate(
        permissions = audiosRequiredPermissions(),
        message = stringResource(R.string.permission_audios_message),
        rationaleMessage = stringResource(R.string.permission_audios_rationale),
        onGranted = viewModel::loadAudios,
        modifier = modifier,
    ) {
        AudiosTable(uiState = uiState, modifier = modifier)
    }
}

@Composable
private fun AudiosContent(
    uiState: AudiosUiState,
    permissionsGranted: Boolean,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier,
    showRationale: Boolean = false,
) {
    if (!permissionsGranted) {
        PermissionRequiredScreen(
            message = stringResource(R.string.permission_audios_message),
            onRequestPermission = onRequestPermission,
            modifier = modifier,
            rationaleMessage = stringResource(R.string.permission_audios_rationale),
            showRationale = showRationale,
        )
    } else {
        AudiosTable(uiState = uiState, modifier = modifier)
    }
}

private fun audiosRequiredPermissions(): List<String> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.READ_MEDIA_AUDIO)
    } else {
        listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

@Composable
private fun AudiosTable(uiState: AudiosUiState, modifier: Modifier) {
    MediaTable(
        items = uiState.audios,
        columns = audioMediaColumns(),
        isLoading = uiState.isLoading,
        error = uiState.error,
        modifier = modifier,
        key = { it.id },
    )
}

@Composable
@Suppress("LongMethod") // MediaStore.Audio が公開する 33 カラムの宣言的リストのため分割しない
private fun audioMediaColumns(): List<TableColumn<AudioItem>> {
    val yes = stringResource(R.string.bool_yes)
    val no = stringResource(R.string.bool_no)
    return listOf(
        TableColumn(stringResource(R.string.col_id), 80.dp) { it.id.toString() },
        TableColumn(stringResource(R.string.col_display_name), 200.dp) {
            formatString(it.displayName)
        },
        TableColumn(stringResource(R.string.col_title), 200.dp) { formatString(it.title) },
        TableColumn(stringResource(R.string.col_size), 100.dp) { formatSize(it.size) },
        TableColumn(stringResource(R.string.col_mime_type), 160.dp) { formatString(it.mimeType) },
        TableColumn(stringResource(R.string.col_date_added), 160.dp) {
            formatDateSec(it.dateAdded)
        },
        TableColumn(stringResource(R.string.col_date_modified), 160.dp) {
            formatDateSec(it.dateModified)
        },
        TableColumn(stringResource(R.string.col_duration), 100.dp) { formatDuration(it.duration) },
        TableColumn(stringResource(R.string.col_artist), 160.dp) { formatString(it.artist) },
        TableColumn(stringResource(R.string.col_artist_id), 100.dp) { formatLong(it.artistId) },
        TableColumn(stringResource(R.string.col_album), 160.dp) { formatString(it.album) },
        TableColumn(stringResource(R.string.col_album_id), 100.dp) { formatLong(it.albumId) },
        TableColumn(stringResource(R.string.col_composer), 160.dp) { formatString(it.composer) },
        TableColumn(stringResource(R.string.col_track), 80.dp) { formatInt(it.track) },
        TableColumn(stringResource(R.string.col_year), 80.dp) { formatInt(it.year) },
        TableColumn(stringResource(R.string.col_bookmark), 120.dp) { formatLong(it.bookmark) },
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
        TableColumn(stringResource(R.string.col_bucket_id), 140.dp) { formatString(it.bucketId) },
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
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun AudiosScreenPermissionDeniedPreview() {
    MediaStoreExplorerTheme {
        AudiosContent(
            uiState = AudiosUiState(),
            permissionsGranted = false,
            onRequestPermission = {},
        )
    }
}

@Preview(showBackground = true, name = "Rationale")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, name = "Rationale (Dark)")
@Composable
private fun AudiosScreenRationalePreview() {
    MediaStoreExplorerTheme {
        AudiosContent(
            uiState = AudiosUiState(),
            permissionsGranted = false,
            onRequestPermission = {},
            showRationale = true,
        )
    }
}
