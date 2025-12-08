package com.josecaballero.rickandmortyapp.data.source.remote.rest.response

import kotlinx.serialization.Serializable

@Serializable
data class CharactersResponse(
    val info: Info,
    val results: List<Character>
) {
    @Serializable
    data class Info(
        val count: Int,
        val pages: Int,
        val next: String? = null,
        val prev: String? = null
    )

    @Serializable
    data class Character(
        val id: Int,
        val name: String,
        val status: String,
        val species: String,
        val type: String,
        val gender: String,
        val origin: LocationInfo,
        val location: LocationInfo,
        val image: String,
        val episode: List<String>,
        val url: String,
        val created: String
    ) {
        @Serializable
        data class LocationInfo(
            val name: String,
            val url: String
        )
    }
}