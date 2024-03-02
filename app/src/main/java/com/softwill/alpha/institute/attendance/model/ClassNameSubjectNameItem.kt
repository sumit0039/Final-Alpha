package com.softwill.alpha.institute.attendance.model

data class ClassNameSubjectNameItem(
    val classId: Int,
    val className: String,
    val class_subjects: List<ClassSubject>,
    val streamName: String
)