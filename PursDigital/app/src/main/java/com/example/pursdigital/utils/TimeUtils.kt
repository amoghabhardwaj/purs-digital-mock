package com.example.pursdigital.utils

import com.example.pursdigital.Hours
import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
    // Utility function to split hours string into a list
    fun splitHours(input: String): List<String> {
        val items = input.split(", ").map { it.trim() }  // Split by comma and space, then trim any leading/trailing whitespace
        return items.mapIndexed { index, timeRange ->
            if (index != items.lastIndex) "$timeRange," else timeRange
        }
    }

    // Utility function to combine opening hours into a map
    fun combineOpeningHours(hoursList: List<Hours>): Map<String, String> {
        val dayOrder = mapOf(
            "MON" to 1, "TUE" to 2, "WED" to 3, "THU" to 4,
            "FRI" to 5, "SAT" to 6, "SUN" to 7
        )
        val dayNames = mapOf(
            "MON" to "Monday", "TUE" to "Tuesday", "WED" to "Wednesday",
            "THU" to "Thursday", "FRI" to "Friday", "SAT" to "Saturday", "SUN" to "Sunday"
        )
        val combinedHours = mutableMapOf<String, MutableList<String>>()

        // Sort hoursList by start time after ensuring proper format conversion
        val sortedHoursList = hoursList.sortedBy { hours ->
            timeToMinutes(convertTo12HourFormat(hours.start_local_time))
        }

        sortedHoursList.forEach { hours ->
            val timeRange = "${convertTo12HourFormat(hours.start_local_time)} - ${convertTo12HourFormat(hours.end_local_time)}"
            val dayAbbreviation = hours.day_of_week
            combinedHours.getOrPut(dayAbbreviation) { mutableListOf() }.add(timeRange)
        }

        // Sort the days by the defined day order and join the times with commas if multiple ranges
        val sortedByDay = combinedHours.toSortedMap(compareBy { dayOrder[it] ?: Int.MAX_VALUE })
        return sortedByDay.mapKeys { entry -> dayNames[entry.key] ?: entry.key }
            .mapValues { entry -> entry.value.joinToString(", ") }
    }


    // Utility function to convert time to 12-hour format
    fun convertTo12HourFormat(time: String?): String {
        if (time == null) {
            println("Null time provided")
            return "12:00 AM" // Return a default time in case of null input
        }
        try {
            val parts = time.split(":")
            var hours = parts[0].toInt()
            val minutes = parts[1]
            val ampm = if (hours==24) "AM" else if (hours < 12) "AM" else "PM"
            if (hours > 12) hours -= 12
            if (hours == 0) hours = 12
            return "$hours:$minutes $ampm"
        } catch (e: Exception) {
            println("Failed to convert time: $time")
            return "12:00 AM" // Return a default time in case of exceptions
        }
    }

    fun timeToMinutes(time: String?): Int {
        if (time == null) return 0
        val matchResult = """(\d+):(\d+)\s(AM|PM)""".toRegex().find(time)
        if (matchResult == null) {
            println("Time format error: $time")
            return 0 // Default to 0 in case of format error
        }
        val (hour, minute, period) = matchResult.destructured
        val hourIn24 = when {
            period == "PM" && hour != "12" -> hour.toInt() + 12
            period == "AM" && hour == "12" -> 0
            else -> hour.toInt()
        }
        return hourIn24 * 60 + minute.toInt()
    }



    // Utility function to get the current day and time
    fun getCurrentDayAndTime(): Pair<String, String> {
        val sdfDay = SimpleDateFormat("EEE", Locale.getDefault())
        val sdfTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val currentDate = Date()
        return sdfDay.format(currentDate).uppercase(Locale.getDefault()) to sdfTime.format(currentDate)
    }

    // Utility function to get the next time block
    fun getNextTimeBlock(currentTime: String, hoursList: List<Hours>): String? {
        val timesToday = hoursList.filter { it.start_local_time > currentTime }
            .sortedBy { it.start_local_time }

        return if (timesToday.isNotEmpty()) {
            timesToday.first().start_local_time
        } else {
            null
        }
    }

    // Function to determine the opening status of the restaurant
    fun determineOpeningStatus(currentDay: String, currentTime: String, hoursList: List<Hours>): String {
        val todayHours = hoursList.filter { it.day_of_week == currentDay }
        val todayTimes = todayHours.sortedBy { it.start_local_time }

        val currentTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val currentDate = currentTimeFormat.parse(currentTime)

        for (i in todayTimes.indices) {
            val startTime = currentTimeFormat.parse(todayTimes[i].start_local_time)
            val endTimeString = todayTimes[i].end_local_time
            var endTime = currentTimeFormat.parse(endTimeString)

            // Handle special case where end time is "24:00:00"
            if (endTimeString == "24:00:00") {
                endTime = currentTimeFormat.parse("00:00:00") // Set to midnight

                // Check if it's open 24 hours
                if (todayTimes[i].start_local_time == "00:00:00") {
                    return "Open 24 hours"
                }

                // Display "Open until midnight"
                val nextDay = getNextDay(todayTimes[i].day_of_week)
                val nextDayFormatted = convertTo12HourFormat("00:00:00")

                // Check if current time is before "24:00:00" (midnight)
                if (currentDate != null && currentDate.before(endTime)) {
                    return "Open until ${convertTo12HourFormat("23:59:59")}, reopens at $nextDayFormatted"
                }

                return "Open until midnight"
            }

            if (currentDate != null) {
                if (currentDate.after(startTime) && currentDate.before(endTime)) {
                    val nextTimeBlock = getNextTimeBlock(todayTimes[i].end_local_time, todayTimes)
                    return if (nextTimeBlock != null) {
                        "Open until ${convertTo12HourFormat(todayTimes[i].end_local_time)}, reopens at ${convertTo12HourFormat(nextTimeBlock)}"
                    } else {
                        "Open until ${convertTo12HourFormat(todayTimes[i].end_local_time)}"
                    }
                } else if (currentDate.before(startTime)) {
                    return "Opens again at ${convertTo12HourFormat(todayTimes[i].start_local_time)}"
                }
            }
        }

        // Handle case when the shop is closed today
        var nextDay = getNextDay(currentDay)
        while (hoursList.none { it.day_of_week == nextDay }) {
            nextDay = getNextDay(nextDay)
        }

        val nextDayHours = hoursList.filter { it.day_of_week == nextDay }
            .sortedBy { it.start_local_time }
            .firstOrNull()

        return nextDayHours?.let {
            "Opens ${nextDay} ${convertTo12HourFormat(it.start_local_time)}"
        } ?: "Closed"
    }


    // Helper function to get the next day of the week
    fun getNextDay(currentDay: String): String {
        val dayOrder = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
        val currentIndex = dayOrder.indexOf(currentDay)
        val nextIndex = (currentIndex + 1) % dayOrder.size
        return dayOrder[nextIndex]
    }


    // Function to determine the status of the restaurant (open, closing soon, closed)
    fun getStatus(currentDay: String, currentTime: String, hoursList: List<Hours>): String {
        val todayHours = hoursList.filter { it.day_of_week == currentDay }
        val todayTimes = todayHours.sortedBy { it.start_local_time }

        val currentTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val currentDate = currentTimeFormat.parse(currentTime)

        for (i in todayTimes.indices) {
            val startTime = currentTimeFormat.parse(todayTimes[i].start_local_time)
            val endTime = currentTimeFormat.parse(todayTimes[i].end_local_time)

            if (currentDate != null) {
                if (currentDate.after(startTime) && currentDate.before(endTime)) {
                    val closingTime = Calendar.getInstance().apply {
                        if (endTime != null) {
                            time = endTime
                        }
                    }
                    closingTime.add(Calendar.HOUR_OF_DAY, -1)
                    return if (currentDate.after(closingTime.time)) {
                        "closing_soon"
                    } else {
                        "open"
                    }
                } else if (currentDate.before(startTime)) {
                    return "closed"
                }
            }
        }

        return "closed"
    }
}