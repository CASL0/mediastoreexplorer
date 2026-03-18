package io.github.casl0.mediastoreexplorer.ui.images

import io.github.casl0.mediastoreexplorer.MainDispatcherRule
import io.github.casl0.mediastoreexplorer.data.model.ImageItem
import io.github.casl0.mediastoreexplorer.data.repository.FakeMediaRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ImagesViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeMediaRepository
    private lateinit var viewModel: ImagesViewModel

    @Before
    fun setUp() {
        fakeRepository = FakeMediaRepository()
        viewModel = ImagesViewModel(fakeRepository)
    }

    // region 初期状態

    @Test
    fun 初期状態では_isLoading_が_false_で_images_が空で_error_が_null() {
        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertTrue(state.images.isEmpty())
        assertNull(state.error)
    }

    // endregion

    // region loadImages 成功

    @Test
    fun loadImages_成功時_images_に取得結果が格納される() {
        val item =
            ImageItem(
                id = 1L,
                displayName = "photo.jpg",
                size = null,
                mimeType = null,
                dateAdded = null,
                dateModified = null,
                dateTaken = null,
                width = null,
                height = null,
                orientation = null,
                bucketId = null,
                bucketDisplayName = null,
                description = null,
                isPrivate = null,
                latitude = null,
                longitude = null,
                data = null,
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
        fakeRepository.images = listOf(item)

        viewModel.loadImages()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(listOf(item), state.images)
        assertNull(state.error)
    }

    @Test
    fun loadImages_成功時_isLoading_が_false_になる() {
        fakeRepository.images = emptyList()

        viewModel.loadImages()

        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    // endregion

    // region loadImages 失敗

    @Test
    fun loadImages_失敗時_error_にメッセージが格納される() {
        fakeRepository.shouldThrow = RuntimeException("network error")

        viewModel.loadImages()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertTrue(state.images.isEmpty())
        assertEquals("network error", state.error)
    }

    @Test
    fun loadImages_失敗時_isLoading_が_false_になる() {
        fakeRepository.shouldThrow = RuntimeException("fail")

        viewModel.loadImages()

        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    // endregion
}
