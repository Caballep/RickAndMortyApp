package com.josecaballero.rickandmortyapp.di

import com.josecaballero.rickandmortyapp.data.source.remote.rest.RickAndMortyAPI
import dagger.Module // 💡 Required Import
import dagger.Provides
import dagger.hilt.InstallIn // 💡 Required Import
import dagger.hilt.components.SingletonComponent // 💡 Required Import
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module // 1. Annotate the containing object as a Module
@InstallIn(SingletonComponent::class) // 2. Specify the lifecycle scope (Application lifetime)
object NetworkModule { // 3. The functions MUST be defined inside this object

    @Provides
    @Singleton
    fun provideKtorClient(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
    }

    @Provides
    @Singleton
    fun provideRickAndMortyAPI(client: HttpClient): RickAndMortyAPI {
        return RickAndMortyAPI(client)
    }
}