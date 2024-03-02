package com.softwill.alpha.institute_detail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.databinding.ItemFacultyStreamBinding
import com.softwill.alpha.institute_detail.model.FacultyStreamResponseItem
import io.grpc.Contexts


class FacultyStreamAdapter(
    private val mList: ArrayList<FacultyStreamResponseItem>,
    private val mappedDataList: Map<Int, List<FacultyStreamResponseItem>>,
    private val context: Context
) :
    RecyclerView.Adapter<FacultyStreamAdapter.ViewHolder>() {

    private lateinit var mFacultyStream2Adapter: FacultyStream2Adapter


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding: ItemFacultyStreamBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                com.softwill.alpha.R.layout.item_faculty_stream,
                parent,
                false
            )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {

            with(mList[position]) {

                if (!isTextDuplicate(mList[position].faculty,position)) {
                    binding.tvfacultyName.text = mList[position].faculty
                    binding.llTop.visibility=View.VISIBLE
                    binding.view.visibility=View.VISIBLE

                }else{
                    binding.llTop.visibility=View.GONE
                    binding.view.visibility=View.GONE
                }


                binding.showMore.setOnClickListener {
                    if (binding.llShow.visibility == View.VISIBLE) {
                        binding.llShow.visibility = View.GONE
                    } else {
                        binding.llShow.visibility = View.VISIBLE
                        mFacultyStream2Adapter = FacultyStream2Adapter(mList,mappedDataList, context)
                        binding.rvFacultyStream2.adapter = mFacultyStream2Adapter
                    }

                }

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemFacultyStreamBinding) :
        RecyclerView.ViewHolder(binding.root)

    // Method to check if the text in the adapter's TextView is duplicate or not
    fun isTextDuplicate(adapter: FacultyStreamAdapter, textToCheck: String): Boolean {
        val itemList = adapter.mList
        for (item in itemList) {
            if (item.faculty == textToCheck) {
                // Found a duplicate text
                return true
            }
        }
        // No duplicate text found
        return false
    }

    // Method to check if the text in the adapter's TextView is duplicate or not
    private fun isTextDuplicate(textToCheck: String, currentPosition: Int): Boolean {
        val itemList = mList
        for (i in 0 until currentPosition) {
            val item = itemList[i]
            if (item.faculty == textToCheck) {
                // Found a duplicate text
                return true
            }
        }
        // No duplicate text found
        return false
    }

}