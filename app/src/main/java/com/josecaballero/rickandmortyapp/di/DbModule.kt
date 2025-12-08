package com.josecaballero.rickandmortyapp.di

import android.content.Context
import androidx.room.Room
import com.josecaballero.rickandmortyapp.data.source.local.sql.RickAndMortyDb
import com.josecaballero.rickandmortyapp.data.source.local.sql.dao.CharacterDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RickAndMortyDb {
        return Room.databaseBuilder(
            context,
            RickAndMortyDb::class.java,
            "rick_and_morty_db"
        ).build()
    }

    @Provides
    fun provideCharacterDao(database: RickAndMortyDb): CharacterDao {
        return database.characterDao()
    }
}