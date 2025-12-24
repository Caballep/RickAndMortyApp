package com.josecaballero.rickandmortyapp.domain.util

sealed class DomainFailure {
    object NetworkError: DomainFailure()
    object SqlError: DomainFailure()
    object Error: DomainFailure()
    object NotFound: DomainFailure()
}