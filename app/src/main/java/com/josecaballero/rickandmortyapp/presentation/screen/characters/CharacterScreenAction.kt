package com.josecaballero.rickandmortyapp.presentation.screen.characters

sealed class CharacterScreenAction {
    data class Search(val name: String) : CharacterScreenAction()
}
