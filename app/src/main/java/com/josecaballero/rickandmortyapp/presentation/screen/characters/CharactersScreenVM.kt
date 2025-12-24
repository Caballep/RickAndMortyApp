package com.josecaballero.rickandmortyapp.presentation.screen.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josecaballero.rickandmortyapp.domain.model.UCResult
import com.josecaballero.rickandmortyapp.domain.usecase.searchCharactersByName.SearchCharactersByNameUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharactersScreenVM @Inject constructor(
    private val searchCharactersByNameUC: SearchCharactersByNameUC
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharactersScreenState())
    val uiState: StateFlow<CharactersScreenState> = _uiState.asStateFlow()

    private val _errorDialogEvent = MutableSharedFlow<Unit>()
    val errorDialogEvent: SharedFlow<Unit> = _errorDialogEvent

    private var fetchJob: Job? = null

    fun handleAction(action: CharacterScreenAction) {
        when (action) {
            is CharacterScreenAction.Search -> {
                _uiState.update { it.copy(searchTerm = action.name) }
                fetchCharacters(_uiState.value.searchTerm)
            }
        }
    }

    private fun fetchCharacters(name: String) {
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    status = CharactersScreenState.CharactersStatus.Loading
                )
            }

            when (val result = searchCharactersByNameUC(name)) {

                is UCResult.Success -> {
                    val characters = result.data.map {
                        CharactersScreenState.CharactersStatus.Character.fromModel(it)
                    }

                    _uiState.update {
                        it.copy(
                            status = if (characters.isEmpty()) {
                                CharactersScreenState.CharactersStatus.Empty
                            } else {
                                CharactersScreenState.CharactersStatus.Success(characters)
                            }
                        )
                    }
                }

                is UCResult.Failure -> {
                    _errorDialogEvent.emit(Unit)
                    _uiState.update {
                        it.copy(
                            status = CharactersScreenState.CharactersStatus.Empty,
                        )
                    }
                }
            }
        }
    }
}