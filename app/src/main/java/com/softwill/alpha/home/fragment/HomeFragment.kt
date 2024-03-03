package com.softwill.alpha.home.fragment

import android.R.attr.value
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.softwill.alpha.R
import com.softwill.alpha.databinding.FragmentHomeBinding
import com.softwill.alpha.home.activity.PostVIewDetailActivity
import com.softwill.alpha.home.activity.SearchHomeActivity
import com.softwill.alpha.home.adapter.*
import com.softwill.alpha.home.adapter.HomePostAdapter.CallbackInterface
import com.softwill.alpha.home.model.CommentModel
import com.softwill.alpha.home.model.HomePostModel
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.notification.NotificationActivity
import com.softwill.alpha.profile.tabActivity.PhotoModel
import com.softwill.alpha.profile_guest.adapter.ConnectionListModel
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import com.softwill.alpha.utils.YourPreference
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator
import java.util.*


class HomeFragment : Fragment(), CallbackInterface, CommentAdapter.CommentCallbackInterface,
    View.OnClickListener, ViewPagerAdapter2.ViewPagerCallbackInterface {

    private lateinit var binding: FragmentHomeBinding
    lateinit var radioButton : RadioButton
    private var mDelayHandler: Handler? = null
    var yourPreference: YourPreference? = null
    var mHomePostAdapter: HomePostAdapter? = null
    var mCommentAdapter: CommentAdapter? = null
    var mShareAdapter: ShareAdapter? = null

    val mHomePostList = ArrayList<HomePostModel>()

    var progressDialog: Dialog? = null
    var currentPage: Int = 0
    var lastPage: Int = 0

    val mConnectionListModel = java.util.ArrayList<ConnectionListModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        yourPreference = YourPreference(activity)


        //9589YourPreference.saveData(Constant.IsStudentLogin, false)

        apiUserPushNotificationToken()
        apiCurrentUserDetails()

        setupSwipeListener()
        binding.ibNotification.setOnClickListener(this)
        binding.ivSearch.setOnClickListener(this)



        mHomePostAdapter = HomePostAdapter(mHomePostList, requireActivity(), this, this)
        binding.rvPost.adapter = mHomePostAdapter
        mHomePostAdapter?.notifyDataSetChanged()

        binding.rvPost.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                if (lastVisibleItemPosition == mHomePostAdapter!!.itemCount - 1) {
                    if (currentPage <= lastPage) {
                        currentPage +=1
                        apiHomePosts()

                    }
                }
            }
        })


        apiHomePosts()

    }

    private fun apiUserPushNotificationToken() {
        // Retrieve the FCM token
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // The token has been successfully retrieved
                    val token = task.result

                    val jsonObject = JsonObject().apply {
                        addProperty("token", token)
                    }
                    val call: Call<ResponseBody> =
                        RetrofitClient.getInstance(requireActivity()).myApi.api_NotificationToken(
                           jsonObject
                        )

                    call.enqueue(object : Callback<ResponseBody> {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                val responseJson = response.body()?.string()
                                val responseObject = JSONObject(responseJson.toString())

                                if (responseObject.has("message")) {
                                    var message = responseObject.getString("message");

                                    if (message == "Updated successfully") {



                                    }


                                } else if (responseObject.has("error")) {
                                    UtilsFunctions().showToast(
                                        requireActivity(), responseObject.getString("error")
                                    )
                                }


                            } else {
                                UtilsFunctions().handleErrorResponse(response, requireActivity());
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            t.printStackTrace()
                        }
                    })
                    println("UserFirebasePushNotificationToken : $token")
                    // Use the token as needed
                    // For example, send it to your server to associate the device with the user
                } else {
                    // Failed to retrieve the token
                    // Handle the error
                }
            }
    }


    private fun setupSwipeListener() {
        mDelayHandler = Handler()
        binding.swiperefresh.setColorSchemeColors(resources.getColor(R.color.blue))
        binding.swiperefresh.setOnRefreshListener {
            mDelayHandler!!.postDelayed(mRunnable, Constant.SWIPE_DELAY)
        }


    }

    private val mRunnable: Runnable = Runnable {
        binding.swiperefresh.isRefreshing = false
        apiHomePosts()
        // Toast.makeText(requireActivity(), "Updated!!", Toast.LENGTH_SHORT).show()
    }


    override fun postDetailsCallback(title: String, desc: String, llRead: LinearLayout) {
        val dialog = context?.let { BottomSheetDialog(it, R.style.AppBottomSheetDialogTheme) }
        val view = layoutInflater.inflate(R.layout.bottomsheet_post_details, null)

        var tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        var tvDescription = view.findViewById<TextView>(R.id.tvDescription)

        tvTitle.text = title
        tvDescription.text = desc


        dialog?.setCanceledOnTouchOutside(true)
        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()

        dialog?.setOnDismissListener {
            llRead.isEnabled=true
        }
    }

    @SuppressLint("RestrictedApi", "ResourceType")
    override fun reportAbuseCallback(imageView: ImageView, id: Int) {

        val dialog = context?.let { BottomSheetDialog(it, R.style.AppBottomSheetDialogTheme) }
        val view = layoutInflater.inflate(R.layout.bottomsheet_report_abuse, null)

        val btnReport = view.findViewById<Button>(R.id.btnReport)
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)

        var Clicked = false;
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            Clicked = true
            radioButton = radioGroup.findViewById(checkedId)
        }

        btnReport.setOnClickListener {
            if(UtilsFunctions().singleClickListener()) return@setOnClickListener
            if (Clicked){
                apiReportPost(id, radioButton.text.toString())
                dialog?.dismiss()
            }else{
                UtilsFunctions().showToast(requireActivity(), "Please select any one to report")
            }

        }


        dialog?.setCanceledOnTouchOutside(true)
        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()

        dialog?.setOnDismissListener {
            imageView.isEnabled=true
        }

        /*val menuBuilder = MenuBuilder(context)
        val inflater = MenuInflater(context)
        inflater.inflate(R.menu.home_post_menu, menuBuilder)
        val optionsMenu = activity?.let {
            MenuPopupHelper(
                it,
                menuBuilder,
                view,
                false,
                0,
                R.style.OverflowMenuStyle
            )
        }
        optionsMenu?.setForceShowIcon(true)



        menuBuilder.setCallback(object : MenuBuilder.Callback {
            override fun onMenuItemSelected(menu: MenuBuilder, item: MenuItem): Boolean {
                Toast.makeText(activity, "Reported Successfully", Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onMenuModeChange(menu: MenuBuilder) {}
        })

        optionsMenu?.show();*/
    }


    override fun commentCallback(id: Int, position: Int, llComment: LinearLayout) {
        apiPostComments(id, position,llComment)
    }

    override fun deleteCommentCallback(commentId: Int , position: Int) {
        DeleteCommentBottomSheet(commentId, position);
    }


    override fun onLikeUnlikeCallback(position: Int, id: Int, llLikeUnlike: LinearLayout) {
        apiPostLikeUnLike(id, position,llLikeUnlike)
    }


    override fun onShareCallback(position: Int, llShare: LinearLayout) {
        openShareBottomSheet(llShare)
        apiConnectionsList()

    }

    private fun apiConnectionsList() {

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireActivity()).myApi.api_ConnectionsList()


        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val connection =
                        Gson().fromJson(responseJson, Array<ConnectionListModel>::class.java)
                            .toList()
                    mConnectionListModel.clear()
                    mConnectionListModel.addAll(connection)

                    if (mConnectionListModel.isNotEmpty()) {
                        mShareAdapter?.notifyDataSetChanged()
                    }


                    println("Total Connection Count : ${mConnectionListModel.size}")

                } else {
                    UtilsFunctions().handleErrorResponse(response, requireActivity())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun openShareBottomSheet(llShare: LinearLayout) {
        val dialog = context?.let { BottomSheetDialog(it, R.style.AppBottomSheetDialogTheme) }
        val view = layoutInflater.inflate(R.layout.bottomsheet_share, null)

        val rvShare = view.findViewById<RecyclerView>(R.id.rvShare)
        val etSearch = view.findViewById<EditText>(R.id.etSearch)

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called when the text is being changed
                val newText = s.toString()
                filter(newText)
                // Do something with the new text, like update UI or perform a search
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text has been changed
            }
        })


        mShareAdapter = ShareAdapter(mConnectionListModel, requireActivity())
        rvShare.adapter = mShareAdapter
        mShareAdapter!!.notifyDataSetChanged()


        dialog?.setCanceledOnTouchOutside(true)
        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()

        dialog?.setOnDismissListener {
            llShare.isEnabled=true
        }
    }




    override fun onDeleteCallback(position: Int, view: ImageView, postId: Int) {
        DeletePostBottomSheet(postId, position, view)
    }

    private fun DeleteCommentBottomSheet(commentId: Int, position: Int) {
        val dialog = context?.let { BottomSheetDialog(it, R.style.AppBottomSheetDialogTheme) }
        val view = layoutInflater.inflate(R.layout.bottomsheet_delete_post, null)


        val btnNo = view.findViewById<Button>(R.id.btnNo)
        val tvText = view.findViewById<TextView>(R.id.tvText)
        val btnYes = view.findViewById<Button>(R.id.btnYes)

        tvText.text = getString(R.string.this_comment_will_be_deleted_permanently_from_your_account)

        btnYes.setOnClickListener {
            if(UtilsFunctions().singleClickListener()) return@setOnClickListener
            dialog?.dismiss()
            apiDeleteComment(commentId, position)
        }

        btnNo.setOnClickListener {
            if(UtilsFunctions().singleClickListener()) return@setOnClickListener
            dialog?.dismiss()
        }


        dialog?.setCanceledOnTouchOutside(true)
        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()
    }

    private fun apiPostDelete(id: Int, position: Int) {

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireActivity()).myApi.api_PostDelete(id)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        var message = responseObject.getString("message");
                        if (message == "Deleted successfully") {

                            mCommentAdapter?.removeItem(position)
                        }
                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            requireActivity(), responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, requireActivity())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun apiReportPost(id: Int, value: String) {
        val jsonObject = JsonObject().apply {
            addProperty("postId", id)
            addProperty("report", value)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireActivity()).myApi.api_ReportPost(jsonObject)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {

                        UtilsFunctions().showToast(
                            requireActivity(),
                            responseObject.getString("message")
                        )

                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            requireActivity(), responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, requireActivity())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ibNotification -> {
                val intent = Intent(context, NotificationActivity::class.java)
                startActivity(intent)
            }
            R.id.ivSearch -> {
                val intent = Intent(context, SearchHomeActivity::class.java)
                startActivity(intent)
            }
        }

    }

    private fun apiCurrentUserDetails() {
        // progressDialog = UtilsFunctions().showCustomProgressDialog(requireContext())

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireActivity()).myApi.api_CurrentUserDetails()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    progressDialog?.dismiss()

                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)


                    // Parse the JSON array from the responseObject
                    val hobbiesArray: JSONArray? = responseObject.optJSONArray("hobbies")
                    val skillsArray: JSONArray? = responseObject.optJSONArray("skills")
                    val achievementsArray: JSONArray? = responseObject.optJSONArray("achievements")


                    YourPreference.saveData(Constant.userId, responseObject.getInt("id"))
                    YourPreference.saveData(Constant.avtarUrl, responseObject.getString("avtarUrl"))
                    YourPreference.saveData(
                        Constant.firstName,
                        responseObject.getString("firstName")
                    )
                    YourPreference.saveData(Constant.lastName, responseObject.getString("lastName"))
                    YourPreference.saveData(Constant.mobile, responseObject.getString("mobile"))
                    YourPreference.saveData(Constant.userName, responseObject.getString("userName"))
                    YourPreference.saveData(Constant.bio, responseObject.getString("bio"))
                    YourPreference.saveData(Constant.email, responseObject.getString("email"))
                    YourPreference.saveData(Constant.dob, responseObject.getString("dob"))
                    YourPreference.saveData(Constant.gender, responseObject.getString("gender"))
                    YourPreference.saveData(
                        Constant.bloodGroup, responseObject.getString("bloodGroup")
                    )
                    YourPreference.saveData(
                        Constant.connections, responseObject.getInt("connections")
                    )
                    YourPreference.saveData(Constant.hobbiesList, hobbiesArray.toString())
                    YourPreference.saveData(Constant.skillsList, skillsArray.toString())
                    YourPreference.saveData(Constant.achievementsList, achievementsArray.toString())

                    if (responseObject.has("student")) {
                        val student = responseObject.getJSONObject("student")
                        YourPreference.saveData(Constant.instituteId, student.getInt("instituteId"))
                        YourPreference.saveData(
                            Constant.instituteFacultyId, student.getInt("instituteFacultyId")
                        )
                        YourPreference.saveData(
                            Constant.instituteStreamId, student.getInt("instituteStreamId")
                        )
                        YourPreference.saveData(
                            Constant.instituteStreamClassId,
                            student.getInt("instituteStreamClassId")
                        )
                        YourPreference.saveData(
                            Constant.instituteName, student.getString("instituteName")
                        )
                        YourPreference.saveData(
                            Constant.instituteUsername, student.getString("instituteUsername")
                        )
                        YourPreference.saveData(
                            Constant.facultyName, student.getString("facultyName")
                        )
                        YourPreference.saveData(Constant.className, student.getString("className"))
                        YourPreference.saveData(
                            Constant.streamName, student.getString("streamName")
                        )
                    } else if (responseObject.has("teacher")) {
                        val teacher = responseObject.getJSONObject("teacher")

                        YourPreference.saveData(Constant.instituteId, teacher.getInt("instituteId"))
                        YourPreference.saveData(
                            Constant.instituteFacultyId, teacher.getInt("instituteFacultyId")
                        )
                        YourPreference.saveData(
                            Constant.instituteName, teacher.getString("instituteName")
                        )
                        YourPreference.saveData(
                            Constant.instituteUsername, teacher.getString("instituteUsername")
                        )
                        YourPreference.saveData(
                            Constant.facultyName, teacher.getString("facultyName")
                        )
                    }


//                    setProfile()

                } else {
//                    UtilsFunctions().handleErrorResponse(response, requireActivity())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun apiHomePosts() {
        if (currentPage == 0){
            progressDialog = UtilsFunctions().showCustomProgressDialog(requireActivity())
        }
        val call: Call<ResponseBody> = RetrofitClient.getInstance(requireActivity()).myApi.api_HomePosts(currentPage, 5)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "onDashboardListResponse: ${response.body()}")
//                    progressDialog?.dismiss()
                    lastPage = currentPage
                    val responseJson = response.body()?.string()

                    val homePostList = Gson().fromJson(responseJson, Array<HomePostModel>::class.java).toList()
                    if (currentPage == 0){
                        mHomePostList.clear()
                    }
                    mHomePostList.addAll(homePostList)

                     if (mHomePostList.isNotEmpty()) {
                        mHomePostAdapter?.notifyDataSetChanged()
                    }
                    binding.swiperefresh.visibility=View.VISIBLE
                    binding.noResultFound.visibility=View.GONE

                    progressDialog?.dismiss()

                } else {
                    progressDialog?.dismiss()
                    binding.swiperefresh.visibility=View.GONE
                    binding.noResultFound.visibility=View.VISIBLE
//                    UtilsFunctions().handleErrorResponse(response, requireActivity())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                binding.swiperefresh.visibility=View.GONE
                binding.noResultFound.visibility=View.VISIBLE
                progressDialog?.dismiss()
            }
        })
    }

    private fun apiPostLikeUnLike(id: Int, position: Int, llLikeUnlike: LinearLayout) {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireActivity()).myApi.api_PostLikeUnLike(id)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        var message = responseObject.getString("message");

                        val likesDelta = if (message == "Like successfully") 1 else -1
                        mHomePostList[position].likes += likesDelta
                        mHomePostList[position].isLiked = if (message == "Like successfully") 1 else 0

                        mHomePostAdapter?.notifyDataSetChanged()

                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            requireActivity(), responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, requireActivity())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })

        llLikeUnlike.isEnabled=true

    }


    private fun apiPostComments(id: Int, position: Int, llComment: LinearLayout) {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireActivity()).myApi.api_PostComments(id)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()

                    val mCommentList = ArrayList(Gson().fromJson(responseJson, Array<CommentModel>::class.java).toList())

                    openCommentBottomSheet(id, mCommentList, position,llComment)

                } else {
                    UtilsFunctions().handleErrorResponse(response, requireActivity())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged", "MissingInflatedId")
    private fun openCommentBottomSheet(
        id: Int,
        mCommentList: ArrayList<CommentModel>,
        position: Int,
        llComment: LinearLayout
    ) {
        val dialog = context?.let { BottomSheetDialog(it, R.style.AppBottomSheetDialogTheme) }
        val view = layoutInflater.inflate(R.layout.bottomsheet_comment, null)

        val rvComment = view.findViewById<RecyclerView>(R.id.rvComment)
        val etComment = view.findViewById<EditText>(R.id.etComment)
        val ivSend = view.findViewById<ImageButton>(R.id.ivSend)

        mCommentAdapter = CommentAdapter(mCommentList, requireActivity(), this)
        rvComment.adapter = mCommentAdapter
        mCommentAdapter!!.notifyDataSetChanged()


        ivSend.setOnClickListener {
            if(UtilsFunctions().singleClickListener()) return@setOnClickListener
            if (etComment.text.toString().trim().isNotEmpty()) {
                apiPostWriteComments(id, etComment.text.toString().trim(), position);
                etComment.text.clear()
                dialog?.dismiss()

            }
        }



        dialog?.setCanceledOnTouchOutside(true)
        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()

        dialog?.setOnDismissListener {
            llComment.isEnabled=true
        }
    }

    private fun apiPostWriteComments(id: Int, value: String, position: Int) {
        val jsonObject = JsonObject().apply {
            addProperty("comment", value)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireActivity()).myApi.api_PostWriteComments(
                id,
                jsonObject
            )

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson.toString())

                    if (responseObject.has("message")) {
                        var message = responseObject.getString("message");

                        if (message == "Updated successfully") {

                            mHomePostList[position].comments += 1
                            mHomePostAdapter?.notifyDataSetChanged()

                        }


                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            requireActivity(), responseObject.getString("error")
                        )
                    }


                } else {
                    UtilsFunctions().handleErrorResponse(response, requireActivity());
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun viewPagerImageCallback(position: Int, mPostId: Int) {
        openPostDetail(mHomePostList,mPostId)
//        apiPostDetails(position,mPostId);
    }

    private fun apiPostDetails(mPosition:Int,mPostId: Int) {

        val call: Call<ResponseBody> = RetrofitClient.getInstance(requireActivity()).myApi.api_PostDetails(mPostId)


        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    val id = responseObject.getInt("id")
                    val title = responseObject.getString("title")
                    val desc = responseObject.getString("desc")
                    val name = responseObject.getString("name")
                    val username = responseObject.getString("username")
                    val likes = responseObject.getInt("likes")
                    val instituteName = responseObject.getString("instituteName")
                    val comments = responseObject.getInt("comments")
                    val createdAt = responseObject.getString("createdAt")

                    val photosArray = responseObject.getJSONArray("photos")
                    val photoList = ArrayList<PhotoModel>()
                    for (i in 0 until photosArray.length()) {
                        val photoObject = photosArray.getJSONObject(i)
                        val pathUrl = photoObject.getString("pathUrl")
                        val photo = PhotoModel(pathUrl)
                        photoList.add(photo)
                    }


                    openPostImage(mPosition,title, desc, createdAt, photoList, id, name, likes, comments)


                } else {
                    UtilsFunctions().handleErrorResponse(response, requireActivity())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun openPostDetail(responseObject: ArrayList<HomePostModel>, mPostId: Int) {
        var homePostModel:HomePostModel?=null

                for (i in responseObject){
                    if(i.id == mPostId){
                        homePostModel= HomePostModel(i.id,i.title,i.desc,i.name,"",i.isMyPost,i.isLiked,i.likes,i.instituteName,i.comments,i.createdAt,i.photos,i.user)
                    }
                }

//        Toast.makeText(requireContext(), homePostModel!!.username,Toast.LENGTH_LONG).show()
            activity?.let{
                val intent = Intent (it, PostVIewDetailActivity::class.java)
                intent.putExtra("DATA", homePostModel)
                it.startActivity(intent)
            }
    }

    private fun openPostImage(mPosition:Int, title: String, desc: String, createdAt: String, photosList: ArrayList<PhotoModel>, id: Int, name: String, likes: Int, comments: Int) {


        var inflater = LayoutInflater.from(context)
        var popupview = inflater.inflate(R.layout.popup_post, null, false)

        var builder = PopupWindow(
            popupview,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        builder.animationStyle = R.style.DialogAnimation
        builder.setBackgroundDrawable(context?.getDrawable(R.drawable.bg_rounded_2))
        builder.showAtLocation(this.binding.root, Gravity.BOTTOM, 0, 0)

        popupview.setOnClickListener {
            if(UtilsFunctions().singleClickListener()) return@setOnClickListener
            builder.dismiss()
        }


        var mViewPagerAdapter: ViewPagerAdapter? = null
        var viewPager = popupview.findViewById<ViewPager>(R.id.viewPager)
        var dotsIndicator = popupview.findViewById<ScrollingPagerIndicator>(R.id.dots_indicator)
        val tvName = popupview.findViewById<TextView>(R.id.tvName)
        var tvLikesCount = popupview.findViewById<TextView>(R.id.tvLikesCount)
        var tvCommentCount = popupview.findViewById<TextView>(R.id.tvCommentCount)
        var tvDate = popupview.findViewById<TextView>(R.id.tvDate)
        var ivMore = popupview.findViewById<ImageView>(R.id.ivMore)
        var llRead = popupview.findViewById<LinearLayout>(R.id.llRead)
        var llShare = popupview.findViewById<LinearLayout>(R.id.llShare)


        tvName.text = name
        tvLikesCount.text = likes.toString()
        tvCommentCount.text = comments.toString()
        tvDate.text = UtilsFunctions().getDDMMMMYYYY(createdAt)

        llRead.setOnClickListener {
            if(UtilsFunctions().singleClickListener()) return@setOnClickListener
            postDetailsCallback(title, desc, llRead);
        }

        mViewPagerAdapter = ViewPagerAdapter(requireActivity(), photosList, builder)
        viewPager.adapter = mViewPagerAdapter
        mViewPagerAdapter.notifyDataSetChanged()
        dotsIndicator.attachToPager(viewPager)

        llShare.setOnClickListener {
            if(UtilsFunctions().singleClickListener()) return@setOnClickListener
            openShareBottomSheet(llShare)
            apiConnectionsList()
        }

        ivMore.setOnClickListener {
            if(UtilsFunctions().singleClickListener()) return@setOnClickListener
            /*if (!mVisitor) {
                DeletePostBottomSheet(id , builder)
            }*/
        }

    }

    private fun DeletePostBottomSheet(postId: Int, position: Int, imageView: ImageView) {
        val dialog = context?.let { BottomSheetDialog(it, R.style.AppBottomSheetDialogTheme) }
        val view = layoutInflater.inflate(R.layout.bottomsheet_delete_post, null)


        val btnNo = view.findViewById<Button>(R.id.btnNo)
        val btnYes = view.findViewById<Button>(R.id.btnYes)

        btnYes.setOnClickListener {
            dialog?.dismiss()
            apiPostDelete(postId, position)
        }

        btnNo.setOnClickListener {
            dialog?.dismiss()
        }


        dialog?.setCanceledOnTouchOutside(true)
        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()

        dialog?.setOnDismissListener {
            imageView.isEnabled=true
        }

    }



    private fun apiDeleteComment(commentId: Int, position: Int) {

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireActivity()).myApi.api_DeleteComment(commentId)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        var message = responseObject.getString("message");
                        if (message == "Deleted successfully") {

                            mHomePostAdapter?.removeItem(position)


                        }
                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            requireActivity(), responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, requireActivity())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun filter(key: String) {
        val filteredList: java.util.ArrayList<ConnectionListModel> = java.util.ArrayList()
        for (item in mConnectionListModel) {
            if (item.name.lowercase(Locale.ROOT).contains(key.lowercase(Locale.getDefault()))) {
                filteredList.add(item)
            }
            mShareAdapter?.filterList(filteredList)
        }
    }


}