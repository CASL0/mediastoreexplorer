package io.github.casl0.mediastoreexplorer.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class AppDestinationsTest {

    @Test
    fun entries_5つのメディアタブが定義されている() {
        assertEquals(5, AppDestinations.entries.size)
    }

    @Test
    fun entries_全エントリが非ゼロのlabelとcontentDescriptionリソースidを持つ() {
        AppDestinations.entries.forEach { destination ->
            assertNotEquals("${destination.name} の label リソース id が 0", 0, destination.label)
            assertNotEquals(
                "${destination.name} の contentDescription リソース id が 0",
                0,
                destination.contentDescription,
            )
        }
    }

    @Test
    fun entries_全エントリのiconがユニーク() {
        val icons = AppDestinations.entries.map { it.icon }
        assertEquals("icon が重複している destination がある: $icons", icons.size, icons.toSet().size)
    }
}
