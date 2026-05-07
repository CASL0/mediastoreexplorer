package io.github.casl0.mediastoreexplorer.data.preferences

/** ユーザー設定の集約モデル。 */
data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.System,
    val dynamicColor: Boolean = true,
)
