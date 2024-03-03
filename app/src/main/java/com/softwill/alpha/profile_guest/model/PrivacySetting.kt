package com.softwill.alpha.profile_guest.model

data class PrivacySetting(
    val aboutBio: Boolean,
    val dob: Boolean,
    val email: Boolean,
    val mobileNumber: Boolean,
    val profilePicture: Boolean
)