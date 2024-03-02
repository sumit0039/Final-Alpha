package com.softwill.alpha.institute.attendance.model

data class StudentAttendenceListingItem(
    val attendanceDate: String,
    val count: Int,
    val month: String,
    val percentage: String,
    val studentId: Int
)