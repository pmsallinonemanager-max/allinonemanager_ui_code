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

// Puts [date] on day [d] of its month; too-short months use their last day
private fun Calendar.onDay(d: Int): Calendar = apply {
    set(Calendar.DAY_OF_MONTH, 1)
    set(Calendar.DAY_OF_MONTH, d.coerceAtMost(getActualMaximum(Calendar.DAY_OF_MONTH)))
}

/** Latest date (today or earlier) that falls on [day], e.g. day 15 on 21 Oct -> 15 Oct. */
fun lastDateForDay(day: String?, today: Calendar = Calendar.getInstance()): Calendar? {
    val d = day?.toIntOrNull()?.takeIf { it in 1..31 } ?: return null
    val date = (today.clone() as Calendar).onDay(d)
    // This month's date is still ahead: use last month
    if (date.after(today)) date.apply { set(Calendar.DAY_OF_MONTH, 1); add(Calendar.MONTH, -1) }.onDay(d)
    return date
}

/**
 * Current bill cycle: the last statement generated and the due date that follows it,
 * e.g. statement 15 / due 4 on 21 Oct -> (15 Oct, 4 Nov). Null for a missing day.
 */
fun currentCycleDates(statementDay: String?, dueDay: String?): Pair<Calendar?, Calendar?> {
    val statement = lastDateForDay(statementDay)
    val d = dueDay?.toIntOrNull()?.takeIf { it in 1..31 }
    val due = when {
        d == null -> null
        statement == null -> nextDateForDay(dueDay)
        // First due day after the statement
        else -> (statement.clone() as Calendar).onDay(d).let {
            if (it.after(statement)) it else it.apply { set(Calendar.DAY_OF_MONTH, 1); add(Calendar.MONTH, 1) }.onDay(d)
        }
    }
    return statement to due
}

/** [currentCycleDates] as "15 Oct" / "4 Nov"; "-" for a missing day */
fun currentCycleLabels(statementDay: String?, dueDay: String?): Pair<String, String> {
    val format = SimpleDateFormat("d MMM", Locale.US)
    val (statement, due) = currentCycleDates(statementDay, dueDay)
    return (statement?.let { format.format(it.time) } ?: "-") to (due?.let { format.format(it.time) } ?: "-")
}

/** Next statement date after today, e.g. day 15 on 10 Nov -> "15 Nov"; null if [day] is not a day */
fun nextStatementLabel(day: String?): String? {
    val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }
    return nextDateForDay(day, tomorrow)?.let { SimpleDateFormat("d MMM", Locale.US).format(it.time) }
}

/** "2026-10-15" -> "Oct 2026"; "-" if it is not yyyy-MM-dd */
fun monthLabel(date: String?): String = date?.let {
    runCatching { SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }.parse(it) }.getOrNull()
}?.let { SimpleDateFormat("MMM yyyy", Locale.US).format(it) } ?: "-"

/** "2026-11-04" -> "4 Nov 2026"; text that is not yyyy-MM-dd is returned as is */
fun readableDate(date: String?): String {
    val parsed = date?.let {
        runCatching { SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }.parse(it) }.getOrNull()
    } ?: return date?.takeIf { it.isNotBlank() } ?: "-"
    return SimpleDateFormat("d MMM yyyy", Locale.US).format(parsed)
}
