package com.softwill.alpha.institute.attendance.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemAttendanceTeacherBinding
import com.softwill.alpha.databinding.RowStudentAttendanceBinding
import com.softwill.alpha.institute.attendance.model.Attendance
import com.softwill.alpha.institute.attendance.model.Student
import com.softwill.alpha.institute.attendance.model.StudentAttendance
import com.softwill.alpha.institute.attendance.model.StudentAttendanceItem
import com.softwill.alpha.institute.attendance.model.StudentAttendanceList
import java.util.ArrayList


class AttendanceStudentAdapter(
    private val context: Context,
    private val mStudentList: ArrayList<StudentAttendanceItem>,
    private val callbackInterface: StudentAbsentPresentAdapterCallbackInterface
) :
    RecyclerView.Adapter<AttendanceStudentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): AttendanceStudentAdapter.ViewHolder {
        val binding: RowStudentAttendanceBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_student_attendance,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mStudentList[position]) {
                binding.monthTextview.text=month
                binding.percentTextview.text = percentage.plus("%") ?: "0%"
            }
        }
    }

    override fun getItemCount(): Int {
        return mStudentList.size
    }


    inner class ViewHolder(val binding: RowStudentAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface StudentAbsentPresentAdapterCallbackInterface {
        fun studentAbsentPresentCallback(attendance: Attendance)
    }

}