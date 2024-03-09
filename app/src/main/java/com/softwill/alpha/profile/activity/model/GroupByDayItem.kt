package com.softwill.alpha.profile.activity.model

data class GroupByDayItem(
    val attendanceDate: String,
    val count: Int,
    val month: String,
    val percentage: String,
    val studentId: Int
)