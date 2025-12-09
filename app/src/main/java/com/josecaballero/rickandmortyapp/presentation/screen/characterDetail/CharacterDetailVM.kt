package com.josecaballero.rickandmortyapp.presentation.screen.characterDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josecaballero.rickandmortyapp.domain.usecase.getCharacter.GetCharacterResult
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
class CharacterDetailVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCharacterDetailsUC: GetCharacterUC,
) : ViewModel() {

    val characterId: Int = checkNotNull(savedStateHandle.get<Int>(CHARACTER_ID))
    private val _uiState = MutableStateFlow(CharacterDetailState(isLoading = true))
    val uiState: StateFlow<CharacterDetailState> = _uiState.asStateFlow()

    init {
        fetchCharacterDetails(characterId)
    }

    private fun fetchCharacterDetails(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = getCharacterDetailsUC(id)) {
                is GetCharacterResult.Success -> {
                    val character = CharacterDetailState.Character.fromModel(result.characterModel)
                    _uiState.update { it.copy(character = character, isLoading = false, error = null) }
                }
                is GetCharacterResult.Failure -> {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to load character details.", character = null) }
                }
            }
        }
    }
}
