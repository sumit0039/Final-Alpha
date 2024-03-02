package com.softwill.alpha.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.profile.tabActivity.PhotoModel
import com.softwill.alpha.utils.UtilsFunctions

class ViewPagerAdapterUser(
    private val mContext: Context,
    private val mList: List<PhotoModel>,
    private val mPostId: Int
) :
    PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(mContext)
        val view = layoutInflater!!.inflate(R.layout.item_post, container, false)
        var imageview: ImageView = view.findViewById(R.id.img)

        Glide.with(mContext).load(mList[position].pathUrl)
            .placeholder(R.drawable.icon_no_image)
            .fallback(R.color.gray_light)
            .centerInside()
            .error(R.drawable.icon_no_image)
            .into(imageview)


//        imageview.setOnClickListener {
//            if(UtilsFunctions().singleClickListener()) return@setOnClickListener
//            viewPagerCallbackInterface.viewPagerImageCallback(position , mPostId)
//        }


        container.addView(view, position)
        return view
    }

    override fun getCount(): Int {
        return mList.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
    }

    interface ViewPagerCallbackInterface {
        fun viewPagerImageCallback(position: Int, mPostId: Int)
    }

}