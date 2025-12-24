package com.josecaballero.rickandmortyapp.presentation.screen.characterDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josecaballero.rickandmortyapp.domain.model.UCResult
import com.josecaballero.rickandmortyapp.domain.usecase.getCharacter.GetCharacterUC
import com.josecaballero.rickandmortyapp.presentation.navigation.CHARACTER_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailScreenVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCharacterDetailsUC: GetCharacterUC,
) : ViewModel() {

    private val characterId: Int = checkNotNull(savedStateHandle.get<Int>(CHARACTER_ID))

    private val _uiState = MutableStateFlow(CharacterDetailScreenState())
    val uiState: StateFlow<CharacterDetailScreenState> = _uiState.asStateFlow()

    init {
        fetchCharacterDetails(characterId)
    }

    private fun fetchCharacterDetails(id: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    status = CharacterDetailScreenState.CharacterDetailStatus.Loading,
                    displayMessage = ""
                )
            }

            when (val result = getCharacterDetailsUC(id)) {
                is UCResult.Success -> {
                    val character = CharacterDetailScreenState.DetailCharacter.fromModel(result.data)

                    _uiState.update {
                        it.copy(
                            status = CharacterDetailScreenState.CharacterDetailStatus.Success(character),
                            displayMessage = ""
                        )
                    }
                }

                is UCResult.Failure -> {
                    val errorMessage = "Failed to load character details."

                    _uiState.update {
                        it.copy(
                            status = CharacterDetailScreenState.CharacterDetailStatus.Error(errorMessage),
                            displayMessage = errorMessage
                        )
                    }
                }
            }
        }
    }
}
