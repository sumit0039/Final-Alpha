package com.softwill.alpha.institute_detail.model.instituteDetailsModel

data class Institute(
    val aboutUs: String?=null,
    val assignDeanDate: String?=null,
    val bannerPath: String,
    val brochurePath: String,
    val createdAt: String,
    val dean: Dean ?=null,
    val deanEducation: String?=null,
    val deanId: Int,
    val desc: String?=null,
    val environmentRating: Double,
    val id: Int,
    val instituteRating: Double,
    val placementRating: Double,
    val policy: String?=null,
    val staffRating: Double,
    val teachingRating: Double,
    val website: String
)