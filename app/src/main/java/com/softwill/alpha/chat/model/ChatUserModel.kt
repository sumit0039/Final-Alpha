package com.softwill.alpha.chat.model

import java.io.Serializable
import java.time.format.DateTimeFormatter

class ChatUserModel : Serializable {
    @JvmField
    var isDeleted: String? = null
    var isRead: Boolean? = null
    @JvmField
    var username: String? = null
    var createdTimestamp: String? = null
    @JvmField
    var userId: String? = null
    @JvmField
//    var fcmToken: String? = null
    var avtarUrl: String? = null
    var attachment: String? = null

    constructor()
    constructor(
        isDeleted: String,
        isRead: Boolean?,
        username: String?,
        createdTimestamp: String?,
        userId: String?,
        avtarUrl: String?,
        attachment: String?,
    ) {
        this.isDeleted = isDeleted
        this.isRead = isRead
        this.username = username
        this.createdTimestamp = createdTimestamp
        this.userId = userId
        this.avtarUrl = avtarUrl
        this.attachment = avtarUrl
    }
}
