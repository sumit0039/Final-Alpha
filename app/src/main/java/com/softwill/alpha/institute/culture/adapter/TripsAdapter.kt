package com.softwill.alpha.institute.culture.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemTripsBinding
import com.softwill.alpha.institute.culture.model.CultureTripModel
import com.softwill.alpha.utils.UtilsFunctions


class TripsAdapter(
    private val context: Context,
    private val callbackInterface: CallbackInterface,
    private var mList: ArrayList<CultureTripModel>,

    ) : RecyclerView.Adapter<TripsAdapter.ViewHolder>(), GalleryAdapter.CallbackInterface {
    var mGalleryAdapter: GalleryAdapter? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripsAdapter.ViewHolder {



        val binding: ItemTripsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_trips,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {


                binding.tvTitle.text = title
                binding.tvManagedBy.text = managedBy
                binding.tvTotalStudent.text = totalStudent.toString()
                binding.tvDescription.text = tripDetails.toString()
                binding.tvDate.text = UtilsFunctions().getDDMMMYYYY(tripDate)



                mGalleryAdapter = GalleryAdapter(context, this@TripsAdapter, trip_photos)
                val layoutManager =
                    GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)

                binding.rvTripPhoto.setHasFixedSize(true)
                binding.rvTripPhoto.layoutManager = layoutManager
                binding.rvTripPhoto.itemAnimator = DefaultItemAnimator()
                binding.rvTripPhoto.adapter = mGalleryAdapter
                mGalleryAdapter!!.notifyDataSetChanged()

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemTripsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onImageCallback(position: Int, picUrl: String) {
        callbackInterface.onImageCallback(position, picUrl)
    }



    interface CallbackInterface {
        fun onImageCallback(position: Int, picUrl: String)
    }

    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}