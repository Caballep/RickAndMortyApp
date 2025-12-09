package com.josecaballero.rickandmortyapp.domain.usecase.getCharacter

import com.josecaballero.rickandmortyapp.domain.model.CharacterModel
import com.josecaballero.rickandmortyapp.domain.util.FailureType

sealed class GetCharacterResult {
    data class Success(val characterModel: CharacterModel): GetCharacterResult()
    data class Failure(val failureType: FailureType): GetCharacterResult()
}
