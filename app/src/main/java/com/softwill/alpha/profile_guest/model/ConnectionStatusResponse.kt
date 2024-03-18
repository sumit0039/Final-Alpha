package com.softwill.alpha.profile_guest.model

data class ConnectionStatusResponse(
    val id: Int,
    val receiverUserId: Int,
    val senderUserId: Int,
    val status: Int,
    val statusString: String
)