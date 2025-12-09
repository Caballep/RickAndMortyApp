package com.josecaballero.rickandmortyapp.presentation.screen.characterDetail

import com.josecaballero.rickandmortyapp.domain.model.CharacterModel

data class CharacterDetailState(
    val character: Character? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    data class Character(
        val name: String,
        val status: String,
        val species: String,
        val origin: String,
        val imageUrl: String
    ) {
        companion object Companion {
            fun fromModel(model: CharacterModel): Character {
                return model.run {
                    Character(
                        name = name,
                        status = status,
                        species = species,
                        origin = origin,
                        imageUrl = imageUrl
                    )
                }
            }
        }
    }
}