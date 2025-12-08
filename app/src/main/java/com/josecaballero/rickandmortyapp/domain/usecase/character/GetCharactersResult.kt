package com.josecaballero.rickandmortyapp.domain.usecase.character

import com.josecaballero.rickandmortyapp.domain.model.CharacterModel

sealed class GetCharactersResult {
    data class Success(val charactersModel: List<CharacterModel>): GetCharactersResult()
    data class Failure(val failureType: FailureType): GetCharactersResult()
}

sealed class FailureType {
    object NetworkError: FailureType()
    object EmptyQuery: FailureType()
    object SqlError: FailureType()
    object Error: FailureType()
}