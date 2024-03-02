package com.softwill.alpha.institute_detail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.databinding.ItemFacultyStream2Binding
import com.softwill.alpha.institute_detail.model.FacultyStreamResponseItem


class FacultyStream2Adapter(
    private var mList: ArrayList<FacultyStreamResponseItem>,
    private var mFeeList: Map<Int, List<FacultyStreamResponseItem>>,
    private val context: Context
) :
    RecyclerView.Adapter<FacultyStream2Adapter.ViewHolder>() {

    private lateinit var mFacultyStream2Adapter: FacultyStreamFeeAdapter

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding: ItemFacultyStream2Binding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                com.softwill.alpha.R.layout.item_faculty_stream2,
                parent,
                false
            )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList) {

                var isShowing:Boolean=false

                if (!isTextDuplicate(mList[position].stream,position)) {
                    binding.tvName.text = mList[position].stream
                    binding.topLl.visibility=View.VISIBLE
                }else{
                    binding.topLl.visibility=View.GONE
                }


                binding.tvShowHide.setOnClickListener {
                    if (binding.llFeesStructure.visibility == View.VISIBLE) {
                        binding.tvShowHide.text = "Show fees"
                        binding.llFeesStructure.visibility = View.GONE
                    } else {
                        binding.tvShowHide.text = "Hide fees"
                        binding.llFeesStructure.visibility = View.VISIBLE
                        mFacultyStream2Adapter = FacultyStreamFeeAdapter(mList,mFeeList,mList[position].instituteStreamId.toString(), context)
                        binding.feeRv.adapter = mFacultyStream2Adapter
                    }

                }

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemFacultyStream2Binding) :
        RecyclerView.ViewHolder(binding.root)


    // Method to check if the text in the adapter's TextView is duplicate or not
    private fun isTextDuplicate(textToCheck: String, currentPosition: Int): Boolean {
        val itemList = mList
        for (i in 0 until currentPosition) {
            val item = itemList[i]
            if (item.stream == textToCheck) {
                // Found a duplicate text
                return true
            }
        }
        // No duplicate text found
        return false
    }

}