package com.softwill.alpha.notification.request.fragment

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.gson.Gson
import com.softwill.alpha.R
import com.softwill.alpha.chat.FirebaseUtil
import com.softwill.alpha.chat.model.ChatUserModel
import com.softwill.alpha.databinding.ActivityAcceptRejectProfileByNotificationBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.profile_guest.model.GuestUserDetailsResponse
import com.softwill.alpha.utils.UtilsFunctions
import com.softwill.alpha.utils.YourPreference
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AcceptRejectProfileByNotificationActivity : AppCompatActivity(), View.OnClickListener {

    var chatUserModel: ChatUserModel? = null
    var yourPreference: YourPreference? = null
    private var mUserId: Int = -1
    private var mName: String = ""
    private var mProfileImage: String = ""

    private lateinit var binding:ActivityAcceptRejectProfileByNotificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentView(this, R.layout.activity_accept_reject_profile_by_notification)
        yourPreference = YourPreference(this)
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUserId = bundle.getInt("mUserId")
        }

        setupBack()

        apiGuestUserDetails();

        binding.cardConnect.setOnClickListener(this)
        binding.cardReject.setOnClickListener(this)


    }

    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        supportActionBar!!.title = (Html.fromHtml("<font color=\"#060B13\">" + "Profile" + "</font>"));
    }

    private fun apiGuestUserDetails() {

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this).myApi.api_GuestUserDetails(mUserId)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject =
                        Gson().fromJson(responseJson, GuestUserDetailsResponse::class.java)


                    mName = responseObject.firstName + " " + responseObject.lastName
                    binding.tvName.text = mName
                    mProfileImage = responseObject.avtarUrl.toString()
                    Glide.with(this@AcceptRejectProfileByNotificationActivity).load(mProfileImage)
                        .placeholder(R.drawable.baseline_account_circle_24).into(binding.ivProfileImage)
                    if (responseObject.userName != null) {
                        binding.tvUserName.text = responseObject.userName
                    }
                    binding.tvBio.text = responseObject.bio

                    chatUserModel = ChatUserModel("",false, mName,
                        FirebaseUtil.timestampToString(Timestamp.now()),responseObject.id.toString(),mProfileImage,"")
//                    Toast.makeText(this@ProfileGuestActivity,"Mobile Number :"+responseObject.mobile+"Name : "+mName+" "+"Time :"+FirebaseUtil.timestampToString(Timestamp.now())+"UserID : "+responseObject.id.toString(),Toast.LENGTH_LONG).show()

                    binding.tvConnectionCount.text = responseObject.connections.toString()


                } else {
                    try {
                        UtilsFunctions().handleErrorResponse(response, this@AcceptRejectProfileByNotificationActivity)
                    }catch (e:Exception){
                        Log.e(ContentValues.TAG, "onResponse: ${e.toString()}", )
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun onClick(p0: View?) {

    }

}