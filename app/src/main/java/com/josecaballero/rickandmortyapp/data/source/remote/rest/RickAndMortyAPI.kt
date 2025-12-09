package com.josecaballero.rickandmortyapp.data.source.remote.rest

import com.josecaballero.rickandmortyapp.data.source.remote.rest.response.CharactersResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.HttpStatusCode

class RickAndMortyAPI(private val client: HttpClient) {

    companion object {
        private const val BASE_URL = "https://rickandmortyapi.com/api/"
        private const val CHARACTER_ENDPOINT = "character/"
    }

    suspend fun getCharactersByName(name: String): CharactersResponse {
        val httpResponse: HttpResponse = client.get(BASE_URL + CHARACTER_ENDPOINT) {
            url {
                parameters.append("name", name)
            }
            expectSuccess = false
        }

        return when (httpResponse.status) {
            HttpStatusCode.OK -> {
                httpResponse.body<CharactersResponse>()
            }

            // REMINDER 4 THE INTERVIEW: When a name doesnt match the API answers with a 404 and a custom JSON instead of empty JSON, thats the reason for this:
            HttpStatusCode.NotFound -> {
                CharactersResponse(
                    info = CharactersResponse.Info(count = 0, pages = 0),
                    results = emptyList()
                )
            }

            else -> {
                throw RuntimeException(
                    "API request failed with status: ${httpResponse.status.value}"
                )
            }
        }
    }
}