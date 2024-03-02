package com.softwill.alpha.institute_detail.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.databinding.ItemFacultyStreamFeeBinding
import com.softwill.alpha.institute_detail.model.FacultyStreamResponseItem


class FacultyStreamFeeAdapter(
    private var mList: ArrayList<FacultyStreamResponseItem>,
    val mFeeList: Map<Int, List<FacultyStreamResponseItem>>,
    val instituteId: String,
    private val context: Context
) :
    RecyclerView.Adapter<FacultyStreamFeeAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding: ItemFacultyStreamFeeBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                com.softwill.alpha.R.layout.item_faculty_stream_fee,
                parent,
                false
            )
        return ViewHolder(binding)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {
//
                if(mList[position].instituteStreamId.toString() == instituteId){
                    binding.yrTv.text = mList[position].className
                    binding.llFeesStructure.visibility= View.VISIBLE
                }else{
                    binding.llFeesStructure.visibility= View.GONE
                }

                mFeeList.forEach { (instituteStreamId, streamDataList) ->
                    println("Institute Stream ID: $instituteStreamId")
                    streamDataList.forEach { data ->
                        // binding.yrTv.text=data.className
                        if(mList[position].instituteStreamId==data.instituteStreamId && mList[position].className == data.className) {

                            binding.yrFeesTv.text = data.fees.joinToString { "${it.caste} - ${it.fees}\n" }.replace(",","").trimEnd()
                        }

                    }
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemFacultyStreamFeeBinding) :
        RecyclerView.ViewHolder(binding.root)
}