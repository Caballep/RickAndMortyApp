package com.josecaballero.rickandmortyapp.presentation.screen

import com.josecaballero.rickandmortyapp.domain.model.CharacterModel

data class CharacterState(
    val characters: List<Character> = emptyList(),
    val searchTerm: String = "Rick",
    val isLoading: Boolean = false,
    val error: String? = null
) {
    data class Character(
        val id: Int,
        val name: String,
        val status: String,
        val species: String,
        val origin: String,
        val imageUrl: String
    ) {
        companion object {
            fun fromModel(model: CharacterModel): Character {
                return model.run {
                    Character(
                        id = id,
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