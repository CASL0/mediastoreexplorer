package io.github.casl0.mediastoreexplorer.ui.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.casl0.mediastoreexplorer.data.model.DownloadItem
import io.github.casl0.mediastoreexplorer.data.repository.MediaRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DownloadsUiState(
    val isLoading: Boolean = false,
    val downloads: List<DownloadItem> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class DownloadsViewModel @Inject constructor(private val mediaRepository: MediaRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow(DownloadsUiState())
    val uiState: StateFlow<DownloadsUiState> = _uiState.asStateFlow()

    /** MediaStore からダウンロードファイル一覧を読み込み、[uiState] を更新する。 */
    fun loadDownloads() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { mediaRepository.getDownloads() }
                .onSuccess { downloads ->
                    _uiState.update { it.copy(isLoading = false, downloads = downloads) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message) }
                }
        }
    }
}
