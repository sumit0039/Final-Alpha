package com.softwill.alpha.institute.online_exam.teacher.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.common.adapter.LectureClassAdapter
import com.softwill.alpha.common.adapter.LectureSubjectAdapterNew
import com.softwill.alpha.common.model.LectureClassModel
import com.softwill.alpha.databinding.ActivityTeacherExamBinding
import com.softwill.alpha.institute.library.activity.SavedActivity
import com.softwill.alpha.institute.online_exam.teacher.adapter.ExamTypeAdapter
import com.softwill.alpha.institute.online_exam.teacher.adapter.TeacherExamCompletedAdapter
import com.softwill.alpha.institute.online_exam.teacher.adapter.TeacherExamCreatedAdapter
import com.softwill.alpha.institute.online_exam.teacher.model.CreateOnlineExam
import com.softwill.alpha.institute.online_exam.teacher.model.ExamType
import com.softwill.alpha.institute.online_exam.teacher.model.TeacherCompletedExam
import com.softwill.alpha.institute.online_exam.teacher.model.TeacherOnlineExam
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeacherExamActivity : AppCompatActivity(), LectureClassAdapter.LectureClassCallbackInterface,
    LectureSubjectAdapterNew.LectureSubjectCallbackInterface,
    ExamTypeAdapter.ExamTypeAdapterCallbackInterface,
    TeacherExamCreatedAdapter.TeacherExamCreatedAdapterCallbackInterface {


    var progressDialog: Dialog? = null

    private lateinit var binding: ActivityTeacherExamBinding
    var mTeacherExamCreatedAdapter: TeacherExamCreatedAdapter? = null
    var mTeacherExamCompletedAdapter: TeacherExamCompletedAdapter? = null


    var mExamTypeAdapter: ExamTypeAdapter? = null
    var mLectureClassAdapter: LectureClassAdapter? = null
    var mLectureSubjectAdapter: LectureSubjectAdapterNew? = null

    private var rvLectureSubject: RecyclerView? = null
    private var rvExamType: RecyclerView? = null

    val mLectureClassModel = ArrayList<LectureClassModel>()
    val mExamTypeList = ArrayList<ExamType>()

    var mClassId: Int = -1
    var mSubjectId: Int = -1
    var mExamTypeId: Int = -1

    val mTeacherOnlineExam = java.util.ArrayList<TeacherOnlineExam>()
    val mTeacherCompletedExam = java.util.ArrayList<TeacherCompletedExam>()

    var isCompletedOn: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, com.softwill.alpha.R.layout.activity_teacher_exam)

        setupBack()
        onClickListener()
        setupAdapter()

        apiTeacherOngoingExam("", "")

    }


    private fun setupAdapter() {

        mTeacherExamCompletedAdapter = TeacherExamCompletedAdapter(this, mTeacherCompletedExam)
        binding.rvCompletedExam.adapter = mTeacherExamCompletedAdapter
        mTeacherExamCompletedAdapter!!.notifyDataSetChanged()


        mTeacherExamCreatedAdapter = TeacherExamCreatedAdapter(this, mTeacherOnlineExam, this@TeacherExamActivity)
        binding.rvCreatedExam.adapter = mTeacherExamCreatedAdapter
        mTeacherExamCreatedAdapter!!.notifyDataSetChanged()
        progressDialog?.dismiss()
    }


    private fun addExamBottomSheet() {

        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_add_exam, null)

        val btnStartCreating = view.findViewById<Button>(R.id.btnStartCreating)
        val rvLectureClass = view.findViewById<RecyclerView>(R.id.rvLectureClass)
        rvLectureSubject = view.findViewById(R.id.rvLectureSubject)
        rvExamType = view.findViewById(R.id.rvExamType)

        val etDate = view.findViewById<EditText>(R.id.etDate)
        val etTotalMarks = view.findViewById<EditText>(R.id.etTotalMarks)
        val etStartTime = view.findViewById<EditText>(R.id.etStartTime)
        val etEndTime = view.findViewById<EditText>(R.id.etEndTime)

        mLectureClassAdapter =
            LectureClassAdapter(this@TeacherExamActivity, mLectureClassModel, this)
        rvLectureClass.adapter = mLectureClassAdapter
        mLectureClassAdapter?.notifyDataSetChanged()


        mExamTypeAdapter =
            ExamTypeAdapter(this@TeacherExamActivity, mExamTypeList, this)
        rvExamType!!.adapter = mExamTypeAdapter
        mExamTypeAdapter?.notifyDataSetChanged()

        apiClassSubjects()
        apiExamType()

        etDate.setOnClickListener {
            UtilsFunctions().showDatePicker(this, etDate, false)
        }


        etStartTime.setOnClickListener {
            UtilsFunctions().showTimePicker(this, etStartTime)
        }

        etEndTime.setOnClickListener {
            if (!etStartTime.text.toString().isEmpty()) {

                UtilsFunctions().showEndTimePicker(this, etEndTime, etStartTime.text.toString())
            } else {
                UtilsFunctions().showToast(this@TeacherExamActivity, "Please select start time first")
            }
        }


        btnStartCreating.setOnClickListener {

            if (etDate.text.toString().isEmpty()) {
                UtilsFunctions().showToast(this, "Select date")
            } else if (etStartTime.text.toString().isEmpty()) {
                UtilsFunctions().showToast(this, "Select start time")
            } else if (etEndTime.text.toString().isEmpty()) {
                UtilsFunctions().showToast(this, "Select end time")
            } else if (etTotalMarks.text.toString().isEmpty()) {
                UtilsFunctions().showToast(this, "Enter total marks")
            } else if ((etTotalMarks.text.toString()).toInt() == 0) {
                UtilsFunctions().showToast(this, "Total marks can't be zero")
            } else {

                val createOnlineExam = CreateOnlineExam(
                    examTypeId = mExamTypeId,
                    classId = mClassId,
                    subjectId = mSubjectId,
                    examDate = etDate.text.toString().trim(),
                    startTime = etStartTime.text.toString().trim(),
                    endTime = etEndTime.text.toString().trim(),
                    totalMarks = etTotalMarks.text.toString().toInt()
                )


                val intent = Intent(this, CreateExamActivity::class.java)
                intent.putExtra("mCreateOnlineExam", createOnlineExam)
                startActivity(intent)
                dialog.dismiss()

            }


        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }


    private fun addFilterBottomSheet() {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_add_filter, null)


        val rvLectureClass = view.findViewById<RecyclerView>(R.id.rvLectureClass)
        rvLectureSubject = view.findViewById(R.id.rvLectureSubject)
        val btnReset = view.findViewById<Button>(R.id.btnReset)
        val btnApply = view.findViewById<Button>(R.id.btnApply)


        mLectureClassAdapter =
            LectureClassAdapter(this@TeacherExamActivity, mLectureClassModel, this)
        rvLectureClass.adapter = mLectureClassAdapter
        mLectureClassAdapter?.notifyDataSetChanged()


        apiClassSubjects()


        btnApply.setOnClickListener {
            dialog.dismiss()
            apiTeacherOngoingExam(mClassId.toString(), mSubjectId.toString())
        }

        btnReset.setOnClickListener {
            dialog.dismiss()
            apiTeacherOngoingExam("", "")
        }




        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }




    private fun onClickListener() {
        binding.fab.setOnClickListener {
            addExamBottomSheet()
        }

        binding.clFilter.setOnClickListener {
            addFilterBottomSheet()
        }


        binding.tvCreated.setOnClickListener {

            binding.tvCreated.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.blue
                )
            )
            binding.tvCreated.setBackgroundResource(R.drawable.bg_rounded_3_selected)
            binding.tvCompleted.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.black
                )
            )
            binding.tvCompleted.setBackgroundResource(R.drawable.bg_rounded_3)


            binding.rvCreatedExam.visibility = View.VISIBLE
            binding.rvCompletedExam.visibility = View.GONE

            binding.clFilter.visibility = View.VISIBLE
            isCompletedOn = false

            apiTeacherOngoingExam("", "")
        }

        binding.tvCompleted.setOnClickListener {

            binding.tvCompleted.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.blue
                )
            )
            binding.tvCompleted.setBackgroundResource(R.drawable.bg_rounded_3_selected)
            binding.tvCreated.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.black
                )
            )
            binding.tvCreated.setBackgroundResource(R.drawable.bg_rounded_3)


            binding.rvCreatedExam.visibility = View.GONE
            binding.rvCompletedExam.visibility = View.VISIBLE

            isCompletedOn = true

            binding.clFilter.visibility = View.GONE

            apiTeacherCompletedExam()

        }


    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.exam)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        supportActionBar!!.title = (Html.fromHtml("<font color=\"#060B13\">" + resources.getString(R.string.exam) + "</font>"));

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                return true
            }

            com.softwill.alpha.R.id.menu_save -> {
                val intent = Intent(applicationContext, SavedActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun lectureClassClickCallback(classId: Int, position: Int, subjectId: Int, className :String) {
        mClassId = classId
        setSubjectAdapter(position, subjectId)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setSubjectAdapter(position: Int, subjectId: Int) {
        mSubjectId = subjectId
        mLectureSubjectAdapter = LectureSubjectAdapterNew(
            this@TeacherExamActivity, mLectureClassModel[position].class_subjects, this
        )
        rvLectureSubject?.adapter = mLectureSubjectAdapter
        mLectureSubjectAdapter?.notifyDataSetChanged()
    }

    override fun lectureSubjectClickCallback(subjectId: Int, position: Int) {
        mSubjectId = subjectId

    }

    private fun apiClassSubjects() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@TeacherExamActivity).myApi.api_ClassSubjects()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<LectureClassModel>>() {}.type
                        val mList: List<LectureClassModel> = Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mLectureClassModel.clear()
                        mLectureClassModel.addAll(mList)


                        if (mLectureClassModel.isNotEmpty()) {
                            mClassId = mLectureClassModel[0].classId
                            mLectureClassAdapter?.notifyDataSetChanged()

                            if (mLectureClassModel[0].class_subjects.isNotEmpty()) {
                                setSubjectAdapter(
                                    0, mLectureClassModel[0].class_subjects[0].subjectId
                                )

                            }

                        }
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@TeacherExamActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun apiExamType() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@TeacherExamActivity).myApi.api_ExamType()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<ExamType>>() {}.type
                        val mList: List<ExamType> = Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mExamTypeList.clear()
                        mExamTypeList.addAll(mList)


                        if (mExamTypeList.isNotEmpty()) {
                            mExamTypeId = mExamTypeList[0].id
                            mExamTypeAdapter?.notifyDataSetChanged()

                        }
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@TeacherExamActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun examTypeClickCallback(examId: Int, position: Int) {
        mExamTypeId = examId
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun apiTeacherOngoingExam(classId: String, subjectId: String) {
//        progressDialog = UtilsFunctions().showCustomProgressDialog(this@TeacherExamActivity)
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@TeacherExamActivity).myApi.api_TeacherOngoingExam(classId, subjectId)

        call.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
//                    progressDialog?.dismiss()
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<TeacherOnlineExam>>() {}.type
                        val mList: List<TeacherOnlineExam> =
                            Gson().fromJson(responseBody, listType)

                        mTeacherOnlineExam.clear()
                        mTeacherOnlineExam.addAll(mList)

                        if (mTeacherOnlineExam.isNotEmpty()) {
                            mTeacherExamCreatedAdapter?.notifyDataSetChanged()
                            binding.rvCreatedExam.visibility = View.VISIBLE
                            binding.rvCompletedExam.visibility = View.GONE

                        } else {
                            binding.rvCreatedExam.visibility = View.GONE
                            binding.rvCompletedExam.visibility = View.GONE
                        }
                    }

                } else {
//                    progressDialog?.dismiss()
                    binding.rvCreatedExam.visibility = View.GONE
                    binding.rvCompletedExam.visibility = View.GONE
                    UtilsFunctions().handleErrorResponse(response, this@TeacherExamActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                progressDialog?.dismiss()
                t.printStackTrace()

            }
        })
    }


    private fun apiTeacherCompletedExam() {
        progressDialog = UtilsFunctions().showCustomProgressDialog(this@TeacherExamActivity)
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@TeacherExamActivity).myApi. api_TeacherCompletedExam()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<TeacherCompletedExam>>() {}.type
                        val mList: List<TeacherCompletedExam> =
                            Gson().fromJson(responseBody, listType)

                        mTeacherCompletedExam.clear()
                        mTeacherCompletedExam.addAll(mList)


                        if (mTeacherCompletedExam.isNotEmpty()) {
                            mTeacherExamCompletedAdapter?.notifyDataSetChanged()
                            binding.rvCreatedExam.visibility = View.GONE
                            binding.rvCompletedExam.visibility = View.VISIBLE
                            progressDialog?.dismiss()
                        } else {
                            binding.rvCreatedExam.visibility = View.GONE
                            binding.rvCompletedExam.visibility = View.GONE
                            progressDialog?.dismiss()
                        }
                        progressDialog?.dismiss()
                    }
                    progressDialog?.dismiss()
                } else {
                    binding.rvCreatedExam.visibility = View.GONE
                    binding.rvCompletedExam.visibility = View.GONE
                    UtilsFunctions().handleErrorResponse(response, this@TeacherExamActivity)
                    progressDialog?.dismiss()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()

                progressDialog?.dismiss()
            }
        })
    }

    override fun deleteExamCallback(position: Int, id: Int) {
        apiDeleteTeacherExam(id, position)
    }

    private fun apiDeleteTeacherExam(id: Int, position: Int) {
        progressDialog = UtilsFunctions().showCustomProgressDialog(this@TeacherExamActivity)

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@TeacherExamActivity).myApi.api_DeleteTeacherExam(
                id
            )

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        var message = responseObject.getString("message");

                        if (message == "Deleted successfully") {
                            mTeacherExamCreatedAdapter?.removeItem(position)
                        }

                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(this@TeacherExamActivity, responseObject.getString("error"))
                    }
                    progressDialog?.dismiss()
                } else {
                    progressDialog?.dismiss()
                    UtilsFunctions().handleErrorResponse(response, this@TeacherExamActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog?.dismiss()
                t.printStackTrace()
            }
        })
    }

    override fun onResume() {
        apiTeacherOngoingExam("", "")
        super.onResume()
    }

}