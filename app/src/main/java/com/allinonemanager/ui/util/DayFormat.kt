package com.allinonemanager.ui.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Cards store statement / due as a day of the month ("15"); these turn that into
// something readable. java.time needs API 26 and minSdk is 24, so this uses Calendar.

/** 1 -> "1st", 2 -> "2nd", 11 -> "11th", 22 -> "22nd" */
fun ordinal(day: Int): String {
    val suffix = if (day % 100 in 11..13) "th" else when (day % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
    return "$day$suffix"
}

/** "15" -> "15th"; anything that is not a day is returned as is */
fun ordinalDay(day: String?): String = day?.toIntOrNull()?.takeIf { it in 1..31 }?.let(::ordinal) ?: day.orEmpty()

/**
 * Next date (today or later) that falls on [day] of the month, e.g. day 4 on 21 Oct -> 4 Nov.
 * Months that are too short use their last day (31 -> 30 Nov). Null if [day] is not 1 - 31.
 */
fun nextDateForDay(day: String?, today: Calendar = Calendar.getInstance()): Calendar? {
    val d = day?.toIntOrNull()?.takeIf { it in 1..31 } ?: return null
    val date = (today.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }
    val todayDay = today.get(Calendar.DAY_OF_MONTH)
    // Already past this month's date: use next month
    if (todayDay > d.coerceAtMost(date.getActualMaximum(Calendar.DAY_OF_MONTH))) date.add(Calendar.MONTH, 1)
    date.set(Calendar.DAY_OF_MONTH, d.coerceAtMost(date.getActualMaximum(Calendar.DAY_OF_MONTH)))
    return date
}

/** Day of the month -> its next date as "4 Nov"; "-" if it is not a day */
fun nextDateLabel(day: String?): String =
    nextDateForDay(day)?.let { SimpleDateFormat("d MMM", Locale.US).format(it.time) } ?: "-"

/** "2026-11-04" -> "4 Nov 2026"; text that is not yyyy-MM-dd is returned as is */
fun readableDate(date: String?): String {
    val parsed = date?.let {
        runCatching { SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }.parse(it) }.getOrNull()
    } ?: return date?.takeIf { it.isNotBlank() } ?: "-"
    return SimpleDateFormat("d MMM yyyy", Locale.US).format(parsed)
}
