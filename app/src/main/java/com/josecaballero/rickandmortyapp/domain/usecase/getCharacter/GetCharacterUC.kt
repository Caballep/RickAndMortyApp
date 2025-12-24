package com.josecaballero.rickandmortyapp.domain.usecase.getCharacter

import com.josecaballero.rickandmortyapp.data.repo.CharacterRepo
import com.josecaballero.rickandmortyapp.domain.model.CharacterModel
import com.josecaballero.rickandmortyapp.domain.model.UCResult
import com.josecaballero.rickandmortyapp.domain.util.DomainFailure
import com.josecaballero.rickandmortyapp.domain.util.ThrowableMapper
import com.josecaballero.rickandmortyapp.domain.util.TimeFormatter
import javax.inject.Inject

class GetCharacterUC @Inject constructor(
    private val repository: CharacterRepo
) {
    suspend operator fun invoke(id: Int): UCResult<CharacterModel> {
        val repoResult = repository.getCharactersById(id)

        return repoResult.fold(
            onSuccess = { data ->
                if (data == null) {
                    return UCResult.Failure(DomainFailure.NotFound)
                }
                val created = TimeFormatter.formatToStandardDate(data.created)
                val model = CharacterModel(
                    id = data.id,
                    name = data.name,
                    status = data.status,
                    species = data.species,
                    origin = data.origin,
                    imageUrl = data.imageUrl,
                    type = data.type,
                    created = created
                )

                UCResult.Success(model)
            },
            onFailure = { throwable ->
                val failureType = ThrowableMapper.getFailureType(throwable)
                UCResult.Failure(failureType)
            }
        )
    }

}