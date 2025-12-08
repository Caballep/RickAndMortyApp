package com.josecaballero.rickandmortyapp.data.repo

import com.josecaballero.rickandmortyapp.data.repo.data.CharacterData

interface CharacterRepo {
    suspend fun getCharactersByName(name: String): Result<List<CharacterData>>
}