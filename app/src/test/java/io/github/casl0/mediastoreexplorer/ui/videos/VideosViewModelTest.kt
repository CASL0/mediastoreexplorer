package io.github.casl0.mediastoreexplorer.ui.videos

import io.github.casl0.mediastoreexplorer.MainDispatcherRule
import io.github.casl0.mediastoreexplorer.data.model.VideoItem
import io.github.casl0.mediastoreexplorer.data.repository.FakeMediaRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class VideosViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeMediaRepository
    private lateinit var viewModel: VideosViewModel

    @Before
    fun setUp() {
        fakeRepository = FakeMediaRepository()
        viewModel = VideosViewModel(fakeRepository)
    }

    // region 初期状態

    @Test
    fun 初期状態では_isLoading_が_false_で_videos_が空で_error_が_null() {
        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertTrue(state.videos.isEmpty())
        assertNull(state.error)
    }

    // endregion

    // region loadVideos 成功

    @Test
    fun loadVideos_成功時_videos_に取得結果が格納される() {
        val item = VideoItem(
            id = 1L,
            displayName = "video.mp4",
            size = null,
            mimeType = null,
            dateAdded = null,
            dateModified = null,
            dateTaken = null,
            width = null,
            height = null,
            bucketId = null,
            bucketDisplayName = null,
            description = null,
            category = null,
            language = null,
            artist = null,
            album = null,
            tags = null,
            duration = null,
            resolution = null,
            bookmark = null,
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
        fakeRepository.videos = listOf(item)

        viewModel.loadVideos()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(listOf(item), state.videos)
        assertNull(state.error)
    }

    @Test
    fun loadVideos_成功時_isLoading_が_false_になる() {
        fakeRepository.videos = emptyList()

        viewModel.loadVideos()

        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    // endregion

    // region loadVideos 失敗

    @Test
    fun loadVideos_失敗時_error_にメッセージが格納される() {
        fakeRepository.shouldThrow = RuntimeException("network error")

        viewModel.loadVideos()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertTrue(state.videos.isEmpty())
        assertEquals("network error", state.error)
    }

    @Test
    fun loadVideos_失敗時_isLoading_が_false_になる() {
        fakeRepository.shouldThrow = RuntimeException("fail")

        viewModel.loadVideos()

        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    // endregion
}
