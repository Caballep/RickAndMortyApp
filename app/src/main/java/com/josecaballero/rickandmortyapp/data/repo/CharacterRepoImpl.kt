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
            val newCharacterEntities = characterResponse.results.map(CharacterTransformer::toEntity)

            withContext(Dispatchers.IO) {
                characterDao.insertCharacters(newCharacterEntities)
            }

            val charactersData = newCharacterEntities.map(CharacterTransformer::toData)
            Result.success(charactersData)

        } catch (e: Exception) {
            val localCharacters = characterDao.getCharactersByName(name)

            if (localCharacters.isNotEmpty()) {
                Result.success(localCharacters.map(CharacterTransformer::toData))
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getCharactersById(id: Int): Result<CharacterData?> {
        return try {
            val entity = characterDao.getCharacterById(id)
            val data = entity?.let { CharacterTransformer.toData(it) }
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}