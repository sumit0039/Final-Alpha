package com.softwill.alpha.institute.attendance.model

data class Student(
    var User: User,
    var attendancePercentage: String,
    var firstName: String,
    var present: Int,
    var lastName: String,
    var rollNumber: String,
    var studentId: Int,
    var userId: Int,
    var userName: String
)