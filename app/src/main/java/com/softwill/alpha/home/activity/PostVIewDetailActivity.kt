package com.softwill.alpha.home.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.softwill.alpha.R
import com.softwill.alpha.home.adapter.CommentAdapter
import com.softwill.alpha.home.adapter.ShareAdapter
import com.softwill.alpha.home.adapter.ViewPagerAdapterUser
import com.softwill.alpha.home.model.CommentModel
import com.softwill.alpha.home.model.HomePostModel
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.profile.tabActivity.ActivityPostAdapter
import com.softwill.alpha.profile.tabActivity.PhotoModel
import com.softwill.alpha.profile.tabActivity.PostModel
import com.softwill.alpha.profile_guest.adapter.ConnectionListModel
import com.softwill.alpha.profile_guest.model.GuestUserDetailsResponse
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import com.softwill.alpha.utils.YourPreference
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale


class PostVIewDetailActivity : AppCompatActivity(), ActivityPostAdapter.CallbackInterface , CommentAdapter.CommentCallbackInterface {

    private lateinit var binding: com.softwill.alpha.databinding.ActivityPostViewDetailBinding
//    private val viewPagerCallbackInterface2 : ViewPagerAdapter2.ViewPagerCallbackInterface = TODO()

    //    lateinit var mActivityPostAdapter: ActivityPostAdapter
    var mIsGuestUser: Boolean = false
    val mPostList = ArrayList<PostModel>()
    var yourPreference: YourPreference? = null
    var mCommentAdapter: CommentAdapter? = null
    var mCommentCount : Int = 0
    var mLikeCount : Int = 0
    var mName : String = ""
    lateinit var ivLiked : ImageView
    lateinit var tvLikesCount : TextView
    var mUserId: Int = -1
    var mGuestProfileImage : String = ""
    val mHomePostList = ArrayList<HomePostModel>()
    lateinit var radioButton : RadioButton

    val mConnectionListModel = java.util.ArrayList<ConnectionListModel>()
    var mShareAdapter: ShareAdapter? = null

    private var postModel: HomePostModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.softwill.alpha.R.layout.activity_post_view_detail)
        yourPreference = YourPreference(this)

        val extras = intent.extras
        if (extras != null) {
            postModel = intent.getSerializableExtra("DATA") as HomePostModel? //Obtaining data
            if (postModel != null) {
                openPostImage(postModel!!)
            }
        }

        setupBack(postModel)

        mPostList.clear()


        if (mIsGuestUser) {
            apiGuestUserDetails()
        }else{
            apiPosts();
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val findMenuItems = menuInflater
        findMenuItems.inflate(R.menu.more_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_more -> {
                item.isEnabled = false
                if (postModel!!.isMyPost==1) {
                    DeletePostBottomSheet(item,postModel!!.id)
                }else{
                    reportAbuseCallback(item,postModel!!.id)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("ResourceAsColor")
    private fun setupBack(postModel: HomePostModel?) {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = (Html.fromHtml("<font color=\"#060B13\">" + postModel!!.name + "</font>"));

    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables", "SuspiciousIndentation")
    private fun openPostImage(
       postModel : HomePostModel
    ) {

        var mViewPagerAdapter2: ViewPagerAdapterUser? = null
        val tvName = findViewById<TextView>(R.id.tvName)
        val tvDate = findViewById<TextView>(R.id.tvDate)
        tvLikesCount = findViewById(R.id.tvLikeCount)
        val tvCommentCount = findViewById<TextView>(R.id.tvCommentCount)
        val ivMore = findViewById<ImageView>(R.id.ivMore)
        val llRead = findViewById<LinearLayout>(R.id.llRead)
        val llComment = findViewById<LinearLayout>(R.id.llComment)
        val ivProfileImage = findViewById<CircleImageView>(R.id.ivProfileImage)
        ivLiked = findViewById(R.id.ivLiked)
        val llLikeUnlike = findViewById<LinearLayout>(R.id.llLikeUnlike)
        var llShare = findViewById<LinearLayout>(R.id.llShare)

        if (!mIsGuestUser) {
            ivMore.setImageResource(R.drawable.ic_more)
            ivMore.setColorFilter(
                ContextCompat.getColor(this, R.color.black),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            if(postModel.user.avtarUrl!=null) {
                Glide.with(this).load(postModel.user.avtarUrl)
                    .placeholder(R.drawable.baseline_account_circle_24).circleCrop()
                    .into(ivProfileImage)
            }else{
                Glide.with(this).load(yourPreference?.getData(Constant.avtarUrl))
                    .placeholder(R.drawable.baseline_account_circle_24).circleCrop()
                    .into(ivProfileImage)
            }
        }  else {
            ivMore.setImageResource(R.drawable.ic_more)
            ivMore.setColorFilter(
                ContextCompat.getColor(this, R.color.black),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )

        }


//        tvName.text = postModel.name
//        tvDate.text = UtilsFunctions().getDDMMMMYYYY(postModel.createdAt)
        tvLikesCount.text = postModel.likes.toString()
        tvCommentCount.text = postModel.comments.toString()

        if (postModel.isLiked == 1) {
            ivLiked.setImageResource(R.drawable.thumbs_up_black_icon)
        } else {
            ivLiked.setImageResource(R.drawable.thumbs_icon)
        }

        llRead.setOnClickListener {
          llRead.isEnabled= false
            if(UtilsFunctions().singleClickListener()) return@setOnClickListener
            postDetailsCallback(postModel.title, postModel.desc);
        }

        llComment.setOnClickListener {
            binding.llComment.isEnabled = false
            if(UtilsFunctions().singleClickListener()) return@setOnClickListener
            apiPostComments(postModel.id, postModel.comments)
        }

        llLikeUnlike.setOnClickListener {
            llLikeUnlike.isEnabled=false
            if(UtilsFunctions().singleClickListener()) return@setOnClickListener
            apiPostLikeUnLike(postModel.id)
        }

        mViewPagerAdapter2 = ViewPagerAdapterUser(this@PostVIewDetailActivity, postModel.photos.reversed(), postModel.id )
        binding.viewPager.adapter = mViewPagerAdapter2
        mViewPagerAdapter2!!.notifyDataSetChanged()
        binding.dotsIndicator.attachToPager(binding.viewPager)

        /*ivMore.setOnClickListener {
            binding.ivMore.isEnabled = false
            if(UtilsFunctions().singleClickListener()) return@setOnClickListener
            if (!mIsGuestUser) {
                DeletePostBottomSheet(item, postModel.id)
            }
        }*/

        llShare.setOnClickListener {
            llShare.isEnabled= false
            if(UtilsFunctions().singleClickListener()) return@setOnClickListener
            openShareBottomSheet()
            apiConnectionsList()
        }

    }


    private fun apiPostDelete(id: Int) {


        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this).myApi.api_PostDelete(id)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        var message = responseObject.getString("message");
                        if (message == "Deleted successfully") {
                            apiPosts()
                        }
                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            this@PostVIewDetailActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@PostVIewDetailActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }



    private fun apiPostComments(id: Int, tvCommentCount: Int) {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@PostVIewDetailActivity).myApi.api_PostComments(id)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()

                    val mCommentList = ArrayList(Gson().fromJson(responseJson, Array<CommentModel>::class.java).toList())

                    openCommentBottomSheet(id, mCommentList,  tvCommentCount)

                } else {
                    UtilsFunctions().handleErrorResponse(response, this@PostVIewDetailActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun apiPosts() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@PostVIewDetailActivity).myApi.api_posts()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()

                    val postList = Gson().fromJson(responseJson, Array<PostModel>::class.java).toList()
                    mPostList.clear()

                    for (post in postList) {
                        if (post.photos.isNotEmpty()) {
                            mPostList.add(post)
                        }
                    }


                    /*if (mPostList.isNotEmpty()) {
                        mActivityPostAdapter.notifyDataSetChanged()
                    }*/


                } else {
                    UtilsFunctions().handleErrorResponse(response, this@PostVIewDetailActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun postDetailsCallback(title: String, desc: String) {

        val dialog = let { BottomSheetDialog(it, R.style.AppBottomSheetDialogTheme) }
        val view = layoutInflater.inflate(R.layout.bottomsheet_post_details, null)

        var tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        var tvDescription = view.findViewById<TextView>(R.id.tvDescription)

        tvTitle.text = title
        tvDescription.text = desc


        dialog?.setCanceledOnTouchOutside(true)
        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()

        dialog.setOnDismissListener {
            binding.llRead.isEnabled= true
        }
    }

    private fun apiConnectionsList() {

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@PostVIewDetailActivity).myApi.api_ConnectionsList()


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
                    UtilsFunctions().handleErrorResponse(response, this@PostVIewDetailActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun openShareBottomSheet() {
        val dialog = let { BottomSheetDialog(it, R.style.AppBottomSheetDialogTheme) }
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


        mShareAdapter = ShareAdapter(mConnectionListModel,
            postModel!!.photos as ArrayList<PhotoModel>, this@PostVIewDetailActivity)
        rvShare.adapter = mShareAdapter
        mShareAdapter!!.notifyDataSetChanged()


        dialog?.setCanceledOnTouchOutside(true)
        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()

        dialog.setOnDismissListener {
            binding.llShare.isEnabled = true

        }
    }


    private fun apiPostLikeUnLike(id: Int) {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@PostVIewDetailActivity).myApi.api_PostLikeUnLike(id)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson.toString())

                    if (responseObject.has("message")) {
                        var message = responseObject.getString("message");

                        val likesDelta = if (message == "Like successfully") 1 else -1
                        postModel!!.likes += likesDelta
                        postModel!!.isLiked = if (message == "Like successfully") 1 else 0

                       /*  if (postModel?.isLiked==1){
                            mLikeCount += postModel!!.likes
                        }else{
                            mLikeCount += postModel!!.likes
                        }*/

                        if (message == "Like successfully") {
                            ivLiked.setImageResource(R.drawable.thumbs_up_black_icon)
                        } else {
                            ivLiked.setImageResource(R.drawable.thumbs_icon)
                        }

                        tvLikesCount.text = postModel!!.likes.toString()

                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            this@PostVIewDetailActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@PostVIewDetailActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })

        binding.llLikeUnlike.isEnabled= true
    }


    private fun DeletePostBottomSheet(item: MenuItem, id: Int) {
        val dialog = let { BottomSheetDialog(this@PostVIewDetailActivity, R.style.AppBottomSheetDialogTheme) }
        val view = layoutInflater.inflate(R.layout.bottomsheet_delete_post, null)


        val btnNo = view.findViewById<Button>(R.id.btnNo)
        val btnYes = view.findViewById<Button>(R.id.btnYes)

        btnYes.setOnClickListener {
            dialog?.dismiss()
            apiPostDelete(id)
        }

        btnNo.setOnClickListener {
            dialog?.dismiss()
        }


        dialog?.setCanceledOnTouchOutside(true)
        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()

        dialog.setOnDismissListener {
            item.isEnabled = true
        }
    }

    override fun onImageCallback(
        position: Int,
        id: Int,
    ) {
//        apiPostDetails(id)
    }

    override fun deleteCommentCallback(commentId: Int, position: Int) {
        DeleteCommentBottomSheet(commentId, position);
    }


    private fun DeleteCommentBottomSheet(commentId: Int, position: Int) {
        val dialog = let { BottomSheetDialog(it, R.style.AppBottomSheetDialogTheme) }
        val view = layoutInflater.inflate(R.layout.bottomsheet_delete_post, null)


        val btnNo = view.findViewById<Button>(R.id.btnNo)
        val tvText = view.findViewById<TextView>(R.id.tvText)
        val btnYes = view.findViewById<Button>(R.id.btnYes)

        tvText.text = getString(R.string.this_comment_will_be_deleted_permanently_from_your_account)

        btnYes.setOnClickListener {
            dialog?.dismiss()
            apiDeleteComment(commentId, position)
        }

        btnNo.setOnClickListener {
            dialog?.dismiss()
        }


        dialog?.setCanceledOnTouchOutside(true)
        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()
    }


    private fun apiDeleteComment(commentId: Int, position: Int) {

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@PostVIewDetailActivity).myApi.api_DeleteComment(commentId)

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
                            this@PostVIewDetailActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@PostVIewDetailActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun apiGuestUserDetails() {

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@PostVIewDetailActivity).myApi.api_GuestUserDetails(mUserId)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = Gson().fromJson(responseJson, GuestUserDetailsResponse::class.java)
                    mPostList.clear()

                    mGuestProfileImage = responseObject.avtarUrl.toString()

                    for (post in responseObject.posts!!) {
                        if (post.photos.isNotEmpty()) {
                            mPostList.add(post)
                        }
                    }

//                    if (mPostList.isNotEmpty()) {
//                        mActivityPostAdapter.notifyDataSetChanged()
//                    }




                } else {
                    UtilsFunctions().handleErrorResponse(response, this@PostVIewDetailActivity)
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

    @SuppressLint("NotifyDataSetChanged")
    private fun openCommentBottomSheet(id: Int, mCommentList: ArrayList<CommentModel>, position: Int) {
        val dialog = this@PostVIewDetailActivity?.let { BottomSheetDialog(it, R.style.AppBottomSheetDialogTheme) }
        val view = layoutInflater.inflate(R.layout.bottomsheet_comment, null)

        val rvComment = view.findViewById<RecyclerView>(R.id.rvComment)
        val etComment = view.findViewById<EditText>(R.id.etComment)
        val ivSend = view.findViewById<ImageButton>(R.id.ivSend)


        mCommentAdapter = CommentAdapter(mCommentList, this@PostVIewDetailActivity, this)
        rvComment.adapter = mCommentAdapter
        mCommentAdapter!!.notifyDataSetChanged()


        ivSend.setOnClickListener {
            if (etComment.text.toString().trim().isNotEmpty()) {
                apiPostWriteComments(id, etComment.text.toString().trim(), position);
                etComment.text.clear()
                dialog?.dismiss()

            }
        }



        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog?.show()

        dialog.setOnDismissListener {
            binding.llComment.isEnabled = true
        }
    }

    private fun apiPostWriteComments(id: Int, value: String, position: Int) {
        val jsonObject = JsonObject().apply {
            addProperty("comment", value)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@PostVIewDetailActivity).myApi.api_PostWriteComments(
                id,
                jsonObject
            )

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)



                    if (responseObject.has("message")) {
                        var message = responseObject.getString("message");

                        if (message == "Updated successfully") {

                            mHomePostList[position].comments += 1
//                            mHomePostAdapter?.notifyDataSetChanged()

                        }


                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            this@PostVIewDetailActivity, responseObject.getString("error")
                        )
                    }


                } else {
                    UtilsFunctions().handleErrorResponse(response, this@PostVIewDetailActivity);
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    fun reportAbuseCallback(item: MenuItem, id: Int) {

        val dialog = let { BottomSheetDialog(it, R.style.AppBottomSheetDialogTheme) }
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
                UtilsFunctions().showToast(this, "Please select any one to report")
            }

        }


        dialog?.setCanceledOnTouchOutside(true)
        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()

        dialog?.setOnDismissListener {
            item.isEnabled=true
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

    private fun apiReportPost(id: Int, value: String) {
        val jsonObject = JsonObject().apply {
            addProperty("postId", id)
            addProperty("report", value)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this).myApi.api_ReportPost(jsonObject)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {

                        UtilsFunctions().showToast(
                            this@PostVIewDetailActivity,
                            responseObject.getString("message")
                        )

                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            this@PostVIewDetailActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@PostVIewDetailActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

}