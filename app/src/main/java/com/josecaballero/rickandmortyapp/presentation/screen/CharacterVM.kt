package com.josecaballero.rickandmortyapp.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josecaballero.rickandmortyapp.domain.usecase.character.FailureType
import com.josecaballero.rickandmortyapp.domain.usecase.character.GetCharactersResult
import com.josecaballero.rickandmortyapp.domain.usecase.character.GetCharactersUC
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
class CharacterVM @Inject constructor(
    private val getCharactersUC: GetCharactersUC
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterState())
    val uiState: StateFlow<CharacterState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent

    private var fetchJob: Job? = null

    init {
        fetchCharacters(name = "Rick")
    }

    fun onSearchClicked(name: String) {
        _uiState.update { it.copy(searchTerm = name) }
        fetchCharacters(name)
    }

    private fun fetchCharacters(name: String) {
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = getCharactersUC(name)) {

                is GetCharactersResult.Success -> {
                    val characters = result.charactersModel.map {
                        CharacterState.Character.fromModel(it)
                    }

                    _uiState.update {
                        it.copy(
                            characters = characters,
                            isLoading = false,
                            error = null
                        )
                    }
                }

                is GetCharactersResult.Failure -> {
                    val errorMessage = when (val failure = result.failureType) {
                        // Check specifically for NetworkError
                        is FailureType.NetworkError -> "Network error. Please check your connection."

                        // Check specifically for EmptyQuery
                        is FailureType.EmptyQuery -> "Please enter a name to search."

                        // Use 'is' checks for the error types that map to a generic message
                        // We also include a final 'else' or cover the other concrete types (like UnknownError)
                        is FailureType.SqlError, is FailureType.Error -> {
                            // If UnknownError is a data class, we can access its message:
                            if (failure is FailureType.Error) {
                                "Something went wrong 2."
                            } else {
                                "Something went wrong."
                            }
                        }
                        // Add any other types here if the sealed class has more members
                        else -> "An unexpected error occurred."
                    }

                    _uiEvent.emit(errorMessage)

                    _uiState.update {
                        it.copy(
                            characters = emptyList(),
                            isLoading = false,
                            error = errorMessage
                        )
                    }
                }
            }
        }
    }
}