package com.josecaballero.rickandmortyapp.data.repo

import android.util.Log
import com.josecaballero.rickandmortyapp.data.source.remote.rest.RickAndMortyAPI
import com.josecaballero.rickandmortyapp.data.repo.data.CharacterData
import com.josecaballero.rickandmortyapp.data.source.local.sql.dao.CharacterDao
import com.josecaballero.rickandmortyapp.data.source.local.sql.entity.CharacterEntity
import com.josecaballero.rickandmortyapp.data.transformer.CharacterTransformer
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CharacterRepoImpl @Inject constructor(
    private val api: RickAndMortyAPI,
    private val characterDao: CharacterDao
) : CharacterRepo {

    private val TAG = this::class.simpleName

    override suspend fun getCharactersByName(name: String): Result<List<CharacterData>> {
        var networkException: Exception? = null
        var sqlException: Exception? = null

        try {
            val characterResponse = api.getCharactersByName(name)
            val entities = characterResponse.results.map(CharacterTransformer::toEntity)

            withContext(Dispatchers.IO) {
                characterDao.insertCharacters(entities)
            }
        } catch (e: Exception) {
            networkException = e
            Log.i(TAG, e.message ?: "")
        }

        var localCharacters = emptyList<CharacterEntity>()
        try {
            localCharacters = withContext(Dispatchers.IO) {
                characterDao.getCharactersByName(name)
            }
        } catch (e: Exception) {
            sqlException = e
            Log.i(TAG, e.message ?: "")
        }

        return when {
            localCharacters.isNotEmpty() -> {
                Result.success(localCharacters.map(CharacterTransformer::toData))
            }

            networkException != null -> {
                Result.failure(networkException)
            }

            sqlException != null -> {
                Result.failure(sqlException)
            }

            else -> {
                // Or a Failure? It's up to Product/UI Flow
                Result.success(emptyList())
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