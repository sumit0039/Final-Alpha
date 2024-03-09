package com.softwill.alpha.institute.attendance.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.common.adapter.LectureClassAdapter
import com.softwill.alpha.common.adapter.LectureSubjectAdapterNew
import com.softwill.alpha.common.model.LectureClassModel
import com.softwill.alpha.databinding.ActivityAttendanceTeacherBinding
import com.softwill.alpha.institute.attendance.adapter.AttendanceTeacherAdapter
import com.softwill.alpha.institute.attendance.model.Attendance
import com.softwill.alpha.institute.attendance.model.GetStudentAttendanceListItem
import com.softwill.alpha.institute.attendance.model.Student
import com.softwill.alpha.institute.attendance.model.StudentAttendanceList
import com.softwill.alpha.institute.attendance.model.TeacherStudentList
import com.softwill.alpha.institute.attendance.model.UpdateStudentsAttendance
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import com.softwill.alpha.utils.YourPreference
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date

class AttendanceTeacherActivity : AppCompatActivity(), AttendanceTeacherAdapter.StudentAbsentPresentAdapterCallbackInterface,
    LectureClassAdapter.LectureClassCallbackInterface, LectureSubjectAdapterNew.LectureSubjectCallbackInterface {

    private lateinit var binding: ActivityAttendanceTeacherBinding
    var mLectureClassAdapter: LectureClassAdapter? = null
    private var mDelayHandler: Handler? = null
    var mAttendanceTeacherAdapter: AttendanceTeacherAdapter? = null
    private val mStudentList = mutableListOf<Student>()
    private val mGetStudentAttendanceListItem = ArrayList<GetStudentAttendanceListItem>()
    var mLectureSubjectAdapter: LectureSubjectAdapterNew? = null
    private var rvClasses: RecyclerView? = null

    val mLectureClassModel = java.util.ArrayList<LectureClassModel>()
    private var teacherStudentList: TeacherStudentList? = null
    private var studentAttendanceList: StudentAttendanceList? = null

    private var updateStudentsAttendance: UpdateStudentsAttendance?=null
    private val attendances = mutableListOf<Attendance>()
    //    private val mStudent = mutableListOf<Student>()
    val attendance:Attendance?=null

    var mClassId: Int = -1
    var classId: Int = -1
    var mSubjectId: Int = -1
    var subjectId: Int = -1
    var mUserId: Int = 218
    @SuppressLint("SimpleDateFormat")
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    private val currentDate = sdf.format(Date())
    private var yourPreference:YourPreference?=null

    var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.softwill.alpha.R.layout.activity_attendance_teacher)
        yourPreference = YourPreference(applicationContext)
//        mUserId = yourPreference!!.getData(Constant.userId).toInt()
//        Toast.makeText(this,yourPreference!!.getData(Constant.userId),Toast.LENGTH_LONG).show()
        setupBack()
        progressDialog = UtilsFunctions().showCustomProgressDialog(this@AttendanceTeacherActivity)

//        setupSwipeListener()
//        setupAdapter()
        apiClassSubjects()


        binding.filterClass.setOnClickListener {
            addClassBottomSheet()
        }

        binding.submitAttendanceList.setOnClickListener {
            when {
                updateStudentsAttendance!=null && updateStudentsAttendance!!.attendances.isNotEmpty() -> {
                    progressDialog = UtilsFunctions().showCustomProgressDialog(this@AttendanceTeacherActivity)
                    apiUpdateStudentAttendance(updateStudentsAttendance!!)
                }
            }
        }

    }

    /*private fun setupSwipeListener() {
        mDelayHandler = Handler()
        binding.swiperefresh.setColorSchemeColors(resources.getColor(R.color.blue))
        binding.swiperefresh.setOnRefreshListener {
            mDelayHandler!!.postDelayed(mRunnable, Constant.SWIPE_DELAY)
        }

    }*/

    /*private val mRunnable: Runnable = Runnable {
        binding.swiperefresh.isRefreshing = false
        apiGetStudentAttendance(classId,subjectId,currentDate)
        apiAttendanceList()
        Toast.makeText(applicationContext, "Updated!!", Toast.LENGTH_SHORT).show()
    }*/

    private fun setupAdapter() {

        mAttendanceTeacherAdapter = AttendanceTeacherAdapter(this, mStudentList, mGetStudentAttendanceListItem,this)
        binding.rvAttendance.adapter = mAttendanceTeacherAdapter
        mAttendanceTeacherAdapter!!.notifyDataSetChanged()

    }

    private fun setupClassAdapter() {

        mLectureClassAdapter = LectureClassAdapter(this@AttendanceTeacherActivity, mLectureClassModel, this)
        rvClasses!!.adapter = mLectureClassAdapter
        mLectureClassAdapter?.notifyDataSetChanged()
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

    @SuppressLint("SimpleDateFormat", "SuspiciousIndentation")
    override fun lectureClassClickCallback(classId: Int, position: Int, subjectId: Int, className :String) {
        mClassId = classId
        mSubjectId = subjectId
        when {
            mClassId!=-1 && mSubjectId!=-1 -> {
                apiGetStudentAttendance(mClassId,mSubjectId,currentDate)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun apiGetStudentAttendance(mClassId: Int, mSubjectId: Int, currentDate: String) {

        val call: Call<ResponseBody> = RetrofitClient.getInstance(this@AttendanceTeacherActivity).myApi.api_GetStudentAttendance(mClassId,mSubjectId,currentDate);

        call.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()

                    if (responseBody!!.isNotEmpty()) {
                        Log.e(TAG, "OnResponse_Api_GetStudentAttendance: ${responseBody.toString()}")

                        val listType = object : TypeToken<ArrayList<GetStudentAttendanceListItem>>() {}.type
                        if(listType.toString().isNotEmpty()) {
                            val mList: ArrayList<GetStudentAttendanceListItem> = Gson().fromJson(responseBody, listType)
//                            Toast.makeText(this@AttendanceTeacherActivity,mList.toString(), Toast.LENGTH_LONG).show()
                            mGetStudentAttendanceListItem.clear()
                            mGetStudentAttendanceListItem.addAll(mList)
                        }
                    } else {
                        Log.e(TAG, "OnResponse_Api_GetStudentAttendance: ${responseBody.toString()}")
                    }

                } else {
                    UtilsFunctions().handleErrorResponse(response, this@AttendanceTeacherActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })

    }

    private fun apiAttendanceList() {

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@AttendanceTeacherActivity).myApi.api_AttandanceList(mUserId,true);

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    Log.e(TAG, "onResponse: ${responseBody.toString()}")

                    if (!responseBody.isNullOrEmpty()) {

                        val listType = object : TypeToken<StudentAttendanceList>() {}.type
                        val mList: StudentAttendanceList = Gson().fromJson(responseBody, listType)

                        mStudentList.clear()
                        mStudentList.addAll(mList.students)
                        if (mStudentList.isNotEmpty()) {

                          /*  if(mGetStudentAttendanceListItem.size>0 && mList.students.isNotEmpty()) {
                                for (student in mList.students){
                                    for (attendence in mGetStudentAttendanceListItem){

                                        if(student.studentId == attendence.studentId){
                                            student.present=attendence.present
                                        }else{
                                            student.present=0
                                        }

                                        student.User=student.User
                                        if(!student.attendancePercentage.isNullOrEmpty()){
                                            student.attendancePercentage=student.attendancePercentage
                                        }else{
                                            student.attendancePercentage="0"
                                        }
                                        student.firstName=student.firstName
                                        student.lastName=student.lastName
                                        if(!student.rollNumber.isNullOrEmpty()){
                                            student.rollNumber=student.rollNumber
                                        }else {
                                            student.rollNumber = ""
                                        }
                                        student.studentId=student.studentId
                                        student.userId=student.userId
                                        if(student.userName.isNotEmpty()){
                                            student.userName=student.userName
                                        } else{
                                            student.userName=""
                                        }

                                    }
                                    mStudentList.add(student)
                                }
                            }
                            else{
                                for (student in mList.students){
                                    student.present=0
                                    student.User=student.User
                                    if(!student.attendancePercentage.isNullOrEmpty()){
                                        student.attendancePercentage=student.attendancePercentage
                                    }else{
                                        student.attendancePercentage="0"

                                    }
                                    student.firstName=student.firstName
                                    student.lastName=student.lastName
                                    if(!student.rollNumber.isNullOrEmpty()){
                                        student.rollNumber=student.rollNumber
                                    }else {
                                        student.rollNumber = ""
                                    }
                                    student.studentId=student.studentId
                                    student.userId=student.userId
                                    if(student.userName.isNotEmpty()){
                                        student.userName=student.userName
                                    } else{
                                        student.userName=""
                                    }
                                    mStudentList.add(student)
                                }
                            }
*/
                            binding.rvAttendance.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                            mAttendanceTeacherAdapter = AttendanceTeacherAdapter(this@AttendanceTeacherActivity, mList.students, mGetStudentAttendanceListItem,this@AttendanceTeacherActivity)
                            binding.rvAttendance.adapter = mAttendanceTeacherAdapter
//                            mAttendanceTeacherAdapter!!.notifyDataSetChanged()
                            progressDialog?.dismiss()
                        } else {
                            binding.rvAttendance.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                            progressDialog?.dismiss()
                        }

                    } else {
                        binding.rvAttendance.visibility = View.GONE
                        binding.tvNoData.visibility = View.VISIBLE
                        progressDialog?.dismiss()
                    }

                } else {
                    binding.rvAttendance.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    progressDialog?.dismiss()
                    UtilsFunctions().handleErrorResponse(response, this@AttendanceTeacherActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })

    }

    private fun apiUpdateStudentAttendance(updateStudentsAttendance: UpdateStudentsAttendance) {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@AttendanceTeacherActivity).myApi.api_UpdateStudentsAttendanceList(updateStudentsAttendance)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson.toString())

                    if (responseObject.has("message")) {
                        val message = responseObject.getString("message");

//                        if (message == "Added successfully") {

                        UtilsFunctions().showToast(this@AttendanceTeacherActivity, message)
                        apiGetStudentAttendance(classId,subjectId,currentDate)
                        apiAttendanceList()
//                        }

                        binding.submitAttendanceList.visibility=View.GONE
                    } else if (responseObject.has("error")) {
                        progressDialog?.dismiss()
                        UtilsFunctions().showToast(
                            this@AttendanceTeacherActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    progressDialog?.dismiss()
                    UtilsFunctions().handleErrorResponse(response, this@AttendanceTeacherActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog?.dismiss()
                t.printStackTrace()
            }
        })

    }



    private fun apiClassSubjects() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@AttendanceTeacherActivity).myApi.api_ClassSubjects()

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
                            classId = mLectureClassModel[0].classId
                            subjectId = mLectureClassModel[0].class_subjects[0].subjectId
//                            Toast.makeText(this@AttendanceTeacherActivity, subjectId.toString(), Toast.LENGTH_LONG).show()
                            mLectureClassAdapter?.notifyDataSetChanged()
                            apiGetStudentAttendance(classId,subjectId,currentDate)
                            apiAttendanceList()
                            if (mLectureClassModel[0].class_subjects.isNotEmpty()) {
                                setSubjectAdapter(0, mLectureClassModel[0].class_subjects[0].subjectId)
                            }
                        }
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@AttendanceTeacherActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setSubjectAdapter(position: Int, subjectId: Int) {
        mSubjectId = subjectId
        mLectureSubjectAdapter = LectureSubjectAdapterNew(
            this@AttendanceTeacherActivity, mLectureClassModel[position].class_subjects, this
        )
        binding.rvClass.adapter = mLectureSubjectAdapter
        mLectureSubjectAdapter?.notifyDataSetChanged()

//        apiGetStudentAttendance(mClassId,mSubjectId,currentDate)


    }

    override fun lectureSubjectClickCallback(subjectId: Int, position: Int) {
        mSubjectId = subjectId
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged", "MissingInflatedId")
    private fun addClassBottomSheet() {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.layout_class_rv, null)

        val rvLectureClass = view.findViewById<RecyclerView>(R.id.rvLectureClass)

        mLectureClassAdapter = LectureClassAdapter(this@AttendanceTeacherActivity, mLectureClassModel, this)
        rvLectureClass.adapter = mLectureClassAdapter
        mLectureClassAdapter?.notifyDataSetChanged()

        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()


    }

    override fun studentAbsentPresentCallback(attendance: Attendance, student: Student) {

//        Toast.makeText(this@AttendanceTeacherActivity,student.toString(),Toast.LENGTH_LONG).show()
        attendance.let { attendances.add(it) }
        updateStudentsAttendance = UpdateStudentsAttendance(attendances,mClassId,mSubjectId)

        if(updateStudentsAttendance!!.attendances.isNotEmpty()){
            binding.submitAttendanceList.visibility = View.VISIBLE
        }

        /*  for (i in 0 until updateStudentsAttendance!!.attendances.size()) {
              if (updateStudentsAttendance!!.attendances.get(i).equals("a")) {
                  updateStudentsAttendance!!.attendances.remove(list.get(i))
              }
          }*/

        /* if(updateStudentsAttendance !=null && attendances.isNotEmpty()){
              val attendancesList = listOf<Attendance>()
             for (a in attendances){
                 if(a.studentId == attendance.studentId){
                     attendances.remove(a)
                     a.present= attendance.present
                     a.studentId= attendance.studentId
                 }else{
                     attendance.let { attendances.add(it) }
                 }
                 attendances.add(a)
             }

         }else{

             attendance.let { attendances.add(it) }
             updateStudentsAttendance = UpdateStudentsAttendance(attendances,mClassId,mSubjectId)

             if(updateStudentsAttendance!!.attendances.isNotEmpty()){
                 binding.submitAttendanceList.visibility = View.VISIBLE
             }

         }
 */
        Log.e(TAG, "studentAbsentPresentCallback: ${updateStudentsAttendance.toString()}")
        Log.e(TAG, "studentAbsentPresentCallback: ${student.toString()}")
    }

}