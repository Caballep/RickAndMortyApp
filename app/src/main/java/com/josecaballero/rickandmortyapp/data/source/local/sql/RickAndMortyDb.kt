package com.josecaballero.rickandmortyapp.data.source.local.sql

import androidx.room.Database
import androidx.room.RoomDatabase
import com.josecaballero.rickandmortyapp.data.source.local.sql.dao.CharacterDao
import com.josecaballero.rickandmortyapp.data.source.local.sql.entity.CharacterEntity

@Database(
    entities = [CharacterEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RickAndMortyDb : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
}