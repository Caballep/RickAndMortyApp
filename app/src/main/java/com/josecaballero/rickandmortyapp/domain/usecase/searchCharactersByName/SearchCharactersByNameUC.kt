package com.josecaballero.rickandmortyapp.domain.usecase.searchCharactersByName

import com.josecaballero.rickandmortyapp.data.repo.CharacterRepo
import com.josecaballero.rickandmortyapp.domain.model.CharacterModel
import com.josecaballero.rickandmortyapp.domain.model.UCResult
import com.josecaballero.rickandmortyapp.domain.util.ThrowableMapper
import com.josecaballero.rickandmortyapp.domain.util.TimeFormatter
import javax.inject.Inject

class SearchCharactersByNameUC @Inject constructor(
    private val repository: CharacterRepo
) {
    suspend operator fun invoke(characterName: String): UCResult<List<CharacterModel>> {

        if (characterName.isBlank()) {
            return UCResult.Success(emptyList())
        }

        val repoResult = repository.getCharactersByName(characterName)

        return repoResult.fold(
            onSuccess = { listCharacterModel ->
                val models = listCharacterModel.map { data ->
                    data.run {
                        val created = TimeFormatter.formatToStandardDate(created)
                        CharacterModel(
                            id = id,
                            name = name,
                            status = status,
                            species = species,
                            origin = origin,
                            imageUrl = imageUrl,
                            type = type,
                            created = created
                        )
                    }
                }
                UCResult.Success(models)
            },

            onFailure = { throwable ->
                val failureType = ThrowableMapper.getFailureType(throwable)
                UCResult.Failure(failureType)
            }
        )
    }
}