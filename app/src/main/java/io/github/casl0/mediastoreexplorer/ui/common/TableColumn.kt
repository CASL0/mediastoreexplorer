package io.github.casl0.mediastoreexplorer.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

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
