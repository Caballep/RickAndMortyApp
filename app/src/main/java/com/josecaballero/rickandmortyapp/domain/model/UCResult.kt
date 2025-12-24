package com.josecaballero.rickandmortyapp.domain.model

sealed class UCResult<out T> {
    data class Success<out T>(val data: T) : UCResult<T>()
    data class Failure(val type: com.josecaballero.rickandmortyapp.domain.util.DomainFailure) : UCResult<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure

    fun getOrNull(): T? = (this as? Success)?.data

    fun failureOrNull(): com.josecaballero.rickandmortyapp.domain.util.DomainFailure? = (this as? Failure)?.type

    inline fun <R> fold(
        onSuccess: (T) -> R,
        onFailure: (com.josecaballero.rickandmortyapp.domain.util.DomainFailure) -> R
    ): R = when (this) {
        is Success -> onSuccess(data)
        is Failure -> onFailure(type)
    }
}