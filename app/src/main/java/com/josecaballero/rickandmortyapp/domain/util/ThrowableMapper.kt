package com.josecaballero.rickandmortyapp.domain.util

import android.database.sqlite.SQLiteException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import java.net.ConnectException

class ThrowableMapper {
    companion object {
        fun getFailureType(throwable: Throwable): DomainFailure {
            return when (throwable) {
                // Ktor IO/Connection Failures
                is HttpRequestTimeoutException,
                is ConnectException -> {
                    DomainFailure.NetworkError
                }

                // Ktor HTTP Status Failures
                is ClientRequestException,
                is ServerResponseException -> {
                    DomainFailure.NetworkError
                }

                // Room/Database Exceptions
                is SQLiteException -> {
                    DomainFailure.SqlError
                }

                // Others
                else -> {
                    DomainFailure.Error
                }
            }
        }
    }
}