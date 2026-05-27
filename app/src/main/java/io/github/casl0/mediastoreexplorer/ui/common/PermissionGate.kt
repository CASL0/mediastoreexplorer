package io.github.casl0.mediastoreexplorer.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale

/**
 * Accompanist Permissions を用いて runtime permission の状態を扱う共通ゲート。
 *
 * 全権限が付与されると [onGranted] を呼び [content] を表示する。 1 つ以上が未付与の場合は [PermissionRequiredScreen] を表示し、
 * `shouldShowRationale` の真偽で [message] / [rationaleMessage] を出し分ける。 [permissions] が空のときは
 * Accompanist の `allPermissionsGranted` が真を返すため、自動的に granted パスに入る。
 *
 * Accompanist の `shouldShowRationale` は「初回」と「永久拒否」を区別できないため、 永久拒否時の設定画面誘導は本ゲートでは扱わない。
 *
 * @param permissions 要求する dangerous permissions のリスト
 * @param message 通常表示用の説明文
 * @param rationaleMessage `shouldShowRationale = true` のときに表示する説明文
 * @param onGranted 全権限が付与されたタイミングで呼ばれる副作用
 * @param modifier 権限要求画面に適用される [Modifier]
 * @param content 権限が付与された後に表示される本体
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
@Suppress(
    "LongParameterList"
) // 権限リスト/2 つのメッセージ/コールバック/モディファイア/コンテンツの組合せは Composable スロット API として妥当
fun PermissionGate(
    permissions: List<String>,
    message: String,
    rationaleMessage: String,
    onGranted: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val state = rememberMultiplePermissionsState(permissions)
    val granted = state.allPermissionsGranted
    val currentOnGranted by rememberUpdatedState(onGranted)
    LaunchedEffect(granted) { if (granted) currentOnGranted() }
    if (granted) {
        content()
    } else {
        PermissionRequiredScreen(
            message = message,
            rationaleMessage = rationaleMessage,
            showRationale = state.shouldShowRationale,
            onRequestPermission = state::launchMultiplePermissionRequest,
            modifier = modifier,
        )
    }
}
