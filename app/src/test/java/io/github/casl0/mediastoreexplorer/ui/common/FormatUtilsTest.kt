package io.github.casl0.mediastoreexplorer.ui.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FormatUtilsTest {

    // region formatDateSec

    @Test
    fun `formatDateSec - null を渡すとハイフンを返す`() {
        assertEquals("-", formatDateSec(null))
    }

    @Test
    fun `formatDateSec - 0 を渡すとハイフンを返す`() {
        assertEquals("-", formatDateSec(0L))
    }

    @Test
    fun `formatDateSec - 有効なタイムスタンプを渡すと日付形式の文字列を返す`() {
        val result = formatDateSec(1_000_000L)
        assertTrue(result.matches(Regex("""\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}""")))
    }

    // endregion

    // region formatDateMs

    @Test
    fun `formatDateMs - null を渡すとハイフンを返す`() {
        assertEquals("-", formatDateMs(null))
    }

    @Test
    fun `formatDateMs - 0 を渡すとハイフンを返す`() {
        assertEquals("-", formatDateMs(0L))
    }

    @Test
    fun `formatDateMs - 有効なタイムスタンプを渡すと日付形式の文字列を返す`() {
        val result = formatDateMs(1_000_000_000L)
        assertTrue(result.matches(Regex("""\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}""")))
    }

    // endregion

    // region formatSize

    @Test
    fun `formatSize - null を渡すとハイフンを返す`() {
        assertEquals("-", formatSize(null))
    }

    @Test
    fun `formatSize - 1023 bytes を渡すと B 単位を返す`() {
        val result = formatSize(1_023L)
        assertTrue(result.endsWith("B") && !result.contains("K"))
    }

    @Test
    fun `formatSize - 1024 bytes を渡すと KB 単位を返す`() {
        assertTrue(formatSize(1_024L).endsWith("KB"))
    }

    @Test
    fun `formatSize - 1 MB を渡すと MB 単位を返す`() {
        assertTrue(formatSize(1_048_576L).endsWith("MB"))
    }

    @Test
    fun `formatSize - 1 GB を渡すと GB 単位を返す`() {
        assertTrue(formatSize(1_073_741_824L).endsWith("GB"))
    }

    // endregion

    // region formatDuration

    @Test
    fun `formatDuration - null を渡すとハイフンを返す`() {
        assertEquals("-", formatDuration(null))
    }

    @Test
    fun `formatDuration - 0 を渡すとハイフンを返す`() {
        assertEquals("-", formatDuration(0L))
    }

    @Test
    fun `formatDuration - 0分5秒 を渡すと 分秒形式を返す`() {
        assertEquals("0:05", formatDuration(5_000L))
    }

    @Test
    fun `formatDuration - 1分30秒 を渡すと 分秒形式を返す`() {
        assertEquals("1:30", formatDuration(90_000L))
    }

    @Test
    fun `formatDuration - 1時間2分3秒 を渡すと 時分秒形式を返す`() {
        assertEquals("1:02:03", formatDuration(3_723_000L))
    }

    // endregion

    // region formatInt

    @Test
    fun `formatInt - null を渡すとハイフンを返す`() {
        assertEquals("-", formatInt(null))
    }

    @Test
    fun `formatInt - 値を渡すと文字列を返す`() {
        assertEquals("42", formatInt(42))
    }

    // endregion

    // region formatLong

    @Test
    fun `formatLong - null を渡すとハイフンを返す`() {
        assertEquals("-", formatLong(null))
    }

    @Test
    fun `formatLong - 値を渡すと文字列を返す`() {
        assertEquals("100", formatLong(100L))
    }

    // endregion

    // region formatDouble

    @Test
    fun `formatDouble - null を渡すとハイフンを返す`() {
        assertEquals("-", formatDouble(null))
    }

    @Test
    fun `formatDouble - 値を渡すと文字列を返す`() {
        assertEquals("3.14", formatDouble(3.14))
    }

    // endregion

    // region formatString

    @Test
    fun `formatString - null を渡すとハイフンを返す`() {
        assertEquals("-", formatString(null))
    }

    @Test
    fun `formatString - 空文字を渡すとハイフンを返す`() {
        assertEquals("-", formatString(""))
    }

    @Test
    fun `formatString - 値を渡すとそのまま返す`() {
        assertEquals("hello", formatString("hello"))
    }

    // endregion

    // region formatBool

    @Test
    fun `formatBool - null を渡すとハイフンを返す`() {
        assertEquals("-", formatBool(null, "Yes", "No"))
    }

    @Test
    fun `formatBool - 1 を渡すと yes を返す`() {
        assertEquals("Yes", formatBool(1, "Yes", "No"))
    }

    @Test
    fun `formatBool - 0 を渡すと no を返す`() {
        assertEquals("No", formatBool(0, "Yes", "No"))
    }

    @Test
    fun `formatBool - 0 と 1 以外を渡すとハイフンを返す`() {
        assertEquals("-", formatBool(2, "Yes", "No"))
    }

    // endregion
}
