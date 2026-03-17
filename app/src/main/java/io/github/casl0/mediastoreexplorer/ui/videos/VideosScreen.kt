package io.github.casl0.mediastoreexplorer.ui.videos

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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

private val videoColumns: List<TableColumn<VideoItem>> = listOf(
    TableColumn("ID", 80.dp) { it.id.toString() },
    TableColumn("表示名", 200.dp) { formatString(it.displayName) },
    TableColumn("サイズ", 100.dp) { formatSize(it.size) },
    TableColumn("MIMEタイプ", 160.dp) { formatString(it.mimeType) },
    TableColumn("追加日時", 160.dp) { formatDateSec(it.dateAdded) },
    TableColumn("更新日時", 160.dp) { formatDateSec(it.dateModified) },
    TableColumn("撮影日時", 160.dp) { formatDateMs(it.dateTaken) },
    TableColumn("幅 (px)", 80.dp) { formatInt(it.width) },
    TableColumn("高さ (px)", 80.dp) { formatInt(it.height) },
    TableColumn("再生時間", 100.dp) { formatDuration(it.duration) },
    TableColumn("解像度", 120.dp) { formatString(it.resolution) },
    TableColumn("バケットID", 140.dp) { formatString(it.bucketId) },
    TableColumn("バケット名", 180.dp) { formatString(it.bucketDisplayName) },
    TableColumn("説明", 200.dp) { formatString(it.description) },
    TableColumn("カテゴリ", 120.dp) { formatString(it.category) },
    TableColumn("言語", 80.dp) { formatString(it.language) },
    TableColumn("アーティスト", 160.dp) { formatString(it.artist) },
    TableColumn("アルバム", 160.dp) { formatString(it.album) },
    TableColumn("タグ", 160.dp) { formatString(it.tags) },
    TableColumn("しおり (ms)", 120.dp) { formatLong(it.bookmark) },
    TableColumn("非公開", 80.dp) { formatBool(it.isPrivate) },
    TableColumn("緯度 (非推奨)", 130.dp) { formatDouble(it.latitude) },
    TableColumn("経度 (非推奨)", 130.dp) { formatDouble(it.longitude) },
    TableColumn("パス (非推奨)", 300.dp) { formatString(it.data) },
    TableColumn("相対パス", 220.dp) { formatString(it.relativePath) },
    TableColumn("ボリューム名", 140.dp) { formatString(it.volumeName) },
    TableColumn("保留中", 80.dp) { formatBool(it.isPending) },
    TableColumn("お気に入り", 100.dp) { formatBool(it.isFavorite) },
    TableColumn("ゴミ箱", 80.dp) { formatBool(it.isTrashed) },
    TableColumn("世代 (追加)", 120.dp) { formatLong(it.generationAdded) },
    TableColumn("世代 (更新)", 120.dp) { formatLong(it.generationModified) },
    TableColumn("ドキュメントID", 220.dp) { formatString(it.documentId) },
    TableColumn("元ドキュメントID", 220.dp) { formatString(it.originalDocumentId) },
)

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
            message = "動画へのアクセス権限が必要です。\n「権限を付与する」をタップしてください。",
            onRequestPermission = { permissionLauncher.launch(requiredPermissions) },
            modifier = modifier,
        )
    } else {
        MediaTable(
            items = uiState.videos,
            columns = videoColumns,
            isLoading = uiState.isLoading,
            error = uiState.error,
            modifier = modifier,
        )
    }
}
