package com.softwill.alpha.chat

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.github.drjacky.imagepicker.ImagePicker
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.softwill.alpha.R
import com.softwill.alpha.chat.adapter.ChatRecyclerAdapter
import com.softwill.alpha.chat.model.ChatMessageModel
import com.softwill.alpha.chat.model.ChatUserModel
import com.softwill.alpha.chat.model.ChatroomModel
import com.softwill.alpha.databinding.ActivityChatBinding
import com.softwill.alpha.profile_guest.activity.ProfileGuestActivity
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.YourPreference
import com.vmb.fileSelect.FileSelector
import com.vmb.fileSelect.FileSelectorCallBack
import com.vmb.fileSelect.FileSelectorData
import com.vmb.fileSelect.FileType
import java.time.format.DateTimeFormatterBuilder
import java.util.Random


class ChatActivity : AppCompatActivity(), ChatRecyclerAdapter.ChatListCallbackInterface {

    private lateinit var binding: ActivityChatBinding
    var yourPreference: YourPreference? = null
    var chatroomModel: ChatroomModel? = null
    var chatUserModel: ChatUserModel? = null
    var adapter: ChatRecyclerAdapter? = null
    var mName: String = ""
    var chatroomId: String? = null
    val random: Int = Random().nextInt()
    @RequiresApi(Build.VERSION_CODES.O)
    val timestamp = DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").toFormatter()
    var attachment:String=""
    var launcher: ActivityResultLauncher<Intent>? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)

        supportActionBar!!.hide();

        yourPreference = YourPreference(this)

        chatUserModel = AndroidUtil.getUserModelFromIntent(intent)

        chatroomId = chatUserModel!!.userId?.let {
            FirebaseUtil.getChatroomId(yourPreference!!.getData(Constant.userId),
                it
            )
        }

        binding.backBtn.setOnClickListener { v: View? -> onBackPressed() }

        binding.otherUsername.text = chatUserModel!!.username

        Glide.with(this).load(chatUserModel!!.avtarUrl)
            .placeholder(R.drawable.baseline_account_circle_24)
            .error(R.drawable.baseline_account_circle_24)
            .circleCrop()
            .into(binding.profilePicLayout)

        binding.messageSendBtn.setOnClickListener(View.OnClickListener { v: View? ->
            val message = binding.chatMessageInput.text.toString().trim { it <= ' ' }
            if (message.isEmpty()) return@OnClickListener
            sendMessageToUser(message,attachment,"")
        })

//        setupBack()

        orCreateChatroomModel
        setupChatRecyclerView()

        binding.attachment.setOnClickListener {

            getImageFromGalleryAndCamera()

        }

        binding.profilePicLayout.setOnClickListener {
            goToProfile()
        }

        binding.otherUsername.setOnClickListener {
            goToProfile()
        }

    }

    private fun goToProfile() {
        val intent = Intent(this, ProfileGuestActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
        intent.putExtra("mUserId", chatUserModel!!.userId!!.toInt())
        startActivity(intent)
    }

    //open camera and gallery
    private fun getImageFromGalleryAndCamera() {
        FileSelector.requiredFileTypes(FileType.IMAGES,FileType.PDF,FileType.MS_WORD).open(this, object :FileSelectorCallBack{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(fileSelectorData: FileSelectorData) {
                attachment = fileSelectorData.uri.toString()
                println("IMAGEPATH : ${fileSelectorData.fileName
                        +fileSelectorData.responseInBase64}")

                if(fileSelectorData.responseInBase64!!.isNotEmpty()){
                    sendMessageToUser("",fileSelectorData.responseInBase64.toString(), fileSelectorData.fileName)
                }

            }

        })
//        com.github.dhaval2404.imagepicker.ImagePicker.with(this)
//            .crop() //Crop image(Optional), Check Customization for more option
//            .compress(1024) //Final image size will be less than 1 MB(Optional)
//            .maxResultSize(
//                1080,
//                1080
//            ) //Final image resolution will be less than 1080 x 1080(Optional)
//            .start()

    }


    //set image in imageview
    /*@RequiresApi(Build.VERSION_CODES.O)
    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            // Get the url from data
            val uri = data!!.data
            attachment = uri?.path?.let { File(it).toString() }.toString()
            println("IMAGEPATH : $uri")
//            Glide.with(this).load(attachment).placeholder(R.drawable.baseline_account_circle_24).into(binding.)

            if(attachment.isNotEmpty()){
                sendMessageToUser("",uri.toString())
            }


        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        }

    }
*/
    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = mName
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        actionBar?.setDisplayUseLogoEnabled(true);
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_2_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupChatRecyclerView() {
        val query = FirebaseUtil.getChatroomMessageReference(chatroomId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<ChatMessageModel>()
            .setQuery(query, ChatMessageModel::class.java).build()
        adapter = ChatRecyclerAdapter(options,yourPreference!!.getData(Constant.userId),this,this)
        val manager = LinearLayoutManager(this)
        manager.reverseLayout = true
        binding.chatRecyclerView.layoutManager = manager
        binding.chatRecyclerView.adapter = adapter
        adapter!!.startListening()
        adapter!!.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.chatRecyclerView.smoothScrollToPosition(0)
            }
        })

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendMessageToUser(message: String?, attachment: String?, fileName: String?) {

        val receiverMap: HashMap<String, String> = hashMapOf()
        receiverMap["avtarUrl"] = chatUserModel!!.avtarUrl.toString()
        receiverMap["name"] = chatUserModel!!.username.toString()
        receiverMap["userId"] = chatUserModel!!.userId.toString()

        val senderMap: HashMap<String, String> = hashMapOf()
        senderMap["avtarUrl"] = yourPreference!!.getData(Constant.avtarUrl)
        senderMap["name"] = yourPreference!!.getData(Constant.firstName)+" "+yourPreference!!.getData(Constant.lastName)
        senderMap["userId"] = yourPreference!!.getData(Constant.userId)

        chatroomModel!!.latestMessage = message
        chatroomModel!!.fileName = fileName
        chatroomModel!!.participants = listOf(yourPreference!!.getData(Constant.userId), chatUserModel!!.userId)
        chatroomModel!!.receiver = receiverMap
        chatroomModel!!.sender = senderMap
        chatroomModel!!.timestamp = FirebaseUtil.timestampToString(Timestamp.now())
        chatroomModel!!.attachment = attachment
        chatroomModel!!.setIsRead(true)

        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel!!)
        val chatMessageModel = ChatMessageModel(chatUserModel!!.avtarUrl,fileName, FirebaseUtil.timestampToString(Timestamp.now()),chatroomModel!!.isRead,message, yourPreference!!.getData(Constant.userName),yourPreference!!.getData(Constant.userId), FirebaseUtil.timestampToString(Timestamp.now()), chatroomModel!!.attachment)
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.chatMessageInput.setText("")
//                    sendNotification(message)
                }
            }

    }

    private val orCreateChatroomModel: Unit
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            FirebaseUtil.getChatroomReference(chatroomId).get()
                .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                    if (task.isSuccessful) {
                        chatroomModel = task.result.toObject(ChatroomModel::class.java)
                        if (chatroomModel == null) {

                            val receiverMap: HashMap<String, String> = hashMapOf()
                            receiverMap["avtarUrl"] = chatUserModel!!.avtarUrl.toString()
                            receiverMap["name"] = chatUserModel!!.username.toString()
                            receiverMap["userId"] = chatUserModel!!.userId.toString()

                            val senderMap: HashMap<String, String> = hashMapOf()
                            senderMap["avtarUrl"] = yourPreference!!.getData(Constant.avtarUrl)
                            senderMap["name"] = yourPreference!!.getData(Constant.firstName)+" "+yourPreference!!.getData(Constant.lastName)
                            senderMap["userId"] = yourPreference!!.getData(Constant.userId)

                            //first time chat
                            chatroomModel = ChatroomModel(
                                "","",
                                listOf(yourPreference!!.getData(Constant.userId), chatUserModel!!.userId),
                                receiverMap,
                                senderMap,
                                FirebaseUtil.timestampToString(Timestamp.now()),
                                ""
                            )

                            FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel!!)
//                            FirebaseUtil.getChatroomReference(chatroomId)
//                            FirebaseUtil.getChatroomReference(chatroomId).delete()

                        }
                    }
                }
        }


    fun chatRemoveById(messageDocumentRef: DocumentReference){
        messageDocumentRef
            .delete()
            .addOnSuccessListener {
                // Handle success, e.g., update UI
                Toast.makeText(this,"Message successfully deleted!", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                // Handle failure
                Toast.makeText(this,"Error deleting message", Toast.LENGTH_LONG).show()

                Log.e("Firestore", "Error deleting message", e)

            }
    }

    override fun itemClickCallback(streamId: Int, position: Int) {
        val db = FirebaseFirestore.getInstance()
        val firestorePath = "/chatRooms/$chatroomId/chats"

// Split the path using "/"
        val pathSegments = firestorePath.split("/")

// Assuming the document ID is the second-to-last segment
        if (pathSegments.size >= 2) {
            val documentId = pathSegments[pathSegments.size - 2]
            // Now 'documentId' should contain "110_117"
            println("Document ID: $documentId")
//            Toast.makeText(this, documentId.toString(), Toast.LENGTH_LONG).show()

            // Replace "your_collection" with the name of your Firestore collection
            val collectionReference = db.collection("chatRooms")

// Delete the document
            collectionReference.document(documentId)
                .delete()
                .addOnSuccessListener {
                    // Document successfully deleted
                    println("Document $documentId deleted successfully.")
                }
                .addOnFailureListener { e ->
                    // Handle errors
                    println("Error deleting document: $e")
                }

        } else {
            // Handle the case where the path doesn't have enough segments
            println("Invalid Firestore path")
            Toast.makeText(this, "Invalid Firestore path", Toast.LENGTH_LONG).show()
        }


        /* val db = FirebaseFirestore.getInstance()

         // Replace "your_collection" with the name of your Firestore collection
         val collectionReference = db.collection("chatRooms")

 // Replace "your_document" with the name of the document you want to retrieve
         val documentReference = collectionReference.document("chats")

         documentReference.get()
             .addOnSuccessListener { documentSnapshot ->
                 if (documentSnapshot.exists()) {
                     // Document exists, retrieve data
                     val path = documentSnapshot.getString("message")
                     // Use the retrieved path as needed
                     Toast.makeText(this,path.toString(),Toast.LENGTH_LONG).show()
                 } else {
                     // Document does not exist
                     Toast.makeText(this,"Document does not exist",Toast.LENGTH_LONG).show()
                 }
             }
             .addOnFailureListener { exception ->
                 // Handle failures
                 Toast.makeText(this,"Document does not exist $exception",Toast.LENGTH_LONG).show()

             }*/

    }


}