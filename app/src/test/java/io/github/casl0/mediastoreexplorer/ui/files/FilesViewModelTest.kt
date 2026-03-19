package io.github.casl0.mediastoreexplorer.ui.files

import io.github.casl0.mediastoreexplorer.MainDispatcherRule
import io.github.casl0.mediastoreexplorer.data.model.FileItem
import io.github.casl0.mediastoreexplorer.data.repository.FakeMediaRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FilesViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeMediaRepository
    private lateinit var viewModel: FilesViewModel

    @Before
    fun setUp() {
        fakeRepository = FakeMediaRepository()
        viewModel = FilesViewModel(fakeRepository)
    }

    // region 初期状態

    @Test
    fun 初期状態では_isLoading_が_false_で_files_が空で_error_が_null() {
        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertTrue(state.files.isEmpty())
        assertNull(state.error)
    }

    // endregion

    // region loadFiles 成功

    @Test
    fun loadFiles_成功時_files_に取得結果が格納される() {
        val item =
            FileItem(
                id = 1L,
                displayName = "document.pdf",
                size = null,
                mimeType = null,
                dateAdded = null,
                dateModified = null,
                data = null,
                mediaType = null,
                title = null,
                parent = null,
                relativePath = null,
                volumeName = null,
                isPending = null,
                isFavorite = null,
                isTrashed = null,
                generationAdded = null,
                generationModified = null,
                documentId = null,
                originalDocumentId = null,
            )
        fakeRepository.files = listOf(item)

        viewModel.loadFiles()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(listOf(item), state.files)
        assertNull(state.error)
    }

    @Test
    fun loadFiles_成功時_isLoading_が_false_になる() {
        fakeRepository.files = emptyList()

        viewModel.loadFiles()

        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    // endregion

    // region loadFiles 失敗

    @Test
    fun loadFiles_失敗時_error_にメッセージが格納される() {
        fakeRepository.shouldThrow = RuntimeException("network error")

        viewModel.loadFiles()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertTrue(state.files.isEmpty())
        assertEquals("network error", state.error)
    }

    @Test
    fun loadFiles_失敗時_isLoading_が_false_になる() {
        fakeRepository.shouldThrow = RuntimeException("fail")

        viewModel.loadFiles()

        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    // endregion
}
