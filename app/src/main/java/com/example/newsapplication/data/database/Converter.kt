package com.example.newsapplication.data.database

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class Converters {

    // Define multiple formatters for the two date formats
    private val formatters = listOf(
        DateTimeFormatter.ISO_DATE_TIME,
        DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
    )

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.atZone(ZoneId.systemDefault())?.toInstant()?.toString()
    }

    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        if (dateTimeString.isNullOrEmpty()) return null

        for (formatter in formatters) {
            try {
                return LocalDateTime.parse(dateTimeString, formatter)
            } catch (_: Exception) {

            }
        }

        return null
    }
}
