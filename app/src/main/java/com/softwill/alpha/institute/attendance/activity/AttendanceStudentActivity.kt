package com.softwill.alpha.institute.attendance.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityAttendanceStudentBinding
import com.softwill.alpha.institute.attendance.adapter.AttendanceStudentAdapter
import com.softwill.alpha.institute.attendance.adapter.AttendanceTeacherAdapter
import com.softwill.alpha.institute.attendance.model.Attendance
import com.softwill.alpha.institute.attendance.model.GetStudentAttendanceListItem
import com.softwill.alpha.institute.attendance.model.Student
import com.softwill.alpha.institute.attendance.model.StudentAttendance
import com.softwill.alpha.institute.attendance.model.StudentAttendanceItem
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class AttendanceStudentActivity : AppCompatActivity(), AttendanceStudentAdapter.StudentAbsentPresentAdapterCallbackInterface {

    private lateinit var binding: ActivityAttendanceStudentBinding
    private val mStudentAttendanceItem = ArrayList<StudentAttendanceItem>()
    private val mStudentAttendance:StudentAttendance?=null
    var mAttendanceStudentAdapter: AttendanceStudentAdapter? = null

    var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            com.softwill.alpha.R.layout.activity_attendance_student
        )
        progressDialog = UtilsFunctions().showCustomProgressDialog(this@AttendanceStudentActivity)

        setupBack()
        setupAdapter()
        apiAttendance()

    }

    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_attendance)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        supportActionBar!!.title = (Html.fromHtml("<font color=\"#060B13\">" + resources.getString(R.string.title_attendance) + "</font>"));

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupAdapter() {

        mAttendanceStudentAdapter = AttendanceStudentAdapter(this, mStudentAttendanceItem, this)
        binding.rvStudentAttendance.adapter = mAttendanceStudentAdapter
        mAttendanceStudentAdapter!!.notifyDataSetChanged()

    }

    private fun apiAttendance() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@AttendanceStudentActivity).myApi.api_Attendance()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {

                        Log.e(ContentValues.TAG, "onAttendanceResponse: ${responseBody.toString()}")
                        val listType = object : TypeToken<ArrayList<StudentAttendanceItem>>() {}.type
                        val mList: ArrayList<StudentAttendanceItem> = Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mStudentAttendanceItem.clear()
                        mStudentAttendanceItem.addAll(mList)

                        if (mStudentAttendanceItem.isNotEmpty()) {
                            binding.rvStudentAttendance.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                            mAttendanceStudentAdapter?.notifyDataSetChanged()
                        } else {
                            binding.rvStudentAttendance.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                        progressDialog?.dismiss()

                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@AttendanceStudentActivity)
                    progressDialog?.dismiss()

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                progressDialog?.dismiss()

            }
        })
    }

    override fun studentAbsentPresentCallback(attendance: Attendance) {
    }


}