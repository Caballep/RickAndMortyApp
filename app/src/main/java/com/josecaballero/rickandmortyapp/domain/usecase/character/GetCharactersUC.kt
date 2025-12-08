package com.josecaballero.rickandmortyapp.domain.usecase.character

import com.josecaballero.rickandmortyapp.data.repo.CharacterRepo
import com.josecaballero.rickandmortyapp.domain.model.CharacterModel
import javax.inject.Inject

class GetCharactersUC @Inject constructor(
    private val repository: CharacterRepo
) {
    suspend operator fun invoke(characterName: String): GetCharactersResult {

        if (characterName.isBlank()) {
            return GetCharactersResult.Failure(FailureType.EmptyQuery)
        }

        val repoResult = repository.getCharactersByName(characterName)

        return repoResult.fold(
            onSuccess = { listCharacterData ->
                val models = listCharacterData.map { data ->
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
                GetCharactersResult.Success(models)
            },

            onFailure = { throwable ->
                when (throwable) {
                    // Ktor IO/Connection Failures
                    is io.ktor.client.plugins.HttpRequestTimeoutException,
                    is java.net.ConnectException -> {
                        GetCharactersResult.Failure(FailureType.NetworkError)
                    }

                    // Ktor HTTP Status Failures
                    is io.ktor.client.plugins.ClientRequestException,
                    is io.ktor.client.plugins.ServerResponseException -> {
                        GetCharactersResult.Failure(FailureType.NetworkError)
                    }

                    // Room/Database Exceptions
                    is android.database.sqlite.SQLiteException -> {
                        GetCharactersResult.Failure(FailureType.SqlError)
                    }

                    // Others
                    else -> {
                        GetCharactersResult.Failure(FailureType.Error)
                    }
                }
            }
        )
    }
}