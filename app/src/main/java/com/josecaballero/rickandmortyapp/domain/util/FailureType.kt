package com.josecaballero.rickandmortyapp.domain.util

sealed class FailureType {
    object NetworkError: FailureType()
    object SqlError: FailureType()
    object Error: FailureType()
    object NotFound: FailureType()
}