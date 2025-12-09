package com.josecaballero.rickandmortyapp.domain.usecase.searchCharactersByName

import com.josecaballero.rickandmortyapp.domain.model.CharacterModel
import com.josecaballero.rickandmortyapp.domain.util.FailureType

sealed class SearchCharactersByNameResult {
    data class Success(val charactersModel: List<CharacterModel>): SearchCharactersByNameResult()
    data class Failure(val failureType: FailureType): SearchCharactersByNameResult()
}