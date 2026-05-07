package io.github.casl0.mediastoreexplorer.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** アプリのトップレベル画面ルート。`rememberNavBackStack` で永続化するため `NavKey` を実装する。 */
@Serializable
sealed interface AppRoute : NavKey {
    @Serializable data object Main : AppRoute

    @Serializable data object Settings : AppRoute

    @Serializable data object Licenses : AppRoute
}
