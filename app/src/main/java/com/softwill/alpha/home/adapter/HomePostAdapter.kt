package com.softwill.alpha.home.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemHomePostBinding
import com.softwill.alpha.home.model.HomePostModel
import com.softwill.alpha.profile.tabActivity.PhotoModel
import com.softwill.alpha.profile_guest.activity.ProfileGuestActivity
import com.softwill.alpha.utils.UtilsFunctions


class HomePostAdapter(
    private val mList: ArrayList<HomePostModel>,
    private val context: Context,
    private val callbackInterface: CallbackInterface,
    private val viewPagerCallbackInterface2 : ViewPagerAdapter2.ViewPagerCallbackInterface
) :
    RecyclerView.Adapter<HomePostAdapter.ViewHolder>() {


    var mViewPagerAdapter2: ViewPagerAdapter2? = null


    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemHomePostBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            com.softwill.alpha.R.layout.item_home_post,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    // binds the list items to a view

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {


                mViewPagerAdapter2 = ViewPagerAdapter2(context, photos.reversed(), viewPagerCallbackInterface2, id )
                binding.viewPager.adapter = mViewPagerAdapter2
                mViewPagerAdapter2!!.notifyDataSetChanged()
                binding.dotsIndicator.attachToPager(binding.viewPager)


                binding.tvName.text = name
                binding.tvInstituteName.text = instituteName
                if(likes==-1){
                    binding.tvLikeCount.text = "0"
                }else {
                    binding.tvLikeCount.text = likes.toString()
                }
                binding.tvCommentCount.text = comments.toString()
                binding.tvDate.text = UtilsFunctions().getDDMMMMYYYY(createdAt)

                if (isMyPost == 1){
                    binding.ivMore.setImageResource(R.drawable.ic_more)
                    binding.ivMore.setColorFilter(
                        ContextCompat.getColor(context, R.color.black),
                        android.graphics.PorterDuff.Mode.MULTIPLY
                    )
                }else{
                    binding.ivMore.setImageResource(R.drawable.ic_more)
                    binding.ivMore.setColorFilter(
                        ContextCompat.getColor(context, R.color.black),
                        android.graphics.PorterDuff.Mode.MULTIPLY
                    )
                }

                if (!user.avtarUrl.isNullOrEmpty()){
                    Glide.with(context).load(user.avtarUrl).placeholder(R.drawable.baseline_account_circle_24).error(R.drawable.baseline_account_circle_24).into(binding.ivProfileImage)
                }

                if(isLiked == 1){
                    binding.ivLiked.setImageResource(R.drawable.thumbs_up_black_icon)
                }else{
                    binding.ivLiked.setImageResource(R.drawable.thumbs_icon)
                }

                binding.ivMore.setOnClickListener {
                    binding.ivMore.isEnabled= false
                    if(UtilsFunctions().singleClickListener()) return@setOnClickListener
                    if (isMyPost == 1) {
                        callbackInterface.onDeleteCallback(position, binding.ivMore , id)
                    } else {
                        callbackInterface.reportAbuseCallback(binding.ivMore , id)
                    }
                }

                binding.llRead.setOnClickListener {
                    binding.llRead.isEnabled=false
                    if(UtilsFunctions().singleClickListener()) return@setOnClickListener
                    callbackInterface.postDetailsCallback(title, desc,binding.llRead);
                }

                binding.llComment.setOnClickListener {
                   binding.llComment.isEnabled=false
                    if(UtilsFunctions().singleClickListener()) return@setOnClickListener
                    callbackInterface.commentCallback(id , position,binding.llComment);
                }

                binding.ivProfileImage.setOnClickListener {
                    if(UtilsFunctions().singleClickListener()) return@setOnClickListener
                    if (isMyPost != 1){
                        val intent = Intent(context, ProfileGuestActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                        intent.putExtra("mUserId", user.id)
                        context.startActivity(intent)
                    }
                }

                binding.llShare.setOnClickListener {
                    binding.llShare.isEnabled=false
                    if(UtilsFunctions().singleClickListener()) return@setOnClickListener
                    callbackInterface.onShareCallback(position,photos,binding.llShare);
                }

                binding.llLikeUnlike.setOnClickListener {
                  binding.llLikeUnlike.isEnabled=false
                    if(UtilsFunctions().singleClickListener()) return@setOnClickListener
                    callbackInterface.onLikeUnlikeCallback(position, id,binding.llLikeUnlike)
                }



            }
        }


    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemHomePostBinding) : RecyclerView.ViewHolder(binding.root)


    interface CallbackInterface {
        fun postDetailsCallback(title: String, desc: String, llRead: LinearLayout)
        fun reportAbuseCallback(view: ImageView, id: Int)
        fun commentCallback(id: Int, position: Int, llComment: LinearLayout)
        fun onShareCallback(position: Int, photos: List<PhotoModel>, llShare: LinearLayout)
        fun onDeleteCallback(position: Int, view : ImageView, postId : Int)
        fun onLikeUnlikeCallback(position: Int, id: Int, llLikeUnlike: LinearLayout)
    }



    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }
}