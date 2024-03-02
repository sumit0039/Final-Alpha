package com.softwill.alpha.chat.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.softwill.alpha.R
import com.softwill.alpha.chat.AndroidUtil
import com.softwill.alpha.chat.ChatActivity
import com.softwill.alpha.chat.FirebaseUtil
import com.softwill.alpha.chat.model.ChatUserModel
import com.softwill.alpha.databinding.ItemConnectionsBinding
import com.softwill.alpha.profile_guest.activity.ConnectionsActivity
import com.softwill.alpha.profile_guest.activity.ProfileGuestActivity
import com.softwill.alpha.profile_guest.adapter.ConnectionListModel
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.YourPreference


class ConnectionChatAdapter(
    private val context: Context,
    private var mList: ArrayList<ConnectionListModel>,
    private val mUserId: Int,


    ) : RecyclerView.Adapter<ConnectionChatAdapter.ViewHolder>() {

    var yourPreference: YourPreference? = null



    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemConnectionsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.item_connections, parent, false
        )
        return ViewHolder(binding)
    }


    // binds the list items to a view

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            yourPreference = YourPreference(context)

            with(mList[position]) {


                binding.tvName.text = name
                binding.tvUsername.text = userName


                Glide.with(context).load(avtarUrl)
                    .placeholder(R.drawable.baseline_account_circle_24).into(binding.ivProfileImage)

//                if (mUserId != -1 && yourPreference!!.getData(Constant.userId).toInt() == userId){
                    binding.btnRemove.visibility = View.INVISIBLE
//                }


//                binding.btnRemove.setOnClickListener {
//                    connectionsActivity.apiRemoveConnection(userId, position)
//                }

                val chatUserModel = ChatUserModel(
                    FirebaseUtil.timestampToString(Timestamp.now())
                    ,false, name,  FirebaseUtil.timestampToString(
                        Timestamp.now()),userId.toString(),avtarUrl,"")

                holder.itemView.setOnClickListener {
                    val intent = Intent(context, ChatActivity::class.java)
                    AndroidUtil.passUserModelAsIntent(intent, chatUserModel)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                    intent.putExtra("mUserId", userId)
                    context.startActivity(intent)
                }
            }

        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(val binding: ItemConnectionsBinding) :
        RecyclerView.ViewHolder(binding.root)

/*
    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

    fun filterList(filteredList: ArrayList<ConnectionListModel>) {
        mList = filteredList
        notifyDataSetChanged()
    }


}


