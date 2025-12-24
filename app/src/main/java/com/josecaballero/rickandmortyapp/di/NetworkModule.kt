package com.josecaballero.rickandmortyapp.di

import com.josecaballero.rickandmortyapp.data.source.remote.rest.RickAndMortyAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideKtorClient(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = false
                })
            }

            expectSuccess = false
            // TODO: Add Validator?? (aka Interceptor) - logging, auth headers, retry/timeout
        }
    }

    @Provides
    @Singleton
    fun provideRickAndMortyAPI(client: HttpClient): RickAndMortyAPI {
        return RickAndMortyAPI(client)
    }
}