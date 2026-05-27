package io.github.casl0.mediastoreexplorer.ui.files

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
import io.github.casl0.mediastoreexplorer.data.model.FileItem
import io.github.casl0.mediastoreexplorer.ui.common.MediaTable
import io.github.casl0.mediastoreexplorer.ui.common.PermissionGate
import io.github.casl0.mediastoreexplorer.ui.common.PermissionRequiredScreen
import io.github.casl0.mediastoreexplorer.ui.common.TableColumn
import io.github.casl0.mediastoreexplorer.ui.common.formatBool
import io.github.casl0.mediastoreexplorer.ui.common.formatDateSec
import io.github.casl0.mediastoreexplorer.ui.common.formatLong
import io.github.casl0.mediastoreexplorer.ui.common.formatSize
import io.github.casl0.mediastoreexplorer.ui.common.formatString
import io.github.casl0.mediastoreexplorer.ui.theme.MediaStoreExplorerTheme

/**
 * 端末内の全ファイルをテーブル形式で表示する画面。
 *
 * 権限が未付与の場合は [PermissionGate] が [PermissionRequiredScreen] を表示し、 付与後に自動でファイル一覧を読み込む。 API 33+ では
 * READ_MEDIA_IMAGES / READ_MEDIA_VIDEO / READ_MEDIA_AUDIO を要求する。 [initialPermissionsGranted]
 * が指定された場合はプレビュー/テスト目的で PermissionGate を経由せず直接 [FilesContent] を表示する。
 *
 * @param viewModel ファイルデータと UI 状態を管理する [FilesViewModel]
 * @param modifier レイアウト調整用の [Modifier]
 * @param initialPermissionsGranted プレビュー/テスト用の権限状態オーバーライド
 */
@Composable
fun FilesScreen(
    viewModel: FilesViewModel,
    modifier: Modifier = Modifier,
    initialPermissionsGranted: Boolean? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (initialPermissionsGranted != null) {
        FilesContent(
            uiState = uiState,
            permissionsGranted = initialPermissionsGranted,
            onRequestPermission = {},
            modifier = modifier,
        )
        return
    }
    PermissionGate(
        permissions = filesRequiredPermissions(),
        message = stringResource(R.string.permission_files_message),
        rationaleMessage = stringResource(R.string.permission_files_rationale),
        onGranted = viewModel::loadFiles,
        modifier = modifier,
    ) {
        FilesTable(uiState = uiState, modifier = modifier)
    }
}

@Composable
private fun FilesContent(
    uiState: FilesUiState,
    permissionsGranted: Boolean,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier,
    showRationale: Boolean = false,
) {
    if (!permissionsGranted) {
        PermissionRequiredScreen(
            message = stringResource(R.string.permission_files_message),
            onRequestPermission = onRequestPermission,
            modifier = modifier,
            rationaleMessage = stringResource(R.string.permission_files_rationale),
            showRationale = showRationale,
        )
    } else {
        FilesTable(uiState = uiState, modifier = modifier)
    }
}

private fun filesRequiredPermissions(): List<String> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO,
        )
    } else {
        listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

@Composable
private fun FilesTable(uiState: FilesUiState, modifier: Modifier) {
    MediaTable(
        items = uiState.files,
        columns = fileMediaColumns(),
        isLoading = uiState.isLoading,
        error = uiState.error,
        modifier = modifier,
        key = { it.id },
    )
}

@Composable
@Suppress("LongMethod") // MediaStore.Files が公開する 19 カラムの宣言的リストのため分割しない
private fun fileMediaColumns(): List<TableColumn<FileItem>> {
    val yes = stringResource(R.string.bool_yes)
    val no = stringResource(R.string.bool_no)
    return listOf(
        TableColumn(stringResource(R.string.col_id), 80.dp) { it.id.toString() },
        TableColumn(stringResource(R.string.col_display_name), 200.dp) {
            formatString(it.displayName)
        },
        TableColumn(stringResource(R.string.col_size), 100.dp) { formatSize(it.size) },
        TableColumn(stringResource(R.string.col_mime_type), 160.dp) { formatString(it.mimeType) },
        TableColumn(stringResource(R.string.col_media_type), 100.dp) {
            it.mediaType?.toString() ?: "—"
        },
        TableColumn(stringResource(R.string.col_date_added), 160.dp) {
            formatDateSec(it.dateAdded)
        },
        TableColumn(stringResource(R.string.col_date_modified), 160.dp) {
            formatDateSec(it.dateModified)
        },
        TableColumn(stringResource(R.string.col_data), 300.dp) { formatString(it.data) },
        TableColumn(stringResource(R.string.col_title), 200.dp) { formatString(it.title) },
        TableColumn(stringResource(R.string.col_parent), 100.dp) { formatLong(it.parent) },
        TableColumn(stringResource(R.string.col_relative_path), 220.dp) {
            formatString(it.relativePath)
        },
        TableColumn(stringResource(R.string.col_volume_name), 140.dp) {
            formatString(it.volumeName)
        },
        TableColumn(stringResource(R.string.col_is_pending), 80.dp) {
            formatBool(it.isPending, yes, no)
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
private fun FilesScreenPermissionDeniedPreview() {
    MediaStoreExplorerTheme {
        FilesContent(uiState = FilesUiState(), permissionsGranted = false, onRequestPermission = {})
    }
}

@Preview(showBackground = true, name = "Rationale")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, name = "Rationale (Dark)")
@Composable
private fun FilesScreenRationalePreview() {
    MediaStoreExplorerTheme {
        FilesContent(
            uiState = FilesUiState(),
            permissionsGranted = false,
            onRequestPermission = {},
            showRationale = true,
        )
    }
}
