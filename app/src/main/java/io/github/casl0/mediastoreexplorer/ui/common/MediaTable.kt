package io.github.casl0.mediastoreexplorer.ui.common

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.casl0.mediastoreexplorer.R
import io.github.casl0.mediastoreexplorer.ui.theme.MediaStoreExplorerTheme

/**
 * テーブルの 1 カラムを定義するクラス。
 *
 * テキストセルの場合は [getValue] を指定する。 カスタム Composable セル（サムネイル等）の場合は [customContent] を指定する。 両方指定した場合は
 * [customContent] が優先される。
 *
 * @param T テーブルに表示するアイテムの型
 * @property header ヘッダーに表示する文字列
 * @property width カラムの表示幅
 * @property customContent カスタムセル描画用の Composable。null の場合は [getValue] の結果をテキスト表示する
 * @property getValue アイテムからセルの表示文字列を取得する関数。[customContent] が null の場合のみ使用される
 */
class TableColumn<T>(
    val header: String,
    val width: Dp,
    val customContent: (@Composable (T) -> Unit)? = null,
    val getValue: (T) -> String = { "" },
)

/**
 * sticky ヘッダーと水平スクロールを備えたメディアテーブル。
 *
 * ロード中はプログレスインジケーター、エラー時はエラーメッセージ、 アイテムが空の場合は空メッセージを表示する。
 *
 * @param T テーブルに表示するアイテムの型
 * @param items 表示するアイテムのリスト
 * @param columns 表示するカラムの定義リスト
 * @param isLoading データ読み込み中かどうか
 * @param error エラーメッセージ。エラーがない場合は null
 * @param modifier レイアウト調整用の [Modifier]
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> MediaTable(
    items: List<T>,
    columns: List<TableColumn<T>>,
    isLoading: Boolean,
    error: String?,
    modifier: Modifier = Modifier,
    key: (T) -> Any,
) {
    val scrollState: ScrollState = rememberScrollState()

    when {
        isLoading ->
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

        error != null ->
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.error_message, error),
                    color = MaterialTheme.colorScheme.error,
                )
            }

        items.isEmpty() ->
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.no_items))
            }

        else -> {
            LazyColumn(modifier = modifier.fillMaxSize()) {
                stickyHeader {
                    Row(
                        modifier =
                            Modifier.fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .horizontalScroll(scrollState)
                    ) {
                        columns.forEach { col ->
                            TableHeaderCell(text = col.header, width = col.width)
                        }
                    }
                    HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.primary)
                }

                itemsIndexed(items, key = { _, item -> key(item) }) { index, item ->
                    val rowBackground =
                        if (index % 2 == 0) {
                            MaterialTheme.colorScheme.surface
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    Row(
                        modifier =
                            Modifier.fillMaxWidth()
                                .background(rowBackground)
                                .horizontalScroll(scrollState)
                    ) {
                        columns.forEach { col ->
                            if (col.customContent != null) {
                                Box(
                                    modifier = Modifier.size(col.width).padding(4.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    col.customContent.invoke(item)
                                }
                            } else {
                                TableDataCell(text = col.getValue(item), width = col.width)
                            }
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

@Composable
private fun TableHeaderCell(text: String, width: Dp) {
    Text(
        text = text,
        modifier = Modifier.width(width).padding(horizontal = 8.dp, vertical = 6.dp),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
private val previewColumns: List<TableColumn<String>> =
    listOf(TableColumn("Name", 180.dp) { it }, TableColumn("Value", 120.dp) { "sample" })

@Preview(showBackground = true)
@Composable
private fun MediaTableLoadingPreview() {
    MediaStoreExplorerTheme {
        MediaTable(
            items = emptyList<String>(),
            columns = previewColumns,
            isLoading = true,
            error = null,
            key = { it },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MediaTableErrorPreview() {
    MediaStoreExplorerTheme {
        MediaTable(
            items = emptyList<String>(),
            columns = previewColumns,
            isLoading = false,
            error = "ContentResolver query failed",
            key = { it },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MediaTableEmptyPreview() {
    MediaStoreExplorerTheme {
        MediaTable(
            items = emptyList<String>(),
            columns = previewColumns,
            isLoading = false,
            error = null,
            key = { it },
        )
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun MediaTableWithDataPreview() {
    MediaStoreExplorerTheme {
        MediaTable(
            items = listOf("photo_001.jpg", "photo_002.png", "video_clip.mp4"),
            columns = previewColumns,
            isLoading = false,
            error = null,
            key = { it },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TableDataCell(text: String, width: Dp) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = { PlainTooltip { Text(text) } },
        state = rememberTooltipState(),
    ) {
        Text(
            text = text,
            modifier = Modifier.width(width).padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
