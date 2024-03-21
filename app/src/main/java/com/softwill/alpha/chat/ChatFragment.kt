package com.softwill.alpha.chat

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.softwill.alpha.R
import com.softwill.alpha.chat.adapter.RecentChatRecyclerAdapter
import com.softwill.alpha.chat.model.ChatroomModel
import com.softwill.alpha.databinding.FragmentChatBinding
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.YourPreference


class ChatFragment : Fragment(), RecentChatRecyclerAdapter.CallbackInterface {

    private lateinit var binding: FragmentChatBinding
    var adapter: RecentChatRecyclerAdapter? = null
    var yourPreference: YourPreference? = null
    val dataList = mutableListOf<ChatroomModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        yourPreference = YourPreference(context)
        setupRecyclerView(yourPreference!!)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClickListener()

    }

    private fun onClickListener() {

        binding.searchImg.setOnClickListener {
            val intent = Intent(activity, SearchUserActivity::class.java)
            startActivity(intent)
        }

        /*binding.ivMenu.setOnClickListener {
            val popupMenu = PopupMenu(activity, binding.ivMenu)

            popupMenu.menuInflater.inflate(R.menu.chat_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                val intent = Intent(activity, ManageNotificationsActivity::class.java)
                startActivity(intent)
                true
            }
            popupMenu.show()
        }*/

    }

    private fun setupRecyclerView(yourPreference: YourPreference) {


       /* val firestore = FirebaseFirestore.getInstance()
        val collectionReference = firestore.collection("chatRooms")

        collectionReference.get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    val data = document.toObject(ChatroomModel::class.java)
                    dataList.add(data)
                }


                // Now, dataList contains your retrieved data
                // You can do further processing or store it in another model class
            }
            .addOnFailureListener { exception ->
                // Handle the failure
            }*/


        val query = FirebaseUtil.allChatroomCollectionReference()
            .whereArrayContains("participants", yourPreference.getData(Constant.userId))
            .orderBy("timestamp", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<ChatroomModel>()
            .setQuery(query, ChatroomModel::class.java).build()

        // Get Firestore instance
        val firestore = FirebaseFirestore.getInstance()

// Execute the query
        query.get().addOnSuccessListener { querySnapshot ->
            // Get the count of documents in the query snapshot
            val count = querySnapshot.size()
            Log.d(TAG, "setupRecyclerView: $count")
            if(count>0){
                binding.noResultFound.visibility=View.GONE
                binding.rvChat.visibility=View.VISIBLE
            }else{
                binding.noResultFound.visibility=View.VISIBLE
                binding.rvChat.visibility=View.GONE
            }
            // Now 'count' contains the number of documents in the query
            // You can use 'count' as needed here
        }.addOnFailureListener { exception ->
            // Handle any errors that may occur
            Log.e(TAG, "Error getting documents: ", exception)
        }

//        Log.d(TAG, "setupRecyclerView: "+ChatroomModel().latestMessage.toString())
//        Toast.makeText(requireContext(),ChatroomModel().latestMessage.toString()?:"0",Toast.LENGTH_LONG).show()

        adapter = RecentChatRecyclerAdapter(dataList,
            options,
            binding.rvChat,this,
            yourPreference.getData(Constant.userId),
            requireContext()
        )
        adapter!!.startListening()

        binding.rvChat.layoutManager = LinearLayoutManager(context)
        binding.rvChat.adapter = adapter


       /* if(options!=null) {

            binding.noResultFound.visibility=View.GONE
            binding.rvChat.visibility=View.VISIBLE

            adapter = RecentChatRecyclerAdapter(dataList,
                options,
                yourPreference.getData(Constant.userId),
                requireContext()
            )
            adapter!!.startListening()


// ...

            val db = FirebaseFirestore.getInstance()
            val chatListCollection = db.collection("chats")

            chatListCollection.get()
                .addOnSuccessListener { querySnapshot ->
                    // Get the count of documents in the collection
                    val count = querySnapshot.size()

                    // Now 'count' contains the number of documents in the "chatlist" collection
                    // You can use this count as needed
                    // ...
//                    Toast.makeText(requireContext(),count.toString(),Toast.LENGTH_LONG).show()
                    // Example: Log the count
                    Log.d("Firestore", "Number of chatlist documents: $count")
                }
                .addOnFailureListener { exception ->
                    // Handle failures
                    Log.e("Firestore", "Error getting chatlist documents: ", exception)
                }


            binding.rvChat.layoutManager = LinearLayoutManager(context)
            binding.rvChat.adapter = adapter
            adapter!!.startListening()

        }else{
            binding.noResultFound.visibility=View.VISIBLE
            binding.rvChat.visibility=View.GONE
        }*/

    }

    override fun onStart() {
        super.onStart()
        if (adapter != null) adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (adapter != null) adapter!!.stopListening()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        if (adapter != null) adapter!!.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun deleteAllChat(chatroomId: String, username: String) {
        FirebaseUtil.getChatroomReference(chatroomId).delete()
            .addOnSuccessListener {
                adapter!!.notifyDataSetChanged()
                adapter!!.startListening()

                Toast.makeText(context,"$username message successfully deleted", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(context,"$username message failed delete", Toast.LENGTH_LONG).show()

            }
    }

}