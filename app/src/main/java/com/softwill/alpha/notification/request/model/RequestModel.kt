package com.softwill.alpha.notification.request.model

import com.softwill.alpha.profile.UserModel
import java.io.Serializable

data class RequestModel(
    val id: Int,
    val senderUserId: Int,
    val receiverUserId: Int,
    val senderName: String,
    val senderUserName: String?,
    val createdAt: String,
    val sender: UserModel
):Serializable
