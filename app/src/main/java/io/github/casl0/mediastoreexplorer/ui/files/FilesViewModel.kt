package io.github.casl0.mediastoreexplorer.ui.files

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.casl0.mediastoreexplorer.data.model.FileItem
import io.github.casl0.mediastoreexplorer.data.repository.MediaRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FilesUiState(
    val isLoading: Boolean = false,
    val files: List<FileItem> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class FilesViewModel @Inject constructor(private val mediaRepository: MediaRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow(FilesUiState())
    val uiState: StateFlow<FilesUiState> = _uiState.asStateFlow()

    /** MediaStore からファイル一覧を読み込み、[uiState] を更新する。 */
    fun loadFiles() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { mediaRepository.getFiles() }
                .onSuccess { files ->
                    _uiState.update { it.copy(isLoading = false, files = files) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message) }
                }
        }
    }
}
