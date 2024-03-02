package com.softwill.alpha.institute_detail.model

data class FacultyStreamResponseItem(
    val classId: Int,
    val className: String,
    val faculty: String,
    val fees: List<Fee>,
    val instituteClassId: Int,
    val instituteFacultyId: Int,
    val instituteStreamId: Int,
    val stream: String
)