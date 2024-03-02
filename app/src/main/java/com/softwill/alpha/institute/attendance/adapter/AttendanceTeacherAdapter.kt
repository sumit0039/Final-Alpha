package com.softwill.alpha.institute.attendance.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemAttendanceTeacherBinding
import com.softwill.alpha.institute.attendance.model.Attendance
import com.softwill.alpha.institute.attendance.model.GetStudentAttendanceListItem
import com.softwill.alpha.institute.attendance.model.Student
import java.util.ArrayList


class AttendanceTeacherAdapter(
    private val context: Context,
    private val mStudentList: MutableList<Student>,
    private val mGetStudentAttendanceListItem: ArrayList<GetStudentAttendanceListItem>,
    private val callbackInterface: StudentAbsentPresentAdapterCallbackInterface
) :
    RecyclerView.Adapter<AttendanceTeacherAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): AttendanceTeacherAdapter.ViewHolder {
        val binding: ItemAttendanceTeacherBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_attendance_teacher,
            parent,
            false
        )
        return ViewHolder(binding)


    }


    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mStudentList[position]) {

                Glide.with(context).load(mStudentList[position].User.avtarUrl)
                    .placeholder(R.drawable.baseline_account_circle_24).into(binding.userImg)

                binding.studentName.text = firstName
                if (attendancePercentage != null) {
                    binding.attendancePercentage.text = attendancePercentage.plus("%")
                } else {
                    binding.attendancePercentage.text = "0%"
                }
            }


          mGetStudentAttendanceListItem.forEach {getStudentAttendanceListItem: GetStudentAttendanceListItem ->
              if(getStudentAttendanceListItem.studentId==mStudentList[position].studentId && getStudentAttendanceListItem.present == 1){
                  binding.presentBtn.setBackgroundResource(R.drawable.btn_present_bg)
                  binding.presentBtn.setTextColor(
                      ContextCompat.getColor(
                          context,
                          R.color.white
                      )
                  )
                  binding.absentBtn.setBackgroundResource(R.drawable.button_background)

                  binding.absentBtn.setTextColor(
                      ContextCompat.getColor(
                          context,
                          R.color.black
                      )
                  )
              }

              if(getStudentAttendanceListItem.studentId==mStudentList[position].studentId && getStudentAttendanceListItem.present == 2){
                  binding.presentBtn.setBackgroundResource(R.drawable.button_background)

                  binding.presentBtn.setTextColor(
                      ContextCompat.getColor(
                          context,
                          R.color.black
                      )
                  )
                  binding.absentBtn.setBackgroundResource(R.drawable.btn_absent_bg)

                  binding.absentBtn.setTextColor(
                      ContextCompat.getColor(
                          context,
                          R.color.white
                      )
                  )
              }

              /*if(getStudentAttendanceListItem.studentId==mStudentList[position].studentId && getStudentAttendanceListItem.present != 1 && getStudentAttendanceListItem.present != 2){
                  binding.presentBtn.setBackgroundResource(R.drawable.button_background)
                  binding.presentBtn.setTextColor(
                      ContextCompat.getColor(
                          context,
                          R.color.black
                      )
                  )

                  binding.absentBtn.setBackgroundResource(R.drawable.button_background)
                  binding.absentBtn.setTextColor(
                      ContextCompat.getColor(
                          context,
                          R.color.black
                      )
                  )
              }*/

          }

          /*  when(mStudentList[position].present){
                1->{
                    binding.presentBtn.setBackgroundResource(R.drawable.btn_present_bg)
                    binding.presentBtn.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )
                    binding.absentBtn.setBackgroundResource(R.drawable.button_background)

                    binding.absentBtn.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.black
                        )
                    )
                }
                2->{
                    binding.presentBtn.setBackgroundResource(R.drawable.button_background)

                    binding.presentBtn.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.black
                        )
                    )
                    binding.absentBtn.setBackgroundResource(R.drawable.btn_absent_bg)

                    binding.absentBtn.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )
                }
                0->{
                    binding.presentBtn.setBackgroundResource(R.drawable.button_background)

                    binding.presentBtn.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.black
                        )
                    )
                    binding.absentBtn.setBackgroundResource(R.drawable.button_background)

                    binding.absentBtn.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.black
                        )
                    )
                }
            }
          */

            binding.presentBtn.setOnClickListener {

                binding.presentBtn.setBackgroundResource(R.drawable.btn_present_bg)
                binding.presentBtn.setTextColor(ContextCompat.getColor(
                    context,
                    R.color.white
                ))

                binding.absentBtn.setBackgroundResource(R.drawable.button_background)
                binding.absentBtn.setTextColor(ContextCompat.getColor(
                    context,
                    R.color.black
                ))

                mStudentList[position].present=1

                val attendance = Attendance(2,mStudentList[position].studentId)
                callbackInterface.studentAbsentPresentCallback(attendance, mStudentList[position])
                binding.presentBtn.isEnabled= false
                binding.absentBtn.isEnabled= true
            }

            binding.absentBtn.setOnClickListener {

                binding.absentBtn.setBackgroundResource(R.drawable.btn_absent_bg)
                binding.absentBtn.setTextColor(ContextCompat.getColor(
                    context,
                    R.color.white
                ))

                binding.presentBtn.setBackgroundResource(R.drawable.button_background)
                binding.presentBtn.setTextColor(ContextCompat.getColor(
                    context,
                    R.color.black
                ))

                mStudentList[position].present=2

                val attendance = Attendance(1,mStudentList[position].studentId)
                callbackInterface.studentAbsentPresentCallback(attendance, mStudentList[position])
                binding.presentBtn.isEnabled= true
                binding.absentBtn.isEnabled= false
            }

        }

    }

    override fun getItemCount(): Int {
        return mStudentList.size
    }
    private fun isStudentIdPresent(studentId: Int, studentId1: Int): Boolean {
        return studentId.equals(studentId1)
    }

    inner class ViewHolder(val binding: ItemAttendanceTeacherBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface StudentAbsentPresentAdapterCallbackInterface {
        fun studentAbsentPresentCallback(attendance: Attendance, student: Student)
    }

}