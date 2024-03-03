package com.softwill.alpha.institute.report_card.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.common.adapter.LectureClassAdapter
import com.softwill.alpha.common.model.LectureClassModel
import com.softwill.alpha.databinding.ActivityReportCardStudentBinding
import com.softwill.alpha.institute.report_card.adapter.ReportCardStudentAdapter
import com.softwill.alpha.institute.report_card.adapter.StudentClassAdapter
import com.softwill.alpha.institute.report_card.model.ReportCard
import com.softwill.alpha.institute.report_card.model.StudentClass
import com.softwill.alpha.institute.report_card.model.StudentClassItem
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportCardStudentActivity : AppCompatActivity(), StudentClassAdapter.LectureClassCallbackInterface {

    private lateinit var binding: ActivityReportCardStudentBinding
    private var mReportCardStudentAdapter: ReportCardStudentAdapter? = null
    private var mLectureClassAdapter: StudentClassAdapter? = null
    val mLectureClassModel = ArrayList<StudentClassItem>()
    var mClassId: Int = -1
    var mClassName: String = ""
    private val mReportCard = ArrayList<ReportCard>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            com.softwill.alpha.R.layout.activity_report_card_student
        )


        setupBack()

        setupClassAdapter()

        setupReportCardStudentAdapter()

        apiClassList()

//        apiReportCardList(mClassId)
    }

    private fun setupReportCardStudentAdapter() {
        mReportCardStudentAdapter = ReportCardStudentAdapter(this@ReportCardStudentActivity, mReportCard)
        binding.rvReportCardStudent.adapter = mReportCardStudentAdapter
        mReportCardStudentAdapter?.notifyDataSetChanged()

    }

    private fun setupClassAdapter() {
        mLectureClassAdapter = StudentClassAdapter(applicationContext, mLectureClassModel, this)
        binding.rvReportClass.adapter = mLectureClassAdapter
        mLectureClassAdapter?.notifyDataSetChanged()

    }

    private fun apiClassList() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@ReportCardStudentActivity).myApi.api_ReportClassList()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<StudentClassItem>>() {}.type
                        val mList: List<StudentClassItem> = Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mLectureClassModel.clear()
                        mLectureClassModel.addAll(mList)


                        if (mLectureClassModel.isNotEmpty()) {
                            mClassId = mLectureClassModel[0].instituteClassId
                            mClassName = mLectureClassModel[0].className
                            apiReportCardList(mLectureClassModel[0].instituteClassId)
                            mLectureClassAdapter?.notifyDataSetChanged()
                        }
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@ReportCardStudentActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun apiReportCardList(mClassId: Int) {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@ReportCardStudentActivity).myApi.api_ReportCardList(mClassId);

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {

                        val listType = object : TypeToken<List<ReportCard>>() {}.type
                        val mList: List<ReportCard> = Gson().fromJson(responseBody, listType)

                        mReportCard.clear()
                        mReportCard.addAll(mList)


                        if (!mReportCard.isNullOrEmpty()) {
                            binding.rvReportCardStudent.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                            mReportCardStudentAdapter?.notifyDataSetChanged()
                        }else{
                            binding.rvReportCardStudent.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    }else{
                        binding.rvReportCardStudent.visibility = View.GONE
                        binding.tvNoData.visibility = View.VISIBLE
                    }

                } else {
                    binding.rvReportCardStudent.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@ReportCardStudentActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_report_card)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        supportActionBar!!.title = (Html.fromHtml("<font color=\"#060B13\">" + resources.getString(R.string.title_report_card) + "</font>"));

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

    override fun reportCardStudentClassClickCallback(
        classId: Int,
        position: Int,
        className: String
    ) {
        mClassId = classId
        mClassName = className
        apiReportCardList(mClassId)
    }
}