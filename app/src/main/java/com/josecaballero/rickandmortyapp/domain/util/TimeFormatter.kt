package com.josecaballero.rickandmortyapp.domain.util

import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.ZoneId

class TimeFormatter {
    companion object {
        private val DISPLAY_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            .withZone(ZoneId.systemDefault())

        fun formatToStandardDate(isoString: String): String {
            return try {
                val instant = Instant.parse(isoString)
                DISPLAY_DATE_FORMATTER.format(instant)
            } catch (e: Exception) {
                isoString
            }
        }
    }
}