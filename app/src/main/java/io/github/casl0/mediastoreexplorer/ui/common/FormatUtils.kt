package io.github.casl0.mediastoreexplorer.ui.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

/** seconds since Unix epoch → 日付文字列 */
fun formatDateSec(timestampSec: Long?): String {
    if (timestampSec == null || timestampSec == 0L) return "-"
    return dateFormat.format(Date(timestampSec * 1000))
}

/** milliseconds since Unix epoch → 日付文字列 */
fun formatDateMs(timestampMs: Long?): String {
    if (timestampMs == null || timestampMs == 0L) return "-"
    return dateFormat.format(Date(timestampMs))
}

/** bytes → 人間が読みやすい単位 */
fun formatSize(bytes: Long?): String {
    if (bytes == null) return "-"
    return when {
        bytes >= 1_073_741_824L -> "%.2f GB".format(bytes / 1_073_741_824.0)
        bytes >= 1_048_576L -> "%.2f MB".format(bytes / 1_048_576.0)
        bytes >= 1_024L -> "%.1f KB".format(bytes / 1_024.0)
        else -> "$bytes B"
    }
}

/** milliseconds → HH:MM:SS */
fun formatDuration(durationMs: Long?): String {
    if (durationMs == null || durationMs == 0L) return "-"
    val hours = durationMs / 3_600_000
    val minutes = (durationMs % 3_600_000) / 60_000
    val seconds = (durationMs % 60_000) / 1_000
    return if (hours > 0) "%d:%02d:%02d".format(hours, minutes, seconds)
    else "%d:%02d".format(minutes, seconds)
}

fun formatInt(value: Int?): String = value?.toString() ?: "-"
fun formatLong(value: Long?): String = value?.toString() ?: "-"
fun formatDouble(value: Double?): String = value?.toString() ?: "-"
fun formatString(value: String?): String = if (value.isNullOrEmpty()) "-" else value
fun formatBool(value: Int?): String = when (value) {
    1 -> "はい"
    0 -> "いいえ"
    else -> "-"
}
