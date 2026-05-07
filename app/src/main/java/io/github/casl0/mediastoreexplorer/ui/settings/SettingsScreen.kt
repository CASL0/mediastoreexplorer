package io.github.casl0.mediastoreexplorer.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.casl0.mediastoreexplorer.BuildConfig
import io.github.casl0.mediastoreexplorer.R
import io.github.casl0.mediastoreexplorer.data.preferences.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit,
    onLicensesClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier =
                Modifier.padding(innerPadding).verticalScroll(rememberScrollState()).fillMaxWidth()
        ) {
            SectionHeader(stringResource(R.string.settings_section_appearance))
            ThemeRow(themeMode = state.themeMode, onThemeChange = viewModel::setThemeMode)
            DynamicColorRow(
                enabled = state.dynamicColor,
                onEnabledChange = viewModel::setDynamicColor,
            )
            LanguageRow(
                appLanguage = state.appLanguage,
                onLanguageChange = viewModel::setAppLanguage,
            )

            HorizontalDivider()

            SectionHeader(stringResource(R.string.settings_section_about))
            SettingsRow(
                title = stringResource(R.string.settings_app_version),
                summary =
                    stringResource(
                        R.string.settings_app_version_format,
                        BuildConfig.VERSION_NAME,
                        BuildConfig.VERSION_CODE,
                    ),
            )
            SettingsRow(
                title = stringResource(R.string.settings_oss_licenses),
                onClick = onLicensesClick,
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
    )
}

@Composable
private fun SettingsRow(
    title: String,
    summary: String? = null,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    val rowModifier =
        Modifier.fillMaxWidth()
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    Row(modifier = rowModifier, verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (summary != null) {
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (trailing != null) trailing()
    }
}

@Composable
private fun ThemeRow(themeMode: ThemeMode, onThemeChange: (ThemeMode) -> Unit) {
    var dialogOpen by remember { mutableStateOf(false) }
    SettingsRow(
        title = stringResource(R.string.settings_theme),
        summary = stringResource(themeMode.labelRes()),
        onClick = { dialogOpen = true },
    )
    if (dialogOpen) {
        SingleChoiceDialog(
            title = stringResource(R.string.settings_theme),
            options = ThemeMode.entries,
            optionLabel = { stringResource(it.labelRes()) },
            selected = themeMode,
            onSelected = {
                onThemeChange(it)
                dialogOpen = false
            },
            onDismiss = { dialogOpen = false },
        )
    }
}

@Composable
private fun DynamicColorRow(enabled: Boolean, onEnabledChange: (Boolean) -> Unit) {
    SettingsRow(
        title = stringResource(R.string.settings_dynamic_color),
        summary = stringResource(R.string.settings_dynamic_color_summary),
        onClick = { onEnabledChange(!enabled) },
        trailing = { Switch(checked = enabled, onCheckedChange = onEnabledChange) },
    )
}

@Composable
private fun LanguageRow(appLanguage: String?, onLanguageChange: (String?) -> Unit) {
    var dialogOpen by remember { mutableStateOf(false) }
    val options =
        listOf(
            null to R.string.settings_language_system,
            "en" to R.string.settings_language_english,
            "ja" to R.string.settings_language_japanese,
        )
    val selectedLabel =
        options.firstOrNull { it.first == appLanguage }?.second ?: R.string.settings_language_system
    SettingsRow(
        title = stringResource(R.string.settings_language),
        summary = stringResource(selectedLabel),
        onClick = { dialogOpen = true },
    )
    if (dialogOpen) {
        SingleChoiceDialog(
            title = stringResource(R.string.settings_language),
            options = options,
            optionLabel = { stringResource(it.second) },
            selected = options.firstOrNull { it.first == appLanguage } ?: options.first(),
            onSelected = {
                onLanguageChange(it.first)
                dialogOpen = false
            },
            onDismiss = { dialogOpen = false },
        )
    }
}

@Composable
private fun <T> SingleChoiceDialog(
    title: String,
    options: List<T>,
    optionLabel: @Composable (T) -> String,
    selected: T,
    onSelected: (T) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                options.forEach { option ->
                    Row(
                        modifier =
                            Modifier.fillMaxWidth()
                                .selectable(
                                    selected = option == selected,
                                    onClick = { onSelected(option) },
                                )
                                .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        RadioButton(selected = option == selected, onClick = null)
                        Text(text = optionLabel(option))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_back)) }
        },
    )
}

private fun ThemeMode.labelRes(): Int =
    when (this) {
        ThemeMode.System -> R.string.settings_theme_system
        ThemeMode.Light -> R.string.settings_theme_light
        ThemeMode.Dark -> R.string.settings_theme_dark
    }
