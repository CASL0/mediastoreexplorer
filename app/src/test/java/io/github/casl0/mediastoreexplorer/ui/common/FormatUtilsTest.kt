package io.github.casl0.mediastoreexplorer.ui.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FormatUtilsTest {

    // region formatDateSec

    @Test
    fun formatDateSec_nullを渡すとハイフンを返す() {
        assertEquals("-", formatDateSec(null))
    }

    @Test
    fun formatDateSec_0を渡すとハイフンを返す() {
        assertEquals("-", formatDateSec(0L))
    }

    @Test
    fun formatDateSec_有効なタイムスタンプを渡すと日付形式の文字列を返す() {
        val result = formatDateSec(1_000_000L)
        assertTrue(result.matches(Regex("""\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}""")))
    }

    // endregion

    // region formatDateMs

    @Test
    fun formatDateMs_nullを渡すとハイフンを返す() {
        assertEquals("-", formatDateMs(null))
    }

    @Test
    fun formatDateMs_0を渡すとハイフンを返す() {
        assertEquals("-", formatDateMs(0L))
    }

    @Test
    fun formatDateMs_有効なタイムスタンプを渡すと日付形式の文字列を返す() {
        val result = formatDateMs(1_000_000_000L)
        assertTrue(result.matches(Regex("""\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}""")))
    }

    // endregion

    // region formatSize

    @Test
    fun formatSize_nullを渡すとハイフンを返す() {
        assertEquals("-", formatSize(null))
    }

    @Test
    fun formatSize_1023bytesを渡すとB単位を返す() {
        val result = formatSize(1_023L)
        assertTrue(result.endsWith("B") && !result.contains("K"))
    }

    @Test
    fun formatSize_1024bytesを渡すとKB単位を返す() {
        assertTrue(formatSize(1_024L).endsWith("KB"))
    }

    @Test
    fun formatSize_1MBを渡すとMB単位を返す() {
        assertTrue(formatSize(1_048_576L).endsWith("MB"))
    }

    @Test
    fun formatSize_1GBを渡すとGB単位を返す() {
        assertTrue(formatSize(1_073_741_824L).endsWith("GB"))
    }

    // endregion

    // region formatDuration

    @Test
    fun formatDuration_nullを渡すとハイフンを返す() {
        assertEquals("-", formatDuration(null))
    }

    @Test
    fun formatDuration_0を渡すとハイフンを返す() {
        assertEquals("-", formatDuration(0L))
    }

    @Test
    fun formatDuration_0分5秒を渡すと分秒形式を返す() {
        assertEquals("0:05", formatDuration(5_000L))
    }

    @Test
    fun formatDuration_1分30秒を渡すと分秒形式を返す() {
        assertEquals("1:30", formatDuration(90_000L))
    }

    @Test
    fun formatDuration_1時間2分3秒を渡すと時分秒形式を返す() {
        assertEquals("1:02:03", formatDuration(3_723_000L))
    }

    // endregion

    // region formatInt

    @Test
    fun formatInt_nullを渡すとハイフンを返す() {
        assertEquals("-", formatInt(null))
    }

    @Test
    fun formatInt_値を渡すと文字列を返す() {
        assertEquals("42", formatInt(42))
    }

    // endregion

    // region formatLong

    @Test
    fun formatLong_nullを渡すとハイフンを返す() {
        assertEquals("-", formatLong(null))
    }

    @Test
    fun formatLong_値を渡すと文字列を返す() {
        assertEquals("100", formatLong(100L))
    }

    // endregion

    // region formatDouble

    @Test
    fun formatDouble_nullを渡すとハイフンを返す() {
        assertEquals("-", formatDouble(null))
    }

    @Test
    fun formatDouble_値を渡すと文字列を返す() {
        assertEquals("3.14", formatDouble(3.14))
    }

    // endregion

    // region formatString

    @Test
    fun formatString_nullを渡すとハイフンを返す() {
        assertEquals("-", formatString(null))
    }

    @Test
    fun formatString_空文字を渡すとハイフンを返す() {
        assertEquals("-", formatString(""))
    }

    @Test
    fun formatString_値を渡すとそのまま返す() {
        assertEquals("hello", formatString("hello"))
    }

    // endregion

    // region formatBool

    @Test
    fun formatBool_nullを渡すとハイフンを返す() {
        assertEquals("-", formatBool(null, "Yes", "No"))
    }

    @Test
    fun formatBool_1を渡すとyesを返す() {
        assertEquals("Yes", formatBool(1, "Yes", "No"))
    }

    @Test
    fun formatBool_0を渡すとnoを返す() {
        assertEquals("No", formatBool(0, "Yes", "No"))
    }

    @Test
    fun formatBool_0と1以外を渡すとハイフンを返す() {
        assertEquals("-", formatBool(2, "Yes", "No"))
    }

    // endregion
}
