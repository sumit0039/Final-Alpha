package com.softwill.alpha.chat.adapter

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.softwill.alpha.R
import com.softwill.alpha.chat.FirebaseUtil
import com.softwill.alpha.chat.FirebaseUtil.decodeBase64ToBitmap
import com.softwill.alpha.chat.model.ChatMessageModel
import com.softwill.alpha.chat.splitDateTime
import com.softwill.alpha.databinding.ChatMessageRecyclerRowBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ChatRecyclerAdapter(
    var options: FirestoreRecyclerOptions<ChatMessageModel?>,
    var userid: String,
    val callbackInterface: ChatListCallbackInterface,
    var context: Context
) : FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder>(options)
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatModelViewHolder {
        val binding: ChatMessageRecyclerRowBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.chat_message_recycler_row,parent,false)
        return ChatModelViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(
        holder: ChatModelViewHolder,
        position: Int,
        model: ChatMessageModel
    ) {

        /* with(holder) {

             var fDate: String = ""
             var fTime: String = ""
             var finalDateTime: String = ""

             val currentDate = LocalDate.now()

             val result = splitDateTime(model.timestamp)
 //           val time_AM_PM = SimpleDateFormat("hh:mm:SS a").format(model.timestamp)
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
                     finalDateTime = fTime
                     println("The date is today")
                 }

                 date.isEqual(currentDate.plusDays(1)) -> {
                     finalDateTime = fTime
 //                    finalDateTime = convertDateFormat(fDate).toString()
                     println("The date is tomorrow")
                 }

                 date.isEqual(currentDate.minusDays(1)) -> {
                     finalDateTime = fTime
 //                    finalDateTime = convertDateFormat(fDate).toString()
 //                    finalDateTime = "yesterday"
                     println("The date is yesterday")
                 }

                 else -> {
                     finalDateTime = fTime
 //                    finalDateTime = convertDateFormat(fDate).toString()
                     println("The date is neither today, tomorrow, nor yesterday")
                 }

             }

             if (model.senderId == userid) {
                 binding.leftChatLayout.visibility = View.GONE
                 binding.rightChatLayout.visibility = View.VISIBLE

                 if (model.message.length.toInt() <= 30) {
                     binding.rightChatLayouts.visibility = View.VISIBLE
                     binding.rightChatLayout.visibility = View.GONE
                     binding.rightAttachmentLayouts.visibility = View.GONE
                     binding.rightsChatTextview.text = model.message
                     if (model.isRead != null) {
                         if (model.isRead) {
                             binding.isRightChatRead.text = "delivered"
                         } else {
                             binding.isRightChatRead.text = "read"
                         }
                     }
 //                    if(model.isRead) {
 //                        binding.isRightChatsRead.text = model.isRead
 //                    }
                     binding.rightsChatTimeTextview.text = FirebaseUtil.timeFormatAM_PM(finalDateTime)

                 } else {
                     binding.rightChatLayouts.visibility = View.GONE
                     binding.rightImagePdfTextview.visibility = View.GONE
                     binding.rightAttachmentLayouts.visibility = View.GONE
                     binding.rightChatLayout.visibility = View.VISIBLE
                     binding.rightChatTextview.text = model.message
                     if (model.isRead != null) {
                         if (model.isRead) {
                             binding.isRightChatsRead.text = "delivered"
                         } else {
                             binding.isRightChatsRead.text = "read"
                         }
                     }
                     binding.rightChatTimeTextview.text = FirebaseUtil.timeFormatAM_PM(finalDateTime)
                 }

                 try {
                     if (!model.attachment.equals("") && !model.fileName.equals("") && model.fileName.endsWith("png")) {
                         binding.rightAttachmentLayouts.visibility = View.VISIBLE
                         binding.rightAttachment.visibility= View.VISIBLE

                         binding.rightChatTextview.visibility = View.GONE
                         binding.rightChatLayout.visibility = View.GONE
                         binding.rightChatLayouts.visibility = View.GONE
                         binding.rightImagePdfTextview.visibility= View.GONE

                         val base64String: String = model.attachment
                         val bitmap: Bitmap? = com.softwill.alpha.chat.FirebaseUtil.decodeBase64ToBitmap(base64String)

                         if (bitmap != null) {
                             Glide.with(context).load(bitmap)
                                 .placeholder(R.drawable.icon_no_image).into(binding.rightAttachment)
                         }
                         binding.rightsAttachmentTimeTextview.text = FirebaseUtil.timeFormatAM_PM(finalDateTime)
                         binding.rightsAttachmentTimeTextview.text = FirebaseUtil.timeFormatAM_PM(finalDateTime)

                         if (model.isRead != null) {
                             if (model.isRead) {
                                 binding.isLeftChatsRead.text = "delivered"
                             } else {
                                 binding.isLeftChatsRead.text = "read"
                             }
                         }

                     }else{

                         binding.rightImagePdfTextview.text=model.fileName
                         binding.rightsAttachmentTimeTextview.text = FirebaseUtil.timeFormatAM_PM(finalDateTime)

                         binding.rightAttachmentLayouts.visibility= View.VISIBLE
                         binding.rightImagePdfTextview.visibility= View.VISIBLE

                         binding.rightAttachment.visibility= View.GONE
                         binding.rightChatTextview.visibility = View.GONE
                         binding.rightChatLayout.visibility = View.GONE
                         binding.rightChatLayouts.visibility = View.GONE

                         *//*val text = model.fileName
                        val drawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.document)
                        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                        val imageSpan = ImageSpan(drawable!!, ImageSpan.ALIGN_BASELINE)
                        val spannableString = SpannableString("$text ")
                        spannableString.setSpan(imageSpan, spannableString.length - 1, spannableString.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                        binding.rightImagePdfTextview.text = spannableString
                        binding.rightAttachment.visibility= View.GONE
                        binding.rightImagePdfTextview.visibility= View.VISIBLE*//*

                    }
                } catch (e: Exception) {
                    Log.e(TAG, "onBindViewHolder: $e")
                }

            }
            else {
                binding.rightChatLayout.visibility = View.GONE
                binding.leftChatLayout.visibility = View.VISIBLE

                if (model.message.length.toInt() <= 30) {

                    binding.leftChatLayouts.visibility = View.VISIBLE
                    binding.leftChatLayout.visibility = View.GONE
                    binding.leftChatLayouts.visibility = View.GONE
                    binding.leftImagePdfTextview.visibility = View.GONE
                    binding.leftsChatTextview.text = model.message
                    binding.leftsChatTimeTextview.text = FirebaseUtil.timeFormatAM_PM(finalDateTime)

                    if (model.isRead != null) {
                        if (model.isRead) {
                            binding.isLeftChatsRead.text = "delivered"
                        } else {
                            binding.isLeftChatsRead.text = "read"
                        }
                    }

                } else {
                    binding.leftChatLayouts.visibility = View.VISIBLE
                    binding.leftChatLayout.visibility = View.GONE
                    binding.leftImagePdfTextview.visibility = View.GONE
                    binding.leftChatTextview.text = model.message

                    if (model.isRead != null) {
                        if (model.isRead) {
                            binding.isLeftChatsRead.text = "delivered"
                        } else {
                            binding.isLeftChatsRead.text = "read"
                        }
                    }

                }
                binding.leftChatTimeTextview.text = FirebaseUtil.timeFormatAM_PM(finalDateTime)
            }

            try {
                if (!model.attachment.equals("") && !model.fileName.equals("") && model.fileName.endsWith("jpg")) {
                    binding.leftAttachmentLayouts.visibility = View.VISIBLE
                    binding.leftChatLayout.visibility = View.GONE
                    binding.leftChatLayouts.visibility = View.GONE

                    val base64String: String = model.attachment
                    val bitmap: Bitmap? = decodeBase64ToBitmap(base64String)

                    if (bitmap != null) {
                        Glide.with(context).load(bitmap)
                            .placeholder(R.drawable.icon_no_image).into(binding.leftAttachment)
                    }
                    binding.leftImageTimeTextview.text = FirebaseUtil.timeFormatAM_PM(finalDateTime)
                    binding.leftChatTimeTextview.text = FirebaseUtil.timeFormatAM_PM(finalDateTime)
                    binding.leftAttachment.visibility= View.VISIBLE
                    binding.leftImagePdfTextview.visibility= View.GONE
                }else{

                    binding.leftImagePdfTextview.text=model.fileName
                    binding.leftImageTimeTextview.text = FirebaseUtil.timeFormatAM_PM(finalDateTime)
                    binding.leftChatTimeTextview.text = FirebaseUtil.timeFormatAM_PM(finalDateTime)
                    binding.leftAttachment.visibility= View.GONE
                    binding.leftImagePdfTextview.visibility= View.VISIBLE
                    binding.leftAttachmentLayouts.visibility = View.VISIBLE
                    binding.leftChatLayout.visibility = View.GONE
                    binding.leftChatLayouts.visibility = View.GONE

                    *//* val text = model.fileName
                     val drawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.document)
                     drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                     val imageSpan = ImageSpan(drawable!!, ImageSpan.ALIGN_BASELINE)
                     val spannableString = SpannableString("$text ")
                     spannableString.setSpan(imageSpan, spannableString.length - 1, spannableString.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                     binding.leftImagePdfTextview.text = spannableString
                     binding.leftAttachment.visibility= View.GONE
                     binding.leftImagePdfTextview.visibility= View.VISIBLE*//*

                }
            } catch (e: Exception) {
                Log.e(TAG, "onBindViewHolder: $e")
            }



            binding.rightImagePdfTextview.setOnClickListener {
                FirebaseUtil.openPdfFromBase64(context,model.attachment)
            }
            binding.leftImagePdfTextview.setOnClickListener {
                FirebaseUtil.openPdfFromBase64(context,model.attachment)
            }

        }*/

        with(holder) {

            var fDate:String=""
            var fTime:String=""
            var finalDateTime:String=""

            val currentDate = LocalDate.now()

            val result = splitDateTime(model.timestamp)
//           val time_AM_PM = SimpleDateFormat("hh:mm:SS a").format(model.timestamp)
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
                    finalDateTime = fTime
                    println("The date is today")
                }
                date.isEqual(currentDate.plusDays(1)) -> {
                    finalDateTime = fTime
//                    finalDateTime = convertDateFormat(fDate).toString()
                    println("The date is tomorrow")
                }
                date.isEqual(currentDate.minusDays(1)) -> {
                    finalDateTime =fTime
//                    finalDateTime = convertDateFormat(fDate).toString()
//                    finalDateTime = "yesterday"
                    println("The date is yesterday")
                }
                else -> {
                    finalDateTime =fTime
//                    finalDateTime = convertDateFormat(fDate).toString()
                    println("The date is neither today, tomorrow, nor yesterday")
                }
            }

            if (model.senderId == userid) {

                binding.leftChatLayout.visibility = View.GONE
                binding.leftChatLayouts.visibility = View.GONE
                binding.leftAttachmentLayouts.visibility = View.GONE

                binding.rightChatLayout.visibility = View.VISIBLE
                binding.rightChatLayouts.visibility = View.VISIBLE
                binding.rightAttachmentLayouts.visibility = View.VISIBLE

                if (model.message.length.toInt() <= 30) {
                    binding.rightChatLayouts.visibility = View.VISIBLE
                    binding.rightChatLayout.visibility = View.GONE
                    binding.rightAttachmentLayouts.visibility = View.GONE
                    binding.rightsChatTextview.text = model.message
//                    if(model.isRead) {
//                        binding.isRightChatsRead.text = model.isRead
//                    }
                    binding.rightsChatTimeTextview.text = FirebaseUtil.timeFormatAM_PM(finalDateTime)

                } else {
                    binding.rightChatLayouts.visibility = View.GONE
                    binding.rightAttachmentLayouts.visibility = View.GONE
                    binding.rightChatLayout.visibility = View.VISIBLE
                    binding.rightChatTextview.text = model.message
                    binding.rightChatTimeTextview.text = FirebaseUtil.timeFormatAM_PM(finalDateTime)
                }

                try {
                    if(model.message.equals("")){
                        if (model.fileName.isNotEmpty() && (model.fileName.endsWith("png") || model.fileName.endsWith("jpg")) ) {
                            binding.rightAttachmentLayouts.visibility = View.VISIBLE
                            binding.rightAttachment.visibility = View.VISIBLE
                            binding.rightsAttachmentTimeTextview.visibility = View.VISIBLE
                            binding.rightChatTextview.visibility = View.GONE
                            binding.rightChatLayout.visibility = View.GONE
                            binding.rightChatLayouts.visibility = View.GONE
                            binding.rightImagePdfTextview.visibility= View.GONE

                            val base64String: String = model.attachment
                            val bitmap: Bitmap? = decodeBase64ToBitmap(base64String)

                            if (bitmap != null) {
                                Glide.with(context).load(bitmap)
                                    .placeholder(R.drawable.icon_no_image).into(binding.rightAttachment)
                            }
                            binding.rightsAttachmentTimeTextview.text = FirebaseUtil.timeFormatAM_PM(finalDateTime)

                        }else{
                            binding.rightAttachmentLayouts.visibility = View.VISIBLE
                            binding.rightsAttachmentTimeTextview.visibility = View.VISIBLE
                            binding.rightImagePdfTextview.text=model.fileName
                            binding.rightsAttachmentTimeTextview.text = FirebaseUtil.timeFormatAM_PM(finalDateTime)
                            binding.rightImagePdfTextview.visibility= View.VISIBLE
                            binding.rightAttachment.visibility= View.GONE
                            binding.rightChatLayouts.visibility = View.GONE
                            binding.rightChatLayout.visibility = View.GONE
                        }
                    }
                }catch (e:Exception){
                    Log.e(TAG, "onBindViewHolder: $e")
                }

            }
            else {

                binding.rightChatLayout.visibility = View.GONE
                binding.rightImagePdfTextview.visibility = View.GONE
                binding.rightAttachmentLayouts.visibility = View.GONE
                binding.rightAttachmentLayouts.visibility = View.GONE

                binding.leftChatLayout.visibility = View.VISIBLE
                binding.leftChatLayouts.visibility = View.VISIBLE
                binding.leftAttachmentLayouts.visibility = View.VISIBLE
                binding.leftImageTimeTextview.visibility = View.VISIBLE
                binding.leftImagePdfTextview.visibility = View.VISIBLE


                if(model.message.length.toInt()<=30) {
                    binding.leftChatLayouts.visibility = View.VISIBLE
                    binding.leftChatLayout.visibility = View.GONE
                    binding.leftAttachmentLayouts.visibility = View.GONE
                    binding.leftImagePdfTextview.visibility = View.GONE
                    binding.leftsChatTextview.text = model.message
                    binding.leftsChatTimeTextview.text = FirebaseUtil.timeFormatAM_PM(finalDateTime)

                }else{
                    binding.leftChatLayouts.visibility = View.GONE
                    binding.leftChatLayout.visibility = View.VISIBLE
                    binding.leftAttachmentLayouts.visibility = View.GONE
                    binding.leftImageTimeTextview.visibility = View.GONE
                    binding.leftImagePdfTextview.visibility = View.GONE
                    binding.leftChatTextview.text = model.message
                    binding.leftChatTimeTextview.text = FirebaseUtil.timeFormatAM_PM(finalDateTime)
                }

                try {
                    if(model.message.equals("")){
                        if(model.fileName.isNotEmpty() && (model.fileName.endsWith("png") || model.fileName.endsWith("jpg"))){
                            binding.leftAttachmentLayouts.visibility=View.VISIBLE
                            binding.leftChatLayout.visibility=View.GONE
                            binding.leftChatLayouts.visibility=View.GONE
                            binding.leftImagePdfTextview.visibility=View.GONE
                            binding.leftAttachment.visibility= View.VISIBLE

                            val base64String: String = model.attachment
                            val bitmap: Bitmap? = decodeBase64ToBitmap(base64String)
                            /*9806130204*/
                            /*9399776287*/
                            if (bitmap != null) {
                                Glide.with(context).load(bitmap).placeholder(R.drawable.icon_no_image).into(binding.leftAttachment)
                            }
                            binding.leftImageTimeTextview.text = FirebaseUtil.timeFormatAM_PM(finalDateTime)

                        }else{
                            binding.leftImagePdfTextview.text=model.fileName
                            binding.leftImageTimeTextview.text = FirebaseUtil.timeFormatAM_PM(finalDateTime)
                            binding.leftChatTimeTextview.text = FirebaseUtil.timeFormatAM_PM(finalDateTime)
                            binding.leftAttachment.visibility= View.GONE
                            binding.leftImagePdfTextview.visibility= View.VISIBLE
                            binding.leftAttachmentLayouts.visibility = View.VISIBLE
                            binding.leftChatLayout.visibility = View.GONE
                            binding.leftChatLayouts.visibility = View.GONE
                        }
                    }
                }catch (e:Exception){
                    Log.e(TAG, "onBindViewHolder: $e")
                }

            }

            holder.itemView.setOnLongClickListener {
//            callbackInterface.itemClickCallback(userid.toInt(), position)
                true
            }

            binding.rightImagePdfTextview.setOnClickListener {
                FirebaseUtil.openPdfFromBase64(context,model.attachment,model.fileName)
            }
            binding.leftImagePdfTextview.setOnClickListener {
                FirebaseUtil.openPdfFromBase64(context,model.attachment, model.fileName)
            }

        }



    }

    inner class ChatModelViewHolder(val binding: ChatMessageRecyclerRowBinding) : RecyclerView.ViewHolder(binding.root)

    interface ChatListCallbackInterface {
        fun itemClickCallback(streamId: Int, position: Int)
    }

}
