package com.softwill.alpha.profile.tabActivity

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class PostModel(
    val id: Int,
    val title: String,
    val desc: String,
    val createdAt: String,
    val comments : Int,
    val likes : Int,
    var isLiked : Int,
    val photos: List<PhotoModel>
): Serializable
