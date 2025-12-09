package com.josecaballero.rickandmortyapp.data.transformer

import com.josecaballero.rickandmortyapp.data.repo.data.CharacterData
import com.josecaballero.rickandmortyapp.data.source.local.sql.entity.CharacterEntity
import com.josecaballero.rickandmortyapp.data.source.remote.rest.response.CharactersResponse

class CharacterTransformer {
    companion object {
        fun toData(entity: CharacterEntity): CharacterData {
            return entity.run {
                CharacterData(
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

        fun toEntity(characterDTO: CharactersResponse.Character): CharacterEntity {
            return characterDTO.run {
                CharacterEntity(
                    id = id,
                    name = name,
                    status = status,
                    species = species,
                    origin = origin.name,
                    imageUrl = image,
                    type = type,
                    created = created
                )
            }
        }
    }
}