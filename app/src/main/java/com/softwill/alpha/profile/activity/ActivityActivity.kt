package com.softwill.alpha.profile.activity

import android.annotation.SuppressLint
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
import com.softwill.alpha.databinding.ActivityActivityBinding
import com.softwill.alpha.home.model.HomePostModel
import com.softwill.alpha.institute.attendance.model.StudentAttendanceItem
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.profile.activity.model.AttendenceGroupByMonthItem
import com.softwill.alpha.profile.activity.model.GroupByCurrentClass
import com.softwill.alpha.profile.activity.model.GroupByDay
import com.softwill.alpha.profile.activity.model.GroupByDayItem
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActivityBinding
    val mGroupByDayItem = ArrayList<GroupByDayItem>()
    val mAttendanceGroupByMonthItem = ArrayList<AttendenceGroupByMonthItem>()
    var mGroupByCurrentClass:GroupByCurrentClass?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_activity)


        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        supportActionBar!!.title = (Html.fromHtml("<font color=\"#060B13\">" + "General reports" + "</font>"));

        apiAttendanceByCurrentClass()
        apiAttendanceByMonth()
        apiAttendanceByDay()

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

    private fun apiAttendanceByCurrentClass() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@ActivityActivity).myApi.api_AttendanceGroupByCurrentClass()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {

                        Log.e(ContentValues.TAG, "currentClass: ${responseBody.toString()}")

                        val listType = object : TypeToken<GroupByCurrentClass>() {}.type
                        mGroupByCurrentClass = Gson().fromJson(responseBody, listType)
//
//                        // Update the mTransportTeamMember list with the new data
//                        mStudentAttendanceItem.clear()
//                        mStudentAttendanceItem.addAll(mList)
//
//                        if (mStudentAttendanceItem.isNotEmpty()) {
//                            binding.rvStudentAttendance.visibility = View.VISIBLE
//                            binding.tvNoData.visibility = View.GONE
//                            mAttendanceStudentAdapter?.notifyDataSetChanged()
//                        } else {
//                            binding.rvStudentAttendance.visibility = View.GONE
//                            binding.tvNoData.visibility = View.VISIBLE
//                        }

                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@ActivityActivity)

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()

            }
        })
    }


    private fun apiAttendanceByMonth() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@ActivityActivity).myApi.api_AttendanceGroupByMonth()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {

                        Log.e(ContentValues.TAG, "groupBy=month: ${responseBody.toString()}")
                        val listType = object : TypeToken<ArrayList<AttendenceGroupByMonthItem>>() {}.type
                        val mList: ArrayList<AttendenceGroupByMonthItem> = Gson().fromJson(responseBody, listType)

                        mAttendanceGroupByMonthItem.clear()
                        mAttendanceGroupByMonthItem.addAll(mList)
//
//                        if (mStudentAttendanceItem.isNotEmpty()) {
//                            binding.rvStudentAttendance.visibility = View.VISIBLE
//                            binding.tvNoData.visibility = View.GONE
//                            mAttendanceStudentAdapter?.notifyDataSetChanged()
//                        } else {
//                            binding.rvStudentAttendance.visibility = View.GONE
//                            binding.tvNoData.visibility = View.VISIBLE
//                        }
    
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@ActivityActivity)

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()

            }
        })
    }

    private fun apiAttendanceByDay() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@ActivityActivity).myApi.api_AttendanceGroupByDay()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {

                        Log.e(ContentValues.TAG, "groupBy=day: ${responseBody.toString()}")
                        val listType = object : TypeToken<ArrayList<GroupByDayItem>>() {}.type
                        val mList: ArrayList<GroupByDayItem> = Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mGroupByDayItem.clear()
                        mGroupByDayItem.addAll(mList)
//
//                        if (mStudentAttendanceItem.isNotEmpty()) {
//                            binding.rvStudentAttendance.visibility = View.VISIBLE
//                            binding.tvNoData.visibility = View.GONE
//                            mAttendanceStudentAdapter?.notifyDataSetChanged()
//                        } else {
//                            binding.rvStudentAttendance.visibility = View.GONE
//                            binding.tvNoData.visibility = View.VISIBLE
//                        }
    
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@ActivityActivity)

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()

            }
        })
    }
}