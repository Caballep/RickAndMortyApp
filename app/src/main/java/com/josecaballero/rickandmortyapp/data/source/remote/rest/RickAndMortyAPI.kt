package com.josecaballero.rickandmortyapp.data.source.remote.rest

import com.josecaballero.rickandmortyapp.data.source.remote.rest.response.CharactersResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class RickAndMortyAPI(private val client: HttpClient) {

    companion object {
        private const val BASE_URL = "https://rickandmortyapi.com/api/"
        private const val CHARACTER_ENDPOINT = "character/"
    }

    suspend fun getCharactersByName(name: String): CharactersResponse {
        return client.get(BASE_URL + CHARACTER_ENDPOINT) {
            url {
                parameters.append("name", name)
            }
        }.body()
    }
}