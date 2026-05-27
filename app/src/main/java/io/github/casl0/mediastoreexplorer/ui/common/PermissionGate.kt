package io.github.casl0.mediastoreexplorer.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale

/**
 * Accompanist Permissions を用いて runtime permission の状態を扱う共通ゲート。
 *
 * [permissions] が空のときは権限不要とみなして即座に [content] を表示する。 全権限が付与されると [onGranted] を一度だけ呼び、以降は [content]
 * を表示する。 1 つ以上が未付与の場合は [PermissionRequiredScreen] を表示し、 `shouldShowRationale` の真偽で [message] /
 * [rationaleMessage] を出し分ける。
 *
 * Accompanist の `shouldShowRationale` は「初回」と「永久拒否」を区別できないため、 永久拒否時の設定画面誘導は本ゲートでは扱わない。
 *
 * @param permissions 要求する dangerous permissions のリスト。空の場合は即 content を表示する
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
    if (permissions.isEmpty()) {
        LaunchedEffect(Unit) { onGranted() }
        content()
        return
    }

    val state = rememberMultiplePermissionsState(permissions = permissions)

    LaunchedEffect(state.allPermissionsGranted) {
        if (state.allPermissionsGranted) {
            onGranted()
        }
    }

    if (state.allPermissionsGranted) {
        content()
    } else {
        PermissionRequiredScreen(
            message = message,
            rationaleMessage = rationaleMessage,
            showRationale = state.shouldShowRationale,
            onRequestPermission = { state.launchMultiplePermissionRequest() },
            modifier = modifier,
        )
    }
}
