package com.example.collegeeventplanner.util

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val displayTimeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    fun formatDate(date: Date): String = dateFormat.format(date)
    fun formatTime(time: Date): String = timeFormat.format(time)
    fun parseDate(dateString: String): Date? = dateFormat.parse(dateString)
    fun parseTime(timeString: String): Date? = timeFormat.parse(timeString)

    fun formatDateForDisplay(date: Date): String = displayDateFormat.format(date)
    fun formatTimeForDisplay(time: Date): String = displayTimeFormat.format(time)

    fun combineDateTime(date: Date, time: Date): Date {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, time.hours)
            set(Calendar.MINUTE, time.minutes)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.time
    }

    fun isFutureDateTime(date: Date, time: Date): Boolean {
        val combined = combineDateTime(date, time)
        return combined.after(Date())
    }

    fun validateEventDateTime(date: String, time: String): Boolean {
        val parsedDate = parseDate(date) ?: return false
        val parsedTime = parseTime(time) ?: return false
        return isFutureDateTime(parsedDate, parsedTime)
    }
}