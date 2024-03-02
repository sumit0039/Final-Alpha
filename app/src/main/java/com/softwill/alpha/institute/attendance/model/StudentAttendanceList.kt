package com.softwill.alpha.institute.attendance.model

data class StudentAttendanceList(
    val classTeacher: String,
    val streamName: String,
    val students: MutableList<Student>
)