package com.josecaballero.rickandmortyapp.data.repo

import com.josecaballero.rickandmortyapp.data.source.remote.rest.RickAndMortyAPI
import com.josecaballero.rickandmortyapp.data.repo.data.CharacterData
import com.josecaballero.rickandmortyapp.data.source.local.sql.dao.CharacterDao
import com.josecaballero.rickandmortyapp.data.transformer.CharacterTransformer
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CharacterRepoImpl @Inject constructor(
    private val api: RickAndMortyAPI,
    private val characterDao: CharacterDao
) : CharacterRepo {

    override suspend fun getCharactersByName(name: String): Result<List<CharacterData>> {
        return try {
            val characterResponse = api.getCharactersByName(name)

            val newCharacterEntities = characterResponse.results.map { characterDTO ->
                CharacterTransformer.toEntity(characterDTO)
            }

            withContext(Dispatchers.IO) {
                characterDao.insertCharacters(newCharacterEntities)
            }

            val characterEntities = characterDao.getCharactersByName(name)

            val charactersData = characterEntities.map { characterEntity ->
                CharacterTransformer.toData(characterEntity)
            }

            Result.success(charactersData)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}