package io.github.casl0.mediastoreexplorer.ui.audios

import io.github.casl0.mediastoreexplorer.MainDispatcherRule
import io.github.casl0.mediastoreexplorer.data.model.AudioItem
import io.github.casl0.mediastoreexplorer.data.repository.FakeMediaRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AudiosViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeMediaRepository
    private lateinit var viewModel: AudiosViewModel

    @Before
    fun setUp() {
        fakeRepository = FakeMediaRepository()
        viewModel = AudiosViewModel(fakeRepository)
    }

    // region 初期状態

    @Test
    fun 初期状態では_isLoading_が_false_で_audios_が空で_error_が_null() {
        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertTrue(state.audios.isEmpty())
        assertNull(state.error)
    }

    // endregion

    // region loadAudios 成功

    @Test
    fun loadAudios_成功時_audios_に取得結果が格納される() {
        val item =
            AudioItem(
                id = 1L,
                displayName = "audio.mp3",
                size = null,
                mimeType = null,
                dateAdded = null,
                dateModified = null,
                title = null,
                album = null,
                albumId = null,
                artist = null,
                artistId = null,
                composer = null,
                track = null,
                year = null,
                duration = null,
                bookmark = null,
                isMusic = null,
                isPodcast = null,
                isRingtone = null,
                isAlarm = null,
                isNotification = null,
                bucketId = null,
                bucketDisplayName = null,
                data = null,
                relativePath = null,
                volumeName = null,
                isPending = null,
                isAudiobook = null,
                isFavorite = null,
                isTrashed = null,
                generationAdded = null,
                generationModified = null,
                documentId = null,
                originalDocumentId = null,
            )
        fakeRepository.audios = listOf(item)

        viewModel.loadAudios()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(listOf(item), state.audios)
        assertNull(state.error)
    }

    @Test
    fun loadAudios_成功時_isLoading_が_false_になる() {
        fakeRepository.audios = emptyList()

        viewModel.loadAudios()

        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    // endregion

    // region loadAudios 失敗

    @Test
    fun loadAudios_失敗時_error_にメッセージが格納される() {
        fakeRepository.shouldThrow = RuntimeException("network error")

        viewModel.loadAudios()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertTrue(state.audios.isEmpty())
        assertEquals("network error", state.error)
    }

    @Test
    fun loadAudios_失敗時_isLoading_が_false_になる() {
        fakeRepository.shouldThrow = RuntimeException("fail")

        viewModel.loadAudios()

        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    // endregion
}
