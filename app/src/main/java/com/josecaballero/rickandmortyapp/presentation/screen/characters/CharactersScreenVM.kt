package com.josecaballero.rickandmortyapp.presentation.screen.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josecaballero.rickandmortyapp.domain.usecase.searchCharactersByName.SearchCharactersByNameResult
import com.josecaballero.rickandmortyapp.domain.usecase.searchCharactersByName.SearchCharactersByNameUC
import com.josecaballero.rickandmortyapp.domain.util.FailureType
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

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent

    private var fetchJob: Job? = null

    fun onSearchClicked(name: String) {
        _uiState.update { it.copy(searchTerm = name) }
        fetchCharacters(name)
    }

    private fun fetchCharacters(name: String) {
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    status = CharactersScreenState.CharactersStatus.Loading,
                    displayMessage = ""
                )
            }

            when (val result = searchCharactersByNameUC(name)) {

                is SearchCharactersByNameResult.Success -> {
                    val characters = result.charactersModel.map {
                        CharactersScreenState.CharactersStatus.Character.fromModel(it)
                    }

                    _uiState.update {
                        it.copy(
                            status = if (characters.isEmpty()) {
                                CharactersScreenState.CharactersStatus.Empty
                            } else {
                                CharactersScreenState.CharactersStatus.Success(characters)
                            },
                            displayMessage = ""
                        )
                    }
                }

                is SearchCharactersByNameResult.Failure -> {
                    if (result.failureType == FailureType.SqlError
                        || result.failureType == FailureType.Error
                    ) {
                        _uiEvent.emit("Something went wrong.")
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
}