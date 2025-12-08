package com.josecaballero.rickandmortyapp.data.source.local.sql.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.josecaballero.rickandmortyapp.data.source.local.sql.entity.CharacterEntity

@Dao
interface CharacterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<CharacterEntity>)

    @Query("SELECT * FROM characters WHERE name LIKE '%' || :searchQuery || '%'")
    suspend fun getCharactersByName(searchQuery: String): List<CharacterEntity>
}