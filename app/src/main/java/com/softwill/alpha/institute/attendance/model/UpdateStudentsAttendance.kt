package com.softwill.alpha.institute.attendance.model

data class UpdateStudentsAttendance(
    var attendances: List<Attendance>,
    var classId: Int,
    var subjectId: Int
)