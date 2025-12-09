package com.josecaballero.rickandmortyapp.presentation.screen.characterDetail

import com.josecaballero.rickandmortyapp.domain.model.CharacterModel

data class CharacterDetailScreenState(
    val status: CharacterDetailStatus = CharacterDetailStatus.Loading,
    val displayMessage: String = ""
) {
    sealed interface CharacterDetailStatus {
        data object Loading : CharacterDetailStatus
        data class Error(val message: String) : CharacterDetailStatus
        data class Success(val character: DetailCharacter) : CharacterDetailStatus
    }

    data class DetailCharacter(
        val name: String,
        val status: String,
        val species: String,
        val origin: String,
        val imageUrl: String,
        val type: String,
        val created: String
    ) {
        companion object Companion {
            fun fromModel(model: CharacterModel): DetailCharacter {
                return model.run {
                    DetailCharacter(
                        name = name,
                        status = status,
                        species = species,
                        origin = origin,
                        imageUrl = imageUrl,
                        type = type,
                        created = created
                    )
                }
            }
        }
    }
}