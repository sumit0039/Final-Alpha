package com.softwill.alpha.chat.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.OnLongClickListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.softwill.alpha.R
import com.softwill.alpha.chat.AndroidUtil
import com.softwill.alpha.chat.ChatActivity
import com.softwill.alpha.chat.FirebaseUtil
import com.softwill.alpha.chat.convertDateFormat
import com.softwill.alpha.chat.model.ChatUserModel
import com.softwill.alpha.chat.model.ChatroomModel
import com.softwill.alpha.chat.splitDateTime
import com.softwill.alpha.databinding.ItemChatBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class RecentChatRecyclerAdapter(
    private var mList: MutableList<ChatroomModel>,
    var options: FirestoreRecyclerOptions<ChatroomModel?>,
    val recentRecyclerView: RecyclerView,
    private val callbackInterface: CallbackInterface,
    var userID: String,
    var context: Context
) : FirestoreRecyclerAdapter<ChatroomModel?, RecentChatRecyclerAdapter.ChatroomModelViewHolder?>(options) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatroomModelViewHolder {
        val binding: ItemChatBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_chat,
            parent,
            false
        )
        return ChatroomModelViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: ChatroomModelViewHolder,
        position: Int,
        model: ChatroomModel
    ) {
        with(holder) {

            var fDate:String=""
            var fTime:String=""
            var finalDateTime:String=""

            val currentDate = LocalDate.now()


//            val result = splitDateTime(model.timestamp)
            val result = splitDateTime(model.timestamp)

            // Print the result
            result?.let { (date, time) ->
                fDate = date
                fTime = time
                println("Date: $fDate")
                println("Time: $fTime")
            } ?: println("Failed to parse date-time string.")

            val date = LocalDate.parse(fDate, DateTimeFormatter.ISO_DATE)

            // Compare the date with the current date
            when {
                date.isEqual(currentDate) -> {

                    binding.tvTime.text = FirebaseUtil.timeFormatAM_PM(fTime)
                    println("The date is today")
                }
                date.isEqual(currentDate.plusDays(1)) -> {
                    binding.tvTime.text = convertDateFormat(fDate).toString()
                    println("The date is tomorrow")
                }
                date.isEqual(currentDate.minusDays(1)) -> {
                    binding.tvTime.text = convertDateFormat(fDate).toString()
//                    finalDateTime = "yesterday"
                    println("The date is yesterday")
                }
                else -> {
                    binding.tvTime.text = convertDateFormat(fDate).toString()
                    println("The date is neither today, tomorrow, nor yesterday")
                }
            }

           /* val data_time = return when(FirebaseUtil.checkDateOrTime(finalDateTime)) {
                "Input is a Date"->finalDateTime = convertDateFormat(fDate).toString()
                "Input is a Time"->finalDateTime = convertDateFormat(fDate).toString()
                else -> {}
            }
            */
            if(model.receiver["userId"]==userID){
                binding.tvName.text = model.sender["name"].toString()
                Glide.with(context).load(model.sender["avtarUrl"].toString())
                    .placeholder(R.drawable.baseline_account_circle_24).into(binding.ivProfileImage)
            }else{
                binding.tvName.text = model.receiver["name"].toString()
                Glide.with(context).load(model.receiver["avtarUrl"].toString())
                    .placeholder(R.drawable.baseline_account_circle_24).into(binding.ivProfileImage)
            }

            if(model.isRead==false){
              binding.cardCount.visibility= VISIBLE
            }else{
                binding.cardCount.visibility= GONE
            }

//            binding.tvTime.text = finalDateTime

            if(model.receiver["userId"].toString() == userID) {

                if(model.fileName.isNotEmpty()){
                    binding.tvLastMsg.text = model.fileName
                }else {
                    binding.tvLastMsg.text = "You : ${model.latestMessage}"
                }
            }else{
                if(model.fileName.isNotEmpty()){
                    binding.tvLastMsg.text = model.fileName
                }else {
                    binding.tvLastMsg.text = model.latestMessage
                }
            }

            val chatUserModel = ChatUserModel()
            if (model.receiver["userId"].toString() == userID){
                chatUserModel.isDeleted = FirebaseUtil.timestampToString(Timestamp.now())
                chatUserModel.isRead = false
                chatUserModel.username =  model.sender["name"].toString()
                chatUserModel.createdTimestamp = FirebaseUtil.timestampToString(Timestamp.now())
                chatUserModel.userId = model.sender["userId"].toString()
                chatUserModel.avtarUrl = model.sender["avtarUrl"].toString()
            }else{
                chatUserModel.isDeleted = FirebaseUtil.timestampToString(Timestamp.now())
                chatUserModel.isRead = false
                chatUserModel.username =  model.receiver["name"].toString()
                chatUserModel.createdTimestamp = FirebaseUtil.timestampToString(Timestamp.now())
                chatUserModel.userId = model.receiver["userId"].toString()
                chatUserModel.avtarUrl = model.receiver["avtarUrl"].toString()
            }


             binding.view.setOnClickListener {
                                //navigate to chat activity
                                val intent = Intent(context, ChatActivity::class.java)
                                AndroidUtil.passUserModelAsIntent(intent, chatUserModel)
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            }

            binding.view.setOnLongClickListener(OnLongClickListener {
               val chatroomId = chatUserModel.userId?.let {
                    FirebaseUtil.getChatroomId(userID,
                        it
                    )
                }
                callbackInterface.deleteAllChat(chatroomId.toString(), chatUserModel.username.toString())

                true
            })


        }

    }

    inner class ChatroomModelViewHolder(val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root)

    interface CallbackInterface {
        fun deleteAllChat(chatroomId: String, userName: String)
    }

}
