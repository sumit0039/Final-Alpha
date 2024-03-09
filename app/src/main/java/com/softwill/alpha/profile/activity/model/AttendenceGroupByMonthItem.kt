package com.softwill.alpha.profile.activity.model

data class AttendenceGroupByMonthItem(
    val count: Int,
    val month: String,
    val percentage: String,
    val studentId: Int
)