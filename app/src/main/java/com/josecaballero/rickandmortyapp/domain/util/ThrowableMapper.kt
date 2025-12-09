package com.josecaballero.rickandmortyapp.domain.util

import android.database.sqlite.SQLiteException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import java.net.ConnectException

class ThrowableMapper {
    companion object {
        fun getFailureType(throwable: Throwable): FailureType {
            return when (throwable) {
                // Ktor IO/Connection Failures
                is HttpRequestTimeoutException,
                is ConnectException -> {
                    FailureType.NetworkError
                }

                // Ktor HTTP Status Failures
                is ClientRequestException,
                is ServerResponseException -> {
                    FailureType.NetworkError
                }

                // Room/Database Exceptions
                is SQLiteException -> {
                    FailureType.SqlError
                }

                // Others
                else -> {
                    FailureType.Error
                }
            }
        }
    }
}