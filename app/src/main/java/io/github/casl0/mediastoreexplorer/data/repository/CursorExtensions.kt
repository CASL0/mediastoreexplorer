package io.github.casl0.mediastoreexplorer.data.repository

import android.database.Cursor
import android.os.Build

internal fun Cursor.optStringCol(column: String): String? =
    getColumnIndex(column).takeIf { it >= 0 }?.let { getString(it) }

internal fun Cursor.optLongCol(column: String): Long? =
    getColumnIndex(column).takeIf { it >= 0 }?.let { getLong(it) }

internal fun Cursor.optIntCol(column: String): Int? =
    getColumnIndex(column).takeIf { it >= 0 }?.let { getInt(it) }

internal fun Cursor.optDoubleCol(column: String): Double? =
    getColumnIndex(column).takeIf { it >= 0 }?.let { getDouble(it) }

internal fun Cursor.optStringColQ(column: String): String? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) optStringCol(column) else null

internal fun Cursor.optIntColQ(column: String): Int? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) optIntCol(column) else null

internal fun Cursor.optStringColR(column: String): String? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) optStringCol(column) else null

internal fun Cursor.optLongColR(column: String): Long? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) optLongCol(column) else null

internal fun Cursor.optIntColR(column: String): Int? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) optIntCol(column) else null

internal fun Cursor.optIntColU(column: String): Int? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) optIntCol(column) else null
