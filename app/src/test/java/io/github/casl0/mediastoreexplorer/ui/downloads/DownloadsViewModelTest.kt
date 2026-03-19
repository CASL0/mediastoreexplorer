package io.github.casl0.mediastoreexplorer.ui.downloads

import io.github.casl0.mediastoreexplorer.MainDispatcherRule
import io.github.casl0.mediastoreexplorer.data.model.DownloadItem
import io.github.casl0.mediastoreexplorer.data.repository.FakeMediaRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DownloadsViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeMediaRepository
    private lateinit var viewModel: DownloadsViewModel

    @Before
    fun setUp() {
        fakeRepository = FakeMediaRepository()
        viewModel = DownloadsViewModel(fakeRepository)
    }

    // region 初期状態

    @Test
    fun 初期状態では_isLoading_が_false_で_downloads_が空で_error_が_null() {
        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertTrue(state.downloads.isEmpty())
        assertNull(state.error)
    }

    // endregion

    // region loadDownloads 成功

    @Test
    fun loadDownloads_成功時_downloads_に取得結果が格納される() {
        val item =
            DownloadItem(
                id = 1L,
                displayName = "file.zip",
                size = null,
                mimeType = null,
                dateAdded = null,
                dateModified = null,
                data = null,
                relativePath = null,
                volumeName = null,
                isPending = null,
                downloadUri = null,
                refererUri = null,
                isFavorite = null,
                isTrashed = null,
                generationAdded = null,
                generationModified = null,
                documentId = null,
                originalDocumentId = null,
                isDrm = null,
            )
        fakeRepository.downloads = listOf(item)

        viewModel.loadDownloads()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(listOf(item), state.downloads)
        assertNull(state.error)
    }

    @Test
    fun loadDownloads_成功時_isLoading_が_false_になる() {
        fakeRepository.downloads = emptyList()

        viewModel.loadDownloads()

        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    // endregion

    // region loadDownloads 失敗

    @Test
    fun loadDownloads_失敗時_error_にメッセージが格納される() {
        fakeRepository.shouldThrow = RuntimeException("network error")

        viewModel.loadDownloads()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertTrue(state.downloads.isEmpty())
        assertEquals("network error", state.error)
    }

    @Test
    fun loadDownloads_失敗時_isLoading_が_false_になる() {
        fakeRepository.shouldThrow = RuntimeException("fail")

        viewModel.loadDownloads()

        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    // endregion
}
