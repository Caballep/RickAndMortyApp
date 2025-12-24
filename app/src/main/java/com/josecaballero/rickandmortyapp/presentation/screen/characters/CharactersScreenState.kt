package com.josecaballero.rickandmortyapp.presentation.screen.characters

import com.josecaballero.rickandmortyapp.domain.model.CharacterModel

data class CharactersScreenState(
    val searchTerm: String = "",
    val status: CharactersStatus = CharactersStatus.Initial
) {
    sealed interface CharactersStatus {
        data object Initial : CharactersStatus
        data object Loading : CharactersStatus
        data object Empty : CharactersStatus
        data class Success(val characters: List<Character>) : CharactersStatus

        data class Character(
            val id: Int,
            val name: String,
            val imageUrl: String
        ) {
            companion object Companion {
                fun fromModel(model: CharacterModel): Character {
                    return model.run {
                        Character(
                            id = id,
                            name = name,
                            imageUrl = imageUrl
                        )
                    }
                }
            }
        }
    }
}
