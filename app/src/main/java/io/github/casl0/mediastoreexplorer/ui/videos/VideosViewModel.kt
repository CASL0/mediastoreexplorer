package io.github.casl0.mediastoreexplorer.ui.videos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.casl0.mediastoreexplorer.data.model.VideoItem
import io.github.casl0.mediastoreexplorer.data.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VideosUiState(
    val isLoading: Boolean = false,
    val videos: List<VideoItem> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class VideosViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideosUiState())
    val uiState: StateFlow<VideosUiState> = _uiState.asStateFlow()

    /** MediaStore から動画一覧を読み込み、[uiState] を更新する。 */
    fun loadVideos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { mediaRepository.getVideos() }
                .onSuccess { videos ->
                    _uiState.update { it.copy(isLoading = false, videos = videos) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message) }
                }
        }
    }
}
