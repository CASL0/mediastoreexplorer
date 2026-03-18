package io.github.casl0.mediastoreexplorer.ui.audios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.casl0.mediastoreexplorer.data.model.AudioItem
import io.github.casl0.mediastoreexplorer.data.repository.MediaRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AudiosUiState(
    val isLoading: Boolean = false,
    val audios: List<AudioItem> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class AudiosViewModel @Inject constructor(private val mediaRepository: MediaRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow(AudiosUiState())
    val uiState: StateFlow<AudiosUiState> = _uiState.asStateFlow()

    /** MediaStore から音声一覧を読み込み、[uiState] を更新する。 */
    fun loadAudios() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { mediaRepository.getAudios() }
                .onSuccess { audios ->
                    _uiState.update { it.copy(isLoading = false, audios = audios) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message) }
                }
        }
    }
}
