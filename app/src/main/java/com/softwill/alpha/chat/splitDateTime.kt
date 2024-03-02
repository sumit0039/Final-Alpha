package com.softwill.alpha.chat

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun splitDateTime(dateTimeString: String): Pair<String, String>? {
    // Define the format of the combined date-time string
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    try {
        // Parse the combined date-time string into LocalDateTime
        val dateTime = LocalDateTime.parse(dateTimeString, formatter)
//        val dateTime = dateTimeString

        // Extract the date and time components
        val date = dateTime.toLocalDate().toString()
        val time = dateTime.toLocalTime().toString()

        return Pair(date, time)
    } catch (e: Exception) {
        // Handle parsing errors
        e.printStackTrace()
        return null
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun main() {
    // Example usage
    val dateTimeString = "2024-01-26 14:30:00"
    
    // Split date and time
    val result = splitDateTime(dateTimeString)

    // Print the result
    result?.let { (date, time) ->
        println("Date: $date")
        println("Time: $time")
    } ?: println("Failed to parse date-time string.")
}
