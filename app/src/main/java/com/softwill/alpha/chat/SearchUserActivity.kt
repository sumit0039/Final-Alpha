package com.softwill.alpha.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import com.softwill.alpha.R
import com.softwill.alpha.chat.adapter.ConnectionChatAdapter
import com.softwill.alpha.chat.adapter.RecentChatRecyclerAdapter
import com.softwill.alpha.chat.adapter.SearchUserRecyclerAdapter
import com.softwill.alpha.chat.model.ChatroomModel
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.profile_guest.adapter.ConnectionListModel
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import com.softwill.alpha.utils.YourPreference
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale


class SearchUserActivity : AppCompatActivity(),RecentChatRecyclerAdapter.CallbackInterface {

    private lateinit var binding: com.softwill.alpha.databinding.ActivitySearchUserBinding
    val mConnectionListModel = ArrayList<ConnectionListModel>()
    var mConnectionAdapter: ConnectionChatAdapter? = null
    private var mUserId: Int = -1
    private var count: Int = -1
    var yourPreference: YourPreference? = null
    var recyclerView: RecyclerView? = null
    var adapter: SearchUserRecyclerAdapter? = null
    var adapterRecentChat: RecentChatRecyclerAdapter? = null
    val dataList = mutableListOf<ChatroomModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_user)
        supportActionBar!!.hide();

        yourPreference = YourPreference(this)
//        mUserId = yourPreference!!.getData(Constant.userId).toInt()


        setupConnections()

        getRecentChatListing()

        binding.searchView.requestFocus()

        binding.backBtn.setOnClickListener { onBackPressed() }

        setupSearchRecyclerView()

        apiConnectionsList()

    }

    private fun setupSearchRecyclerView() {

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {

                if(newText.length >= 3){
                    filterData(newText)
                    binding.recentChatRecyclerView.visibility=View.GONE
                    binding.title.visibility=View.GONE
                    binding.titles.visibility=View.GONE
                }
                else{
                    binding.recentChatRecyclerView.visibility=View.VISIBLE
                    binding.title.visibility=View.VISIBLE
                    binding.titles.visibility=View.VISIBLE
                    setupConnections()
                }
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

        })

    }

    private fun filterData(newText: String?) {


//        val query = FirebaseUtil.allUserCollectionReference()
//            .whereGreaterThanOrEqualTo("username", newText!!)
//            .whereLessThanOrEqualTo("username", newText + '\uf8ff')

//        val query = FirebaseUtil.allChatroomCollectionReference()
//            .whereArrayContains("participants", yourPreference!!.getData(Constant.userId))
//            .whereLessThanOrEqualTo("username", newText + '\uf8ff')
//            .orderBy("timestamp", Query.Direction.DESCENDING)

        val filteredRecentList: java.util.ArrayList<ChatroomModel> = java.util.ArrayList()
        val filteredList: java.util.ArrayList<ConnectionListModel> = java.util.ArrayList()

      /*  for (items in dataList){
            if (items.receiver["name"]!!.lowercase(Locale.ROOT).contains(newText!!.lowercase(Locale.getDefault()))) {
                filteredRecentList.add(items)
                adapterRecentChat!!.filterRecentList(filteredRecentList)

            }else{
                for (item in mConnectionListModel) {
                    if (item.name.lowercase(Locale.ROOT).contains(newText!!.lowercase(Locale.getDefault()))) {
                        filteredList.add(item)
                    }else{
                        binding.chatLl.visibility=View.GONE
                        binding.noResultFound.visibility=View.VISIBLE
                    }
                    mConnectionAdapter?.filterList(filteredList)
                }
               *//* binding.chatLl.visibility=View.GONE
                binding.noResultFound.visibility=View.VISIBLE*//*
            }

        }*/

        for (item in mConnectionListModel) {
            if (item.name.lowercase(Locale.ROOT).contains(newText!!.lowercase(Locale.getDefault()))) {
                filteredList.add(item)
            }/*else{
                binding.chatLl.visibility=View.GONE
                binding.noResultFound.visibility=View.VISIBLE
            }*/
            mConnectionAdapter?.filterList(filteredList)
        }
        binding.titles.visibility=View.VISIBLE
        binding.connectionRecyclerView.visibility=View.VISIBLE
        binding.title.visibility=View.GONE
        binding.recentChatRecyclerView.visibility=View.GONE

        /* query.get().addOnCompleteListener { task ->
             val b = task.result.isEmpty
             if(b){
                *//* val options = FirestoreRecyclerOptions.Builder<com.softwill.alpha.chat.model.ChatUserModel>()
                    .setQuery(query, com.softwill.alpha.chat.model.ChatUserModel::class.java).build()
                adapter = SearchUserRecyclerAdapter(options,yourPreference!!.getData(Constant.userId), applicationContext)
                binding.searchUserRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.searchUserRecyclerView.adapter = adapter
                adapter!!.startListening()*//*

                val options = FirestoreRecyclerOptions.Builder<ChatroomModel>()
                    .setQuery(query, ChatroomModel::class.java).build()

                adapterRecentChat =
                    RecentChatRecyclerAdapter(options, yourPreference!!.getData(Constant.userId), this)
                binding.recentChatRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.recentChatRecyclerView.adapter = adapterRecentChat
                *//*binding.recentChatRecyclerView.addItemDecoration(
                    DividerItemDecoration(
                        binding.recentChatRecyclerView.context,
                        DividerItemDecoration.VERTICAL
                    )
                )*//*
                adapterRecentChat!!.startListening()

                binding.title.visibility=View.VISIBLE
                binding.recentChatRecyclerView.visibility=View.VISIBLE
                binding.titles.visibility=View.GONE
                binding.connectionRecyclerView.visibility=View.VISIBLE
            }else{
                val filteredList: java.util.ArrayList<ConnectionListModel> = java.util.ArrayList()
                for (item in mConnectionListModel) {
                    if (item.name.lowercase(Locale.ROOT).contains(newText.lowercase(Locale.getDefault()))) {
                        filteredList.add(item)
                    }
                    mConnectionAdapter?.filterList(filteredList)
                }
                binding.titles.visibility=View.VISIBLE
                binding.connectionRecyclerView.visibility=View.VISIBLE
                binding.title.visibility=View.GONE
                binding.recentChatRecyclerView.visibility=View.GONE
            }
        }
        */

    }

    private fun getRecentChatListing(){

//        val querys = FirebaseUtil.allUserCollectionReference()
//            .whereGreaterThanOrEqualTo("username", newText!!)
//            .whereLessThanOrEqualTo("username", newText + '\uf8ff')

      /*  val firestore = FirebaseFirestore.getInstance()
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
            .whereArrayContains("participants", yourPreference!!.getData(Constant.userId))
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<ChatroomModel>()
            .setQuery(query, ChatroomModel::class.java).build()

        adapterRecentChat = RecentChatRecyclerAdapter(
            dataList,
            options,
            binding.recentChatRecyclerView,
            this,
            yourPreference!!.getData(Constant.userId),
            this
        )

        binding.recentChatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.recentChatRecyclerView.adapter = adapterRecentChat
        adapterRecentChat!!.startListening()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupConnections() {
        if(mConnectionListModel.isNotEmpty()) {
            mConnectionAdapter = ConnectionChatAdapter(this, mConnectionListModel, mUserId)
            binding.connectionRecyclerView.layoutManager = LinearLayoutManager(this)
            binding.connectionRecyclerView.adapter = mConnectionAdapter
            mConnectionAdapter!!.notifyDataSetChanged()
            binding.noResultFound.visibility=View.GONE
            binding.chatLl.visibility=View.VISIBLE
            binding.title.visibility=View.GONE
            binding.recentChatRecyclerView.visibility=View.GONE
//            count=0
//            binding.searchView.requestFocus()
        }else{
            binding.noResultFound.visibility = View.VISIBLE
            binding.chatLl.visibility = View.GONE
        }
    }

    private fun apiConnectionsList() {
        val retrofit = RetrofitClient.getInstance(this).myApi
        val call: Call<ResponseBody> = retrofit.api_ConnectionsList()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val connection = Gson().fromJson(responseJson, Array<ConnectionListModel>::class.java).toList()
                    mConnectionListModel.clear()
                    mConnectionListModel.addAll(connection)

                    if (mConnectionListModel.isNotEmpty()) {
                        mConnectionAdapter?.notifyDataSetChanged()
                    }

                } else {
                    UtilsFunctions().handleErrorResponse(response, this@SearchUserActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }



    override fun onStart() {
        super.onStart()
        if (/*adapter != null || */adapterRecentChat!=null) {
//            adapter!!.startListening()
            adapterRecentChat!!.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (/*adapter != null ||*/ adapterRecentChat!=null) {
//            adapter!!.stopListening()
            adapterRecentChat!!.stopListening()
        }    }

    override fun onResume() {
        super.onResume()
        if (/*adapter != null ||*/ adapterRecentChat!=null) {
//            adapter!!.startListening()
            adapterRecentChat!!.startListening()
        }
    }

    override fun deleteAllChat(chatroomId: String, userName: String) {

    }
}
