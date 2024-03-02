package com.softwill.alpha.chat.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.softwill.alpha.R
import com.softwill.alpha.chat.AndroidUtil
import com.softwill.alpha.chat.ChatActivity
import com.softwill.alpha.chat.FirebaseUtil
import com.softwill.alpha.chat.model.ChatUserModel
import com.softwill.alpha.databinding.SearchUserRecyclerRowBinding

class SearchUserRecyclerAdapter(
   val options: FirestoreRecyclerOptions<ChatUserModel?>,
    var userId: String,
    var context: Context
) : FirestoreRecyclerAdapter<ChatUserModel, SearchUserRecyclerAdapter.UserModelViewHolder>(options) {
    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    override fun onBindViewHolder(holder: UserModelViewHolder, position: Int, model: ChatUserModel) {
        with(holder){

               if (model.userId == FirebaseUtil.currentUserId(userId)) {
                   binding.userNameText.text = model.username + " (Me)"
               }else{
                   binding.userNameText.text = model.username
               }

//            binding.phoneText.text = model.createdTimestamp

            FirebaseUtil.getOtherProfilePicStorageRef(model.userId.toString()).downloadUrl
                .addOnCompleteListener { t: Task<Uri?> ->
                    if (t.isSuccessful) {
                        val uri = t.result
                        AndroidUtil.setProfilePic(context, uri, binding.imageView)
                    }
                }
            binding.searchLl.setOnClickListener {
                //navigate to chat activity
                val intent = Intent(context, ChatActivity::class.java)
                AndroidUtil.passUserModelAsIntent(intent, model)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserModelViewHolder {
        val  binding : SearchUserRecyclerRowBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.search_user_recycler_row,parent,false)
        return UserModelViewHolder(binding)
    }

    inner class UserModelViewHolder(val binding: SearchUserRecyclerRowBinding) : RecyclerView.ViewHolder(binding.root)

}
