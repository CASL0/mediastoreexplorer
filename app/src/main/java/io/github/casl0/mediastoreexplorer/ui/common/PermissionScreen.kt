package io.github.casl0.mediastoreexplorer.ui.common

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.casl0.mediastoreexplorer.R
import io.github.casl0.mediastoreexplorer.ui.theme.MediaStoreExplorerTheme

/**
 * 権限が付与されていない場合に表示する汎用画面。
 *
 * @param message ユーザーに表示する権限の説明メッセージ
 * @param onRequestPermission 権限リクエストボタンがタップされたときのコールバック
 * @param modifier レイアウト調整用の [Modifier]
 */
@Composable
fun PermissionRequiredScreen(
    message: String,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRequestPermission) { Text(stringResource(R.string.grant_permission)) }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun PermissionRequiredScreenPreview() {
    MediaStoreExplorerTheme {
        PermissionRequiredScreen(
            message = "Permission to access images is required.\nTap \"Grant Permission\".",
            onRequestPermission = {},
        )
    }
}
