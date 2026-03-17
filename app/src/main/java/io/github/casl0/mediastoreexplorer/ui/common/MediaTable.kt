package io.github.casl0.mediastoreexplorer.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TableColumn<T>(
    val header: String,
    val width: Dp,
    val getValue: (T) -> String,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> MediaTable(
    items: List<T>,
    columns: List<TableColumn<T>>,
    isLoading: Boolean,
    error: String?,
    modifier: Modifier = Modifier,
) {
    val scrollState: ScrollState = rememberScrollState()

    when {
        isLoading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        error != null -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "エラー: $error", color = MaterialTheme.colorScheme.error)
        }

        items.isEmpty() -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "アイテムがありません")
        }

        else -> {
            LazyColumn(modifier = modifier.fillMaxSize()) {
                // ヘッダー行 (sticky)
                stickyHeader {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .horizontalScroll(scrollState),
                    ) {
                        columns.forEach { col ->
                            TableHeaderCell(text = col.header, width = col.width)
                        }
                    }
                    HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.primary)
                }

                // データ行
                itemsIndexed(items) { index, item ->
                    val rowBackground = if (index % 2 == 0) {
                        MaterialTheme.colorScheme.surface
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(rowBackground)
                            .horizontalScroll(scrollState),
                    ) {
                        columns.forEach { col ->
                            TableDataCell(text = col.getValue(item), width = col.width)
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
        modifier = Modifier
            .width(width)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
    )
}

@Composable
private fun TableDataCell(text: String, width: Dp) {
    Text(
        text = text,
        modifier = Modifier
            .width(width)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        fontSize = 11.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}
