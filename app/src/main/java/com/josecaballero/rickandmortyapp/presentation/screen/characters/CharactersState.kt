package com.josecaballero.rickandmortyapp.presentation.screen.characters

import com.josecaballero.rickandmortyapp.domain.model.CharacterModel

data class CharactersScreenState(
    val searchTerm: String = "",
    val status: CharactersState = CharactersState.Initial,
    val displayMessage: String = ""
) {
    sealed interface CharactersState {
        data object Initial : CharactersState
        data object Loading : CharactersState
        data object Empty : CharactersState
        data class Success(val characters: List<Character>) : CharactersState

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
