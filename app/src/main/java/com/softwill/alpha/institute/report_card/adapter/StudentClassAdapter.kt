package com.softwill.alpha.institute.report_card.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.common.model.LectureClassModel
import com.softwill.alpha.databinding.ItemClassesBinding
import com.softwill.alpha.institute.report_card.model.StudentClassItem


class StudentClassAdapter(
    private val context: Context,
    private var mList: ArrayList<StudentClassItem>,
    private val callbackInterface: LectureClassCallbackInterface,
    var clickPosition: Int = 0,

    ) :
    RecyclerView.Adapter<StudentClassAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding: ItemClassesBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_classes,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvTitle.text = className


                if (position == clickPosition) {
                    binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.blue))
                    binding.tvTitle.setBackgroundResource(R.drawable.bg_rounded_3_selected)
                } else {
                    binding.tvTitle.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.grey_light2
                        )
                    )
                    binding.tvTitle.setBackgroundResource(R.drawable.bg_rounded_3)
                }

                binding.root.setOnClickListener {
                    callbackInterface.reportCardStudentClassClickCallback(mList[position].instituteClassId, position, className)
                    if (clickPosition != holder.adapterPosition) {
                        clickPosition = holder.adapterPosition
                    }
                    notifyDataSetChanged()
                }

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemClassesBinding) :
        RecyclerView.ViewHolder(binding.root)


    interface LectureClassCallbackInterface {
        fun reportCardStudentClassClickCallback(classId: Int, position: Int, className : String)
    }

    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}