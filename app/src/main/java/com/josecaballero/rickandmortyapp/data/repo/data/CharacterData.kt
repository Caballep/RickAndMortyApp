package com.josecaballero.rickandmortyapp.data.repo.data

data class CharacterData(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val origin: String,
    val imageUrl: String
)