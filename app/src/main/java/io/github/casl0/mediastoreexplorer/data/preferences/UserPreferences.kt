package io.github.casl0.mediastoreexplorer.data.preferences

/**
 * ユーザー設定の集約モデル。
 *
 * @property appLanguage BCP-47 言語タグ。`null` のときは端末設定に追従する。
 */
data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.System,
    val dynamicColor: Boolean = true,
    val appLanguage: String? = null,
)
