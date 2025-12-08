package com.josecaballero.rickandmortyapp.di

import com.josecaballero.rickandmortyapp.data.repo.CharacterRepo
import com.josecaballero.rickandmortyapp.data.repo.CharacterRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Binds
    @Singleton
    abstract fun bindCharacterRepository(
        characterRepoImpl: CharacterRepoImpl
    ): CharacterRepo
}