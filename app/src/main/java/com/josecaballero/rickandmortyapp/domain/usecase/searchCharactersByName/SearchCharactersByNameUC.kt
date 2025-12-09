package com.josecaballero.rickandmortyapp.domain.usecase.searchCharactersByName

import com.josecaballero.rickandmortyapp.data.repo.CharacterRepo
import com.josecaballero.rickandmortyapp.domain.model.CharacterModel
import com.josecaballero.rickandmortyapp.domain.util.FailureType
import com.josecaballero.rickandmortyapp.domain.util.ThrowableMapper
import javax.inject.Inject

class SearchCharactersByNameUC @Inject constructor(
    private val repository: CharacterRepo
) {
    suspend operator fun invoke(characterName: String): SearchCharactersByNameResult {

        if (characterName.isBlank()) {
            return SearchCharactersByNameResult.Success(emptyList())
        }

        val repoResult = repository.getCharactersByName(characterName)

        return repoResult.fold(
            onSuccess = { listCharacterModel ->
                val models = listCharacterModel.map { data ->
                    data.run {
                        CharacterModel(
                            id = id,
                            name = name,
                            status = status,
                            species = species,
                            origin = origin,
                            imageUrl = imageUrl
                        )
                    }
                }
                SearchCharactersByNameResult.Success(models)
            },

            onFailure = { throwable ->
                val failureType = ThrowableMapper.getFailureType(throwable)
                SearchCharactersByNameResult.Failure(failureType)
            }
        )
    }
}