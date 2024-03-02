package com.softwill.alpha.chat

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun convertDateFormat(originalDateString: String): String? {
    // Define the format of the original date string
    val originalFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    try {
        // Parse the original date string into LocalDate
        val originalDate = LocalDate.parse(originalDateString, originalFormatter)

        // Define the format of the target date string
        val targetFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        // Format the LocalDate into the target date string
        return originalDate.format(targetFormatter)
    } catch (e: Exception) {
        // Handle parsing errors
        e.printStackTrace()
        return null
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun main() {
    // Example usage
    val originalDateString = "2024-01-26"
    
    // Convert date format
    val result = convertDateFormat(originalDateString)

    // Print the result
    result?.let {
        println("Original Date: $originalDateString")
        println("Formatted Date: $result")
    } ?: println("Failed to parse the date string.")
}
