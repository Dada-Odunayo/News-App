package com.example.newsapplication.utilities

import com.example.newsapplication.model.Article
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZonedDateTime

fun LocalDateTime.formatDurationBetween(): String {
    val now = LocalDateTime.now()
    val duration = Duration.between(this, now)

    val seconds = duration.seconds
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    val timeAgo = when {
        seconds < 60 -> "$seconds seconds ago"
        minutes < 60 -> "$minutes minutes ago"
        hours < 24 -> "$hours hours ago"
        else -> "$days days ago"
    }
    return timeAgo
}
fun Article.getPublishedAtAsLocalDateTime(): LocalDateTime {
    return ZonedDateTime.parse(publishedAt).toLocalDateTime()
}
